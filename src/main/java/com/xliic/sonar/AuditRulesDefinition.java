package com.xliic.sonar;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xliic.sonar.model.Issues;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class AuditRulesDefinition implements RulesDefinition {
    protected static final String KEY = "audit";
    protected static final String NAME = "Audit";

    public static final String REPO_KEY = OpenApiLanguage.KEY + "-" + KEY;
    protected static final String REPO_NAME = OpenApiLanguage.KEY + "-" + NAME;

    private static final String PATH_TO_AUDIT_JSON = "/audit/audit.json";

    private static final Logger LOGGER = Loggers.get(AuditRulesDefinition.class);

    @Override
    public void define(Context context) {
        defineRulesForLanguage(context, REPO_KEY, REPO_NAME, OpenApiLanguage.KEY);
    }

    private void defineRulesForLanguage(Context context, String repositoryKey, String repositoryName,
            String languageKey) {
        InputStream auditJson = this.getClass().getResourceAsStream(PATH_TO_AUDIT_JSON);

        LOGGER.error("audit json: " + auditJson);

        NewRepository repository = context.createRepository(repositoryKey, languageKey).setName(repositoryName);

        ObjectMapper mapper = new ObjectMapper();
        try {
            Issues issues = mapper.readValue(auditJson, Issues.class);
            LOGGER.error("audit.json loaded");

            for (Issues.Entry<String, Issues.Issue> entry : issues.entrySet()) {
                String id = entry.getKey();
                Issues.Issue issue = entry.getValue();
                String title = issue.title.text.replace("<h1>", "").replace("</h1>", "");

                if (id.startsWith("v3-")) {
                    title = "OpenAPI v3: " + title;
                } else {
                    title = "OpenAPI v2: " + title;
                }

                RuleType ruleType = RuleType.BUG;
                if (issue.group.equals("security")) {
                    ruleType = RuleType.VULNERABILITY;
                }

                repository.createRule(RuleKey.of(REPO_KEY, id).rule()).setName(title)
                        .setHtmlDescription(entry.getValue().getHtml()).setType(ruleType).setActivatedByDefault(true);
            }
            LOGGER.error("audit.json done");

            repository.done();
        } catch (IOException ex) {
            LOGGER.error("audit.json error: " + ex);
        }
    }
}