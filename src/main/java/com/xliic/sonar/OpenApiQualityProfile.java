package com.xliic.sonar;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xliic.sonar.model.Issues;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class OpenApiQualityProfile implements BuiltInQualityProfilesDefinition {

    private static final String PATH_TO_AUDIT_JSON = "/audit/audit.json";
    private static final String REPO_KEY = OpenApiLanguage.KEY + "-security-audit";
    private static final Logger LOGGER = Loggers.get(OpenApiQualityProfile.class);

    @Override
    public void define(Context context) {
        try {
            NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("OpenAPI Security Audit",
                    OpenApiLanguage.KEY);
            profile.setDefault(true);

            InputStream auditJson = this.getClass().getResourceAsStream(PATH_TO_AUDIT_JSON);
            ObjectMapper mapper = new ObjectMapper();
            Issues issues = mapper.readValue(auditJson, Issues.class);

            for (Issues.Entry<String, Issues.Issue> entry : issues.entrySet()) {
                profile.activateRule(REPO_KEY, entry.getKey());
            }
            profile.done();
        } catch (IOException ex) {
            LOGGER.error("Failed to create OpenAPI Security Audit quality profile", ex);
        }
    }
}