package com.xliic.sonar;

import java.util.List;

import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import static java.util.Arrays.asList;

public class OpenApiLanguageProperties {
    public static final String FILE_SUFFIXES_KEY = "sonar.openapi.file.suffixes";
    public static final String FILE_SUFFIXES_DEFAULT_VALUE = ".json";

    private OpenApiLanguageProperties() {
        // only statics
    }

    public static List<PropertyDefinition> getProperties() {
        return asList(PropertyDefinition.builder(FILE_SUFFIXES_KEY).multiValues(true)
                .defaultValue(FILE_SUFFIXES_DEFAULT_VALUE).category("OpenAPI").subCategory("General")
                .name("File Suffixes").description("Comma-separated list of suffixes for files to analyze.").build());
    }
}