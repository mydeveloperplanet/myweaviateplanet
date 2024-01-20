package com.mydeveloperplanet.myweaviateplanet;

import com.google.gson.GsonBuilder;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.schema.model.WeaviateClass;

public class ManageCollections {

    public static void main(String[] args) {
        Config config = new Config("http", "localhost:8080");
        WeaviateClient client = new WeaviateClient(config);

        printCollectionDefinition(client);
        printCollectionObjects(client);
    }

    private static void printCollectionDefinition(WeaviateClient client) {
        String className = "CompilationAlbums";

        Result<WeaviateClass> result = client.schema().classGetter()
                .withClassName(className)
                .run();

        String json = new GsonBuilder().setPrettyPrinting().create().toJson(result.getResult());
        System.out.println(json);
    }

    private static void printCollectionObjects(WeaviateClient client) {
        Field song = Field.builder().name("title").build();

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName("CompilationAlbums")
                .withFields(song)
                .run();
        if (result.hasErrors()) {
            System.out.println(result.getError());
            return;
        }
        System.out.println(result.getResult());
    }

}
