package com.xliic.sonar;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xliic.sonar.model.Issues;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class OpenApiQualityProfile implements BuiltInQualityProfilesDefinition {

    private static final String PATH_TO_AUDIT_JSON = "/audit/audit.json";

    @Override
    public void define(Context context) {
        try {
            NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("OpenAPI Rules",
                    OpenApiLanguage.KEY);
            profile.setDefault(true);

            InputStream auditJson = this.getClass().getResourceAsStream(PATH_TO_AUDIT_JSON);
            ObjectMapper mapper = new ObjectMapper();
            Issues issues = mapper.readValue(auditJson, Issues.class);

            for (Issues.Entry<String, Issues.Issue> entry : issues.entrySet()) {
                profile.activateRule("openapi-audit", entry.getKey());
            }
            profile.done();
        } catch (IOException ex) {
            System.out.println("Exception creating profile: " + ex);
        }
    }
}