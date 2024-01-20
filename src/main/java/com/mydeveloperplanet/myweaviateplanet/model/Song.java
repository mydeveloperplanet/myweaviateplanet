package com.mydeveloperplanet.myweaviateplanet.model;

import io.weaviate.client.v1.graphql.query.fields.Field;

public class Song {

    public static String NAME = "Songs";

    public static Field[] getFields() {
        return new Field[]{Field.builder().name("producers").build(),
                Field.builder().name("song").build(),
                Field.builder().name("originalRelease").build(),
                Field.builder().name("year").build(),
                Field.builder().name("writers").build()};
    }
}
