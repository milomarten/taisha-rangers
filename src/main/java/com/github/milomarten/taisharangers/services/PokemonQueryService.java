package com.github.milomarten.taisharangers.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.milomarten.taisharangers.models.PokemonSearchParams;
import com.github.milomarten.taisharangers.models.graphql.operations.Equals;
import com.github.milomarten.taisharangers.models.graphql.operations.IsNull;
import com.github.milomarten.taisharangers.models.graphql.query.AggregateCount;
import com.github.milomarten.taisharangers.models.graphql.query.Query;
import com.github.milomarten.taisharangers.models.graphql.query.TypeWhere;
import com.github.milomarten.taisharangers.models.graphql.query.domain.*;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Service to query PokeAPI's GraphQL database, based on certain criteria.
 * A lot of this is a little janky, since PokeAPI does not expose their Schema, and I can't
 * find a tool that converts what I do have into POJOs. In the future, I'd want something more robust, particularly
 * with which fields are returned back. As of right now, the expectation is to use the regular
 * API to get fuller details about a Pokemon that was found this way.
 */
@Service
public class PokemonQueryService {
    private final HttpGraphQlClient graphQlClient;

    private final ObjectMapper om;

    public PokemonQueryService(HttpGraphQlClient graphQlClient, ObjectMapper om) {
        this.graphQlClient = graphQlClient;
        this.om = om.copy().disable(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature());
    }

    /**
     * Search for all the Pokemon that match the given parameters.
     * When used via the autowired service, the result of this function is cached for 24 hours after the first call.
     * If the call failed due to system error, the error is only cached for 5 minutes, to allow the backend time
     * to heal.
     * @param params The search params
     * @return The found Pokemon IDs and names matching the query
     */
    @Cacheable("pokemon-queries")
    public Mono<List<QLResult>> searchPokemon(PokemonSearchParams params) {
        return Mono.defer(() -> _searchPokemon(params))
                .cache(
                        (s) -> Duration.ofHours(24),
                        (e) -> Duration.ofMinutes(5),
                        () -> Duration.ofHours(24)
                );
    }

    private Mono<List<QLResult>> _searchPokemon(PokemonSearchParams params) {
        var pq = PokemonQuery.builder()
                .where(buildWhereFromParams(params))
                .build();

        try {
            return retrieve(pq)
                    .toEntity(new ParameterizedTypeReference<List<QLResult>>() {
                    })
                    .cache();

        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    private static PokemonWhere buildWhereFromParams(PokemonSearchParams params) {
        var where = PokemonWhere.builder();

        if (!params.isIncludeUnusual()) {
            where = where.isDefault(new Equals<>(true));
        }

        if (ObjectUtils.anyNotNull(params.getIsEvolved(), params.getLegendary(), params.getMinGeneration(), params.getMaxGeneration())) {
            where = where.specy(PokemonBySpeciesWhere.builder()
                .evolvesFromSpeciesId(params.getIsEvolved() == null ? null : new IsNull<>(!params.getIsEvolved()))
                .isLegendary(params.getLegendary() == null ? null : new Equals<>(params.getLegendary()))
                .generationId(GraphQLOperationUtils.range(params.getMinGeneration(), params.getMaxGeneration()))
                .evolutionChain(params.getEvolutionChain().isEmpty() ? null : EvolutionChainWhere.builder()
                        .speciesAggregate(PokemonBySpeciesAggregateWhere.builder()
                                .count(AggregateCount.builder()
                                        .predicate(new Equals<>(params.getEvolutionChain().getAsInt()))
                                        .build())
                                .build())
                        .build())
                .build());
        }

        if (!CollectionUtils.isEmpty(params.getTypes())) {
            var typesNormalized = params.getTypes().stream().map(String::toLowerCase).toList();
            where = where.type(PokemonByTypeWhere.builder()
                .type(TypeWhere.builder()
                        .name(params.getTypes() == null ? null : GraphQLOperationUtils.equalsOrIn(typesNormalized))
                        .build())
                .build());
        }

        if (!CollectionUtils.isEmpty(params.getAbilities())) {
            var abilitiesNormalized = params.getAbilities().stream().map(String::toLowerCase).toList();
            where = where.ability(PokemonByAbilityWhere.builder()
                .ability(AbilityWhere.builder()
                        .name(params.getAbilities() == null ? null : GraphQLOperationUtils.equalsOrIn(abilitiesNormalized))
                        .build())
                .build());
        }

        return where.build();
    }

    private GraphQlClient.RetrieveSpec retrieve(Query<?> query) throws JsonProcessingException {
        String whereClause = om.writeValueAsString(query.getWhere());
        String label = query.getLabel();

        var queryStr = String.format("""
                query pokemonQuery {
                    %s(where: %s) {
                        id,
                        name
                    }
                }
                """, label, whereClause);

        return graphQlClient.document(queryStr)
                .retrieve(label);
    }

    @Data
    public static class QLResult {
        int id;
        String name;
    }
}
