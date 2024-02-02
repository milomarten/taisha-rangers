package com.github.milomarten.taisharangers.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.milomarten.taisharangers.services.query.Equals;
import com.github.milomarten.taisharangers.services.query.model.PokemonByTypeWhere;
import com.github.milomarten.taisharangers.services.query.model.PokemonQuery;
import com.github.milomarten.taisharangers.services.query.model.PokemonWhere;
import com.github.milomarten.taisharangers.services.query.Query;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PokemonQueryService {
    private final HttpGraphQlClient graphQlClient;

    private final ObjectMapper om;

    public PokemonQueryService(HttpGraphQlClient graphQlClient, ObjectMapper om) {
        this.graphQlClient = graphQlClient;
        this.om = om.disable(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature());
    }
    public Mono<List<Integer>> searchPokemon() {
        var pq = PokemonQuery.builder()
                .where(PokemonWhere.builder()
                        .type(PokemonByTypeWhere.builder()
                                .typeId(new Equals<>(5))
                                .build())
                        .build())
                .build();
        try {
            return retrieve(pq)
                    .toEntity(new ParameterizedTypeReference<List<QLResult>>() {
                    })
                    .map(r -> r.stream().map(QLResult::getId).toList());
        } catch (JsonProcessingException e) {
            return Mono.error(e);
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
