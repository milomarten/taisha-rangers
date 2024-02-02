package com.github.milomarten.taisharangers.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.milomarten.taisharangers.models.PokemonSearchParams;
import com.github.milomarten.taisharangers.models.graphql.operations.In;
import com.github.milomarten.taisharangers.models.graphql.operations.IsNull;
import com.github.milomarten.taisharangers.models.graphql.operations.Operation;
import com.github.milomarten.taisharangers.models.graphql.query.AggregateCount;
import com.github.milomarten.taisharangers.models.graphql.query.TypeWhere;
import com.github.milomarten.taisharangers.models.graphql.query.domain.*;
import com.github.milomarten.taisharangers.models.graphql.query.Query;
import com.github.milomarten.taisharangers.models.graphql.operations.Equals;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PokemonQueryService {
    private final HttpGraphQlClient graphQlClient;

    private final ObjectMapper om;

    public PokemonQueryService(HttpGraphQlClient graphQlClient, ObjectMapper om) {
        this.graphQlClient = graphQlClient;
        this.om = om.copy().disable(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature());
    }

    @PostConstruct
    private void test() {
        searchPokemon(
                PokemonSearchParams.builder()
                        .types(List.of("ice"))
                        .legendary(false)
                        .evolutionChain(3)
                        .build()
        )
                .subscribe(System.out::println);
    }

    public Mono<Set<Integer>> searchPokemon(PokemonSearchParams params) {
        var pq = PokemonQuery.builder()
                .where(buildWhereFromParams(params))
                .build();

        try {
            return retrieve(pq)
                    .toEntity(new ParameterizedTypeReference<List<QLResult>>() {
                    })
                    .map(r -> r.stream().map(QLResult::getId).collect(Collectors.toSet()));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    private static PokemonWhere buildWhereFromParams(PokemonSearchParams params) {
        var where = PokemonWhere.builder()
                .isDefault(new Equals<>(true));

        if (ObjectUtils.anyNotNull(params.getIsEvolved(), params.getLegendary())) {
                where = where.specy(PokemonBySpeciesWhere.builder()
                    .evolvesFromSpeciesId(params.getIsEvolved() == null ? null : new IsNull<>(!params.getIsEvolved()))
                    .isLegendary(params.getLegendary() == null ? null : new Equals<>(params.getLegendary()))
                    .evolutionChain(params.getEvolutionChain() == 0 ? null : EvolutionChainWhere.builder()
                            .speciesAggregate(PokemonBySpeciesAggregateWhere.builder()
                                    .count(AggregateCount.builder()
                                            .predicate(new Equals<>(params.getEvolutionChain()))
                                            .build())
                                    .build())
                            .build())
                    .build());
        }

        if (ObjectUtils.anyNotNull(params.getTypes())) {
                where = where.type(PokemonByTypeWhere.builder()
                    .type(TypeWhere.builder()
                            .name(params.getTypes() == null ? null : listOperation(params.getTypes()))
                            .build())
                    .build());
        }

        if (ObjectUtils.anyNotNull(params.getAbility())) {
                where = where.ability(PokemonByAbilityWhere.builder()
                    .ability(AbilityWhere.builder()
                            .name(params.getAbility() == null ? null : listOperation(params.getAbility()))
                            .build())
                    .build());
        }

        return where.build();
    }

    private static <T> Operation<T> listOperation(List<T> l) {
        if (l.size() == 1) {
            return new Equals<>(l.get(0));
        } else {
            return new In<>(l);
        }
    }

    private GraphQlClient.RetrieveSpec retrieve(Query<?> query) throws JsonProcessingException {
        String whereClause = om.writeValueAsString(query.getWhere());
        String label = query.getLabel();

        var queryStr = String.format("""
                query pokemonQuery {
                    %s(where: %s) {
                        id
                    }
                }
                """, label, whereClause);

        return graphQlClient.document(queryStr)
                .retrieve(label);
    }

    @Data
    public static class QLResult {
        int id;
    }
}
