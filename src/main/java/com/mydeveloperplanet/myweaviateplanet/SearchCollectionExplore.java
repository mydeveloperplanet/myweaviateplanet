package com.mydeveloperplanet.myweaviateplanet;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.graphql.model.ExploreFields;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument;

public class SearchCollectionExplore {

    public static void main(String[] args) {
        askQuestion("on which album was \"adam raised a cain\" originally released?");
        askQuestion("what is the highest chart position of \"Greetings from Asbury Park, N.J.\" in the US?");
        askQuestion("what is the highest chart position of the album \"tracks\" in canada?");
        askQuestion("in which year was \"Highway Patrolman\" released?");
        askQuestion("who produced \"all or nothin' at all?\"");
    }

    private static void askQuestion(String question) {
        Config config = new Config("http", "localhost:8080");
        WeaviateClient client = new WeaviateClient(config);

        ExploreFields[] fields = new ExploreFields[]{
                ExploreFields.CERTAINTY,  // only supported if distance==cosine
                ExploreFields.DISTANCE,   // always supported
                ExploreFields.BEACON,
                ExploreFields.CLASS_NAME
        };

        NearTextArgument nearText = NearTextArgument.builder().concepts(new String[]{question}).build();

        Result<GraphQLResponse> result = client.graphQL().explore()
                .withFields(fields)
                .withNearText(nearText)
                .run();

        if (result.hasErrors()) {
            System.out.println(result.getError());
            return;
        }
        System.out.println(result.getResult());
    }
}
