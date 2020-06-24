package com.xliic.sonar;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

public class OpenApiLanguage extends AbstractLanguage {
    public static final String NAME = "OpenAPI";
    public static final String KEY = "openapi";

    private final Configuration config;

    public OpenApiLanguage(Configuration config) {
        super(KEY, NAME);
        this.config = config;
    }

    @Override
    public String[] getFileSuffixes() {
        return config.getStringArray(OpenApiLanguageProperties.FILE_SUFFIXES_KEY);
    }
}