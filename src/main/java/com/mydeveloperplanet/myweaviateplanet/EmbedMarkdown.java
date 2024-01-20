package com.mydeveloperplanet.myweaviateplanet;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocuments;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;

import dev.langchain4j.data.document.Document;
import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.WeaviateClass;

public class EmbedMarkdown {

    private static Map<String, String> documentNames = Map.of(
            "bruce_springsteen_list_of_songs_recorded.md", "Songs",
            "bruce_springsteen_discography_compilation_albums.md", "CompilationAlbums",
            "bruce_springsteen_discography_studio_albums.md", "StudioAlbums");

    public static void main(String[] args) {

        Config config = new Config("http", "localhost:8080");
        WeaviateClient client = new WeaviateClient(config);

        // Remove existing data
        Result<Boolean> deleteResult = client.schema().allDeleter().run();
        if (deleteResult.hasErrors()) {
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(deleteResult.getResult()));
        }

        List<Document> documents = loadDocuments(toPath("markdown-files"));

        for (Document document : documents) {

            // Split the document line by line
            String[] splittedDocument = document.text().split("\n");

            // split the header on | and remove the first item (the line starts with | and the first item is therefore empty)
            String[] tempSplittedHeader = splittedDocument[0].split("\\|");
            String[] splittedHeader = Arrays.copyOfRange(tempSplittedHeader,1, tempSplittedHeader.length);

            // Create the Weaviate collection, every item in the header is a Property
            ArrayList<Property> properties = new ArrayList<>();
            for (String splittedHeaderItem : splittedHeader) {
                Property property = Property.builder().name(splittedHeaderItem.strip()).build();
                properties.add(property);
            }

            WeaviateClass documentClass = WeaviateClass.builder()
                    .className(documentNames.get(document.metadata("file_name")))
                    .properties(properties)
                    .build();

            // Add the class to the schema
            Result<Boolean> collectionResult = client.schema().classCreator()
                    .withClass(documentClass)
                    .run();
            if (collectionResult.hasErrors()) {
                System.out.println("Creation of collection failed: " + documentNames.get(document.metadata("file_name")));
            }

            // Preserve only the rows containing data, the first two rows contain the header
            String[] dataOnly = Arrays.copyOfRange(splittedDocument, 2, splittedDocument.length);

            for (String documentLine : dataOnly) {
                // split a data row on | and remove the first item (the line starts with | and the first item is therefore empty)
                String[] tempSplittedDocumentLine = documentLine.split("\\|");
                String[] splittedDocumentLine = Arrays.copyOfRange(tempSplittedDocumentLine, 1, tempSplittedDocumentLine.length);

                // Every item becomes a property
                HashMap<String, Object> propertiesDocumentLine = new HashMap<>();
                int i = 0;
                for (Property property : properties) {
                    propertiesDocumentLine.put(property.getName(), splittedDocumentLine[i].strip());
                    i++;
                }

                Result<WeaviateObject> objectResult = client.data().creator()
                        .withClassName(documentNames.get(document.metadata("file_name")))
                        .withProperties(propertiesDocumentLine)
                        .run();
                if (objectResult.hasErrors()) {
                    System.out.println("Creaton of object failed: " + propertiesDocumentLine);
                }

                String json = new GsonBuilder().setPrettyPrinting().create().toJson(objectResult.getResult());
                System.out.println(json);
            }
        }
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = EmbedMarkdown.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
