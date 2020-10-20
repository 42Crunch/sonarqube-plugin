/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xliic.sonar.model.Issues;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class OpenApiQualityProfile implements BuiltInQualityProfilesDefinition {

    private static final String PATH_TO_AUDIT_JSON = "/audit/audit.json";
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

            for (Map.Entry<String, Issues.Issue> entry : issues.entrySet()) {
                profile.activateRule(AuditPlugin.REPO_KEY, entry.getKey());
            }
            profile.activateRule(AuditPlugin.REPO_KEY, "AuditError");
            profile.done();
        } catch (IOException ex) {
            LOGGER.error("Failed to create OpenAPI Security Audit quality profile", ex);
        }
    }
}