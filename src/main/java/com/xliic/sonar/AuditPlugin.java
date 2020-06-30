package com.xliic.sonar;

import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import static java.util.Arrays.asList;

public class AuditPlugin implements Plugin {
        public static final String REPO_NAME = "Security Audit";
        public static final String REPO_KEY = OpenApiLanguage.KEY + "-security-audit";

        public static final String EXCLUSIONS_KEY = "sonar.openapi.audit.exclusions";
        public static final String API_TOKEN_KEY = "sonar.openapi.audit.api.token";
        public static final String COLLECTION_NAME = "sonar.openapi.audit.collection.name";

        @Override
        public void define(Context context) {

                context.addExtensions(OpenApiLanguage.class, OpenApiQualityProfile.class);

                context.addExtensions(AuditMetrics.class, ComputeAuditScore.class, ComputeAuditSecurityScore.class,
                                ComputeAuditDataScore.class, AuditRulesDefinition.class, AuditSensor.class,
                                OpenApiExclusionsFileFilter.class);

                context.addExtensions(asList(

                                PropertyDefinition.builder(API_TOKEN_KEY).name("API Token").type(PropertyType.PASSWORD)
                                                .description("An API token that the plugin uses to authenticate to Security Audit.")
                                                .category("OpenAPI").build(),

                                PropertyDefinition.builder(COLLECTION_NAME).name("Collection Name")
                                                .type(PropertyType.STRING)
                                                .description("API collection where the discovered OpenAPI definitions are stored.")
                                                .category("OpenAPI").defaultValue("SonarQube").build(),

                                PropertyDefinition.builder(OpenApiLanguage.FILE_SUFFIXES_KEY)
                                                .defaultValue(OpenApiLanguage.FILE_SUFFIXES_DEFVALUE)
                                                .name("OpenAPI File suffixes")
                                                .description("OpenAPI File suffixes to analyze").category("OpenAPI")
                                                .multiValues(true).onQualifiers(Qualifiers.PROJECT).build(),

                                PropertyDefinition.builder(EXCLUSIONS_KEY).multiValues(true).name("OpenAPI exclusions")
                                                .description("List of file path patterns to be excluded from analysis of OpenAPI files.")
                                                .onQualifiers(Qualifiers.PROJECT).category("OpenAPI")
                                                .defaultValue("**/node_modules/**,package.json,package-lock.json")
                                                .build()

                ));

        }
}