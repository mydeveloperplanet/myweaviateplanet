package com.mydeveloperplanet.myweaviateplanet;

import java.util.Arrays;

import com.mydeveloperplanet.myweaviateplanet.model.CompilationAlbum;
import com.mydeveloperplanet.myweaviateplanet.model.Song;
import com.mydeveloperplanet.myweaviateplanet.model.StudioAlbum;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;

public class RagWeaviateLocalAi {

    public static void main(String[] args) {
        askQuestion(Song.NAME, Song.getFields(), "on which album was \"adam raised a cain\" originally released?", "");
        askQuestion(StudioAlbum.NAME, StudioAlbum.getFields(), "what is the highest chart position of \"Greetings from Asbury Park, N.J.\" in the US?", "");
        askQuestion(CompilationAlbum.NAME, CompilationAlbum.getFields(), "what is the highest chart position of the album \"tracks\" in canada?", "");
        askQuestion(Song.NAME, Song.getFields(), "in which year was \"Highway Patrolman\" released?", "");
        askQuestion(Song.NAME, Song.getFields(), "who produced \"all or nothin' at all?\"", "");
    }

    private static void askQuestion(String className, Field[] fields, String question, String extraInstruction) {
        Config config = new Config("http", "localhost:8081");
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

        ChatLanguageModel model = LocalAiChatModel.builder()
                .baseUrl("http://localhost:8080")
                .modelName("lunademo")
                .temperature(0.0)
                .build();

        String answer = model.generate(createPrompt(question, result.getResult().getData().toString(), extraInstruction));

        System.out.println(question);
        System.out.println(answer);
    }

    private static String createPrompt(String question, String inputData, String extraInstruction) {
        return "Answer the following question: " + question + "\n" +
                extraInstruction + "\n" +
                "Use the following data to answer the question: " + inputData;
    }
}
