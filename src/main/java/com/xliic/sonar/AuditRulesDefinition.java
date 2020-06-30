package com.xliic.sonar;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xliic.sonar.model.Issues;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;

public class AuditRulesDefinition implements RulesDefinition {

    private static final String PATH_TO_AUDIT_JSON = "/audit/audit.json";
    private static final Logger LOGGER = Loggers.get(AuditRulesDefinition.class);

    @Override
    public void define(Context context) {
        defineRulesForLanguage(context, AuditPlugin.REPO_KEY, AuditPlugin.REPO_NAME, OpenApiLanguage.KEY);
    }

    private void defineRulesForLanguage(Context context, String repositoryKey, String repositoryName,
            String languageKey) {
        InputStream auditJson = this.getClass().getResourceAsStream(PATH_TO_AUDIT_JSON);

        NewRepository repository = context.createRepository(repositoryKey, languageKey).setName(repositoryName);

        ObjectMapper mapper = new ObjectMapper();
        try {
            Issues issues = mapper.readValue(auditJson, Issues.class);

            for (Issues.Entry<String, Issues.Issue> entry : issues.entrySet()) {
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

            repository.done();
        } catch (IOException ex) {
            LOGGER.error("Failed to load OpenAPI rules defintion", ex);
        }
    }
}