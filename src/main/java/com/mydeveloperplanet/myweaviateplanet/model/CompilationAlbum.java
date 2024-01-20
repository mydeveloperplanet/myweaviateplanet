package com.mydeveloperplanet.myweaviateplanet.model;

import io.weaviate.client.v1.graphql.query.fields.Field;

public class CompilationAlbum {

    public static String NAME = "CompilationAlbums";

    public static Field[] getFields() {
        return new Field[]{Field.builder().name("nOR").build(),
                Field.builder().name("gER").build(),
                Field.builder().name("uK").build(),
                Field.builder().name("title").build(),
                Field.builder().name("sWE").build(),
                Field.builder().name("nLD").build(),
                Field.builder().name("nZ").build(),
                Field.builder().name("iRE").build(),
                Field.builder().name("uS").build(),
                Field.builder().name("cAN").build(),
                Field.builder().name("aUS").build()};
    }
}
