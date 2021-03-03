/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import java.io.IOException;
import java.util.Map;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import com.xliic.sonar.model.Issues;

public class AuditRulesDefinition implements RulesDefinition {

    private static final Logger LOGGER = Loggers.get(AuditRulesDefinition.class);

    @Override
    public void define(Context context) {
        defineRulesForLanguage(context, AuditPlugin.REPO_KEY, AuditPlugin.REPO_NAME, OpenApiLanguage.KEY);
    }

    private void defineRulesForLanguage(Context context, String repositoryKey, String repositoryName,
            String languageKey) {

        NewRepository repository = context.createRepository(repositoryKey, languageKey).setName(repositoryName);

        try {
            Issues issues = AuditKdb.loadIssues();

            for (Map.Entry<String, Issues.Issue> entry : issues.entrySet()) {
                String id = entry.getKey();
                Issues.Issue issue = entry.getValue();
                String title = issue.title.text.replace("<h1>", "").replace("</h1>", "");
                String[] tags = { null };
                if (id.startsWith("v3-")) {
                    tags[0] = "openapi-v3";
                } else {
                    tags[0] = "openapi-v2";
                }

                RuleType ruleType = RuleType.VULNERABILITY;
                if (issue.group.equals("oasconformance")) {
                    ruleType = RuleType.BUG;
                }

                repository.createRule(RuleKey.of(AuditPlugin.REPO_KEY, id).rule()).setName(title).addTags(tags)
                        .setHtmlDescription(entry.getValue().getHtml()).setType(ruleType).setActivatedByDefault(true);
            }

            // AuditError rule
            repository.createRule(RuleKey.of(AuditPlugin.REPO_KEY, "AuditError").rule())
                    .setName("Failed to perform Security Audit").setMarkdownDescription("Failed to run Security Audit")
                    .setType(RuleType.BUG).setActivatedByDefault(true).setSeverity(Severity.BLOCKER);

            // Missing Article rule
            repository.createRule(RuleKey.of(AuditPlugin.REPO_KEY, "MissingArticle").rule())
                    .setName("Failed to locate article")
                    .setMarkdownDescription(
                            "Whoops! Looks like there has been an oversight and we are missing a article for this issue. \n[Let us know](https://support.42crunch.com/) and we make sure to fix it.")
                    .setType(RuleType.BUG).setActivatedByDefault(true).setSeverity(Severity.BLOCKER);

            repository.done();
        } catch (IOException ex) {
            LOGGER.error("Failed to load OpenAPI rules defintion", ex);
        }
    }
}