package com.mydeveloperplanet.myweaviateplanet;

import java.util.Arrays;

import com.mydeveloperplanet.myweaviateplanet.model.CompilationAlbum;
import com.mydeveloperplanet.myweaviateplanet.model.Song;
import com.mydeveloperplanet.myweaviateplanet.model.StudioAlbum;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;

public class SearchCollectionNearTextExplore {

    public static void main(String[] args) {
        question1();
        question2();
        question3();
        question4();
        question4WithTypo();
        question5();
    }

    private static void question1() {
        askQuestion(Song.NAME, Song.getFields(), "on which album was \"adam raised a cain\" originally released?");
        askQuestion(StudioAlbum.NAME, StudioAlbum.getFields(), "on which album was \"adam raised a cain\" originally released?");
        askQuestion(CompilationAlbum.NAME, CompilationAlbum.getFields(), "on which album was \"adam raised a cain\" originally released?");
    }

    private static void question2(){
        askQuestion(Song.NAME, Song.getFields(), "what is the highest chart position of \"Greetings from Asbury Park, N.J.\" in the US?");
        askQuestion(StudioAlbum.NAME, StudioAlbum.getFields(), "what is the highest chart position of \"Greetings from Asbury Park, N.J.\" in the US?");
        askQuestion(CompilationAlbum.NAME, CompilationAlbum.getFields(), "what is the highest chart position of \"Greetings from Asbury Park, N.J.\" in the US?");
    }

    private static void question3() {
        askQuestion(Song.NAME, Song.getFields(), "what is the highest chart position of the album \"tracks\" in canada?");
        askQuestion(StudioAlbum.NAME, StudioAlbum.getFields(), "what is the highest chart position of the album \"tracks\" in canada?");
        askQuestion(CompilationAlbum.NAME, CompilationAlbum.getFields(), "what is the highest chart position of the album \"tracks\" in canada?");
    }

    private static void question4() {
        askQuestion(Song.NAME, Song.getFields(), "in which year was \"Highway Patrolman\" released?");
        askQuestion(StudioAlbum.NAME, StudioAlbum.getFields(), "in which year was \"Highway Patrolman\" released?");
        askQuestion(CompilationAlbum.NAME, CompilationAlbum.getFields(), "in which year was \"Highway Patrolman\" released?");
    }

    private static void question4WithTypo() {
        askQuestion(Song.NAME, Song.getFields(), "in which year was \"Higway Patrolman\" released?");
        askQuestion(StudioAlbum.NAME, StudioAlbum.getFields(), "in which year was \"Higway Patrolman\" released?");
        askQuestion(CompilationAlbum.NAME, CompilationAlbum.getFields(), "in which year was \"Higway Patrolman\" released?");
    }

    private static void question5() {
        askQuestion(Song.NAME, Song.getFields(), "who produced \"all or nothin' at all?\"");
        askQuestion(StudioAlbum.NAME, StudioAlbum.getFields(), "who produced \"all or nothin' at all?\"");
        askQuestion(CompilationAlbum.NAME, CompilationAlbum.getFields(), "who produced \"all or nothin' at all?\"");
    }

    private static void askQuestion(String className, Field[] fields, String question) {
        Config config = new Config("http", "localhost:8080");
        WeaviateClient client = new WeaviateClient(config);

        Field additional = Field.builder()
                .name("_additional")
                .fields(Field.builder().name("certainty").build(), // only supported if distance==cosine
                        Field.builder().name("distance").build()   // always supported
                ).build();
        Field[] allFields = Arrays.copyOf(fields, fields.length + 1);
        allFields[fields.length] = additional;

        // Embed the question
        NearTextArgument nearText = NearTextArgument.builder()
                .concepts(new String[]{question})
                .build();

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(className)
                .withFields(allFields)
                .withNearText(nearText)
                .withLimit(1)
                .run();

        if (result.hasErrors()) {
            System.out.println(result.getError());
            return;
        }
        System.out.println(result.getResult());
    }
}
