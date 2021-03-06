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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.xliic.sonar.json.JsonLanguage;
import com.xliic.sonar.json.JsonQualityProfile;
import com.xliic.sonar.json.JsonSensor;
import com.xliic.sonar.yaml.YamlLanguage;
import com.xliic.sonar.yaml.YamlQualityProfile;
import com.xliic.sonar.yaml.YamlSensor;

public class AuditPlugin implements Plugin {
        public static final String REPO_NAME = "Security Audit";
        public static final String REPO_KEY = OpenApiLanguage.KEY + "-security-audit";
        public static final String EXCLUSIONS_KEY = "sonar.openapi.audit.exclusions";
        public static final String API_TOKEN_KEY = "sonar.openapi.audit.api.token";
        public static final String PLATFORM_URL = "sonar.openapi.audit.platform.url";
        public static final String DISABLE = "sonar.openapi.audit.disable";
        static final String CATEGORY = "OpenAPI";

        private static final String XLIIC_SETTINGS_LANGUAGES = "/xliic/settings/languages";

        @Override
        public void define(Context context) {

                boolean loadAllLanguages = false;
                try {
                        loadAllLanguages = new BufferedReader(new InputStreamReader(
                                        this.getClass().getResourceAsStream(XLIIC_SETTINGS_LANGUAGES),
                                        StandardCharsets.UTF_8)).readLine().contains("all");
                } catch (IOException e) {
                        // ignore
                }

                context.addExtensions(OpenApiLanguage.class, OpenApiQualityProfile.class);

                context.addExtensions(AuditMetrics.class, ComputeAuditScore.class, ComputeAuditSecurityScore.class,
                                ComputeAuditDataScore.class, AuditRulesDefinition.class, AuditSensor.class);

                context.addExtensions(asList(

                                PropertyDefinition.builder(API_TOKEN_KEY).name("API token").type(PropertyType.PASSWORD)
                                                .description("The API token that the plugin uses to authenticate to API Security Audit")
                                                .category(CATEGORY).onQualifiers(Qualifiers.PROJECT).build(),

                                PropertyDefinition.builder(PLATFORM_URL).name("Platform URL").type(PropertyType.STRING)
                                                .description("The URL where you access 42Crunch Platform")
                                                .category(CATEGORY).defaultValue("https://platform.42crunch.com")
                                                .build(),

                                PropertyDefinition.builder(DISABLE).name("Switch audit off").type(PropertyType.BOOLEAN)
                                                .description("Conditionally switch Security Audit off on per-project basis")
                                                .category(CATEGORY).defaultValue("false")
                                                .onQualifiers(Qualifiers.PROJECT).build(),

                                PropertyDefinition.builder(EXCLUSIONS_KEY).multiValues(true).name("Excluded filepaths")
                                                .description("A list of directories and filepaths to be excluded from the audit")
                                                .onQualifiers(Qualifiers.PROJECT).category(CATEGORY)
                                                .defaultValue("**/node_modules/**,**/package.json,**/package-lock.json")
                                                .build(),

                                PropertyDefinition.builder(OpenApiLanguage.FILE_SUFFIXES_KEY)
                                                .defaultValue(OpenApiLanguage.FILE_SUFFIXES_DEFVALUE)
                                                .name("OpenAPI file suffixes")
                                                .description("File types included in the analysis").category(CATEGORY)
                                                .multiValues(true).onQualifiers(Qualifiers.PROJECT).build()

                ));

                if (loadAllLanguages) {
                        context.addExtensions(YamlLanguage.class, YamlQualityProfile.class, YamlSensor.class);
                        context.addExtensions(JsonLanguage.class, JsonQualityProfile.class, JsonSensor.class);
                }
        }
}
