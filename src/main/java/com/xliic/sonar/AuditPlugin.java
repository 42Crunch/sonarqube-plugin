/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import static java.util.Arrays.asList;

public class AuditPlugin implements Plugin {
        public static final String REPO_NAME = "Security Audit";
        public static final String REPO_KEY = "42crunch-security-audit";
        public static final String LANGUAGE_YAML = "yaml";

        public static final String EXCLUSIONS_KEY = "sonar.openapi.audit.exclusions";
        public static final String API_TOKEN_KEY = "sonar.openapi.audit.api.token";
        public static final String PLATFORM_URL = "sonar.openapi.audit.platform.url";
        public static final String DISABLE = "sonar.openapi.audit.disable";
        static final String CATEGORY = "OpenAPI";

        @Override
        public void define(Context context) {

                context.addExtensions(OpenApiQualityProfile.class, AuditMetrics.class, ComputeAuditScore.class,
                                ComputeAuditSecurityScore.class, ComputeAuditDataScore.class,
                                AuditRulesDefinition.class, AuditSensor.class, OpenApiExclusionsFileFilter.class);

                context.addExtensions(asList(

                                PropertyDefinition.builder(API_TOKEN_KEY).name("API token").type(PropertyType.PASSWORD)
                                                .description("The API token that the plugin uses to authenticate to API Contract Security Audit.")
                                                .category(CATEGORY).onQualifiers(Qualifiers.PROJECT).build(),

                                PropertyDefinition.builder(PLATFORM_URL).name("Platform URL").type(PropertyType.STRING)
                                                .description("42Crunch Platform URL.").category(CATEGORY)
                                                .defaultValue("https://platform.42crunch.com").build(),

                                PropertyDefinition.builder(DISABLE).name("Disable audit").type(PropertyType.BOOLEAN)
                                                .description("Disable API Contract Security Audit (use to conditionally disable audit on per-project basis)")
                                                .category(CATEGORY).defaultValue("false")
                                                .onQualifiers(Qualifiers.PROJECT).build(),

                                PropertyDefinition.builder(EXCLUSIONS_KEY).multiValues(true).name("Excluded filepaths")
                                                .description("A list of directories and file paths.")
                                                .onQualifiers(Qualifiers.PROJECT).category(CATEGORY)
                                                .defaultValue("**/node_modules/**,**/package.json,**/package-lock.json")
                                                .build()

                ));

        }
}
