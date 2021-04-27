/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import com.xliic.cicd.audit.AuditException;
import com.xliic.cicd.audit.Auditor;
import com.xliic.cicd.audit.client.Client;
import com.xliic.cicd.audit.model.api.Maybe;
import com.xliic.cicd.audit.model.assessment.AssessmentReport;
import com.xliic.cicd.audit.model.assessment.AssessmentReport.Issue;
import com.xliic.cicd.audit.model.assessment.AssessmentReport.Section;
import com.xliic.cicd.audit.model.assessment.AssessmentReport.SubIssue;
import com.xliic.openapi.bundler.Mapping;
import com.xliic.openapi.bundler.Mapping.Location;
import com.xliic.openapi.bundler.reverse.Document;
import com.xliic.openapi.bundler.reverse.Parser;
import com.xliic.sonar.ResultCollectorImpl.Result;
import com.xliic.sonar.model.Issues;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class AuditSensor implements Sensor {

    private static final Logger LOG = Loggers.get(AuditRulesDefinition.class);
    private static final int MAX_BATCH_SIZE = 25;

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("REST API Static Security Testing").onlyOnLanguage(AuditPlugin.LANGUAGE_YAML);
    }

    @Override
    public void execute(SensorContext context) {

        Optional<String> token = context.config().get(AuditPlugin.API_TOKEN_KEY);
        Optional<String> platformUrl = context.config().get(AuditPlugin.PLATFORM_URL);
        Optional<Boolean> disable = context.config().getBoolean(AuditPlugin.DISABLE);

        if (disable.isPresent() && disable.get()) {
            LOG.info("API Contract Security Audit is disabled");
            return;
        }

        if (!token.isPresent()) {
            throw new AnalysisException("API Token is not configured");
        }

        if (!platformUrl.isPresent()) {
            throw new AnalysisException("42Crunch Platform URL is not configured");
        }

        FileSystem fs = context.fileSystem();
        FilePredicate mainFilePredicate = fs.predicates().and(fs.predicates().hasType(InputFile.Type.MAIN),
                fs.predicates().hasLanguage(AuditPlugin.LANGUAGE_YAML));

        WorkspaceImpl workspace = new WorkspaceImpl(context.fileSystem(), fs.inputFiles(mainFilePredicate));
        Iterator<InputFile> workspaceFiles = workspace.getInputFiles();

        try {
            while (workspaceFiles.hasNext()) {
                FinderImpl finder = new FinderImpl(workspaceFiles, MAX_BATCH_SIZE);
                ResultCollectorImpl results = audit(workspace, finder, platformUrl.get(), new SecretImpl(token.get()));
                saveResults(context, workspace, results);
            }
        } catch (IOException | InterruptedException | AuditException e) {
            e.printStackTrace();
            throw new AnalysisException("Unexpected exception", e);
        }

    }

    private ResultCollectorImpl audit(WorkspaceImpl workspace, FinderImpl finder, String platformUrl, SecretImpl apiKey)
            throws IOException, InterruptedException, AuditException {
        // temporary collection name
        String collectionName = String.format("SonarQube %s", UUID.randomUUID());
        LoggerImpl logger = new LoggerImpl();
        ResultCollectorImpl results = new ResultCollectorImpl();
        Auditor auditor = new Auditor(finder, logger, apiKey);
        auditor.setPlatformUrl(platformUrl);
        auditor.setResultCollector(results);
        auditor.audit(workspace, collectionName, 0);
        // when done, remove the temporary collection
        String collectionId = auditor.getCollectionId();
        if (collectionId != null) {
            Maybe<String> deleted = Client.deleteCollection(collectionId, apiKey, logger);
            if (deleted.isError()) {
                throw new AuditException(String.format("Unable to delete temporary collection '%s' id '%s': %s",
                        collectionName, collectionId, deleted.getError().getMessage()));
            }
        }
        return results;
    }

    void saveMeasures(SensorContext context, InputFile file, Result result) {
        int score = result.score;
        int security_score = result.report.security != null ? Math.round(result.report.security.score) : 0;
        int data_score = result.report.data != null ? Math.round(result.report.data.score) : 0;
        context.<Integer>newMeasure().withValue(score).forMetric(AuditMetrics.SCORE).on(file).save();
        context.<Integer>newMeasure().withValue(security_score).forMetric(AuditMetrics.SECURITY_SCORE).on(file).save();
        context.<Integer>newMeasure().withValue(data_score).forMetric(AuditMetrics.DATA_SCORE).on(file).save();
    }

    private void saveResults(SensorContext context, WorkspaceImpl workspace, ResultCollectorImpl results) {
        for (URI file : results.results.keySet()) {
            InputFile inputFile = workspace.getInputFile(file);
            Result result = results.get(file);
            String[] failures = result.failures;
            AssessmentReport report = result.report;
            Mapping mapping = result.mapping;

            LOG.info(String.format("Reporting to SonarQube audit results for: %s", workspace.getInputFile(file)));

            if (failures.length > 0) {
                for (String failure : failures) {
                    saveAuditErrorIssue(context, inputFile, failure);
                }
            }

            if (report != null) {
                saveMeasures(context, inputFile, result);

                try {
                    Issues issues = AuditKdb.loadIssues();
                    saveIssues(context, workspace, issues, report.index, mapping, report.data, inputFile,
                            Severity.MAJOR);
                    saveIssues(context, workspace, issues, report.index, mapping, report.security, inputFile,
                            Severity.MAJOR);
                    saveIssues(context, workspace, issues, report.index, mapping, report.semanticErrors, inputFile,
                            Severity.CRITICAL);
                    saveIssues(context, workspace, issues, report.index, mapping, report.validationErrors, inputFile,
                            Severity.BLOCKER);
                    saveIssues(context, workspace, issues, report.index, mapping, report.warnings, inputFile,
                            Severity.INFO);
                } catch (IOException | InterruptedException e) {
                    saveAuditErrorIssue(context, inputFile, "Exception: " + e.getMessage());
                }
            }
        }
    }

    private Severity criticalityToSeverity(int criticality, Severity defaultSeverity) {
        switch (criticality) {
        case 1:
            return Severity.INFO;
        case 2:
            return Severity.MINOR;
        case 3:
            return Severity.MAJOR;
        case 4:
            return Severity.CRITICAL;
        case 5:
            return Severity.BLOCKER;
        default:
            return defaultSeverity;
        }
    }

    private IssueLocation getLineByPointerIndex(InputFile file, WorkspaceImpl workspace, Mapping mapping,
            String pointer) throws IOException, InterruptedException, URISyntaxException {
        Document document;
        Location location = mapping.find(pointer);
        if (location == null) {
            // issue in the main file
            if (file.filename().toLowerCase().endsWith(".json")) {
                document = Parser.parseJson(file.contents());
            } else {
                document = Parser.parseYaml(file.contents());
            }
            return new IssueLocation(null, file, (int) document.getLine(pointer));
        } else if (location.file.toLowerCase().endsWith(".json")) {
            URI issueFile = workspace.resolve(file.uri(), location.file);
            document = Parser.parseJson(workspace.read(issueFile));
            return new IssueLocation(file, workspace.getInputFile(issueFile), (int) document.getLine(location.pointer));
        } else {
            URI issueFile = workspace.resolve(file.uri(), location.file);
            document = Parser.parseYaml(workspace.read(issueFile));
            return new IssueLocation(file, workspace.getInputFile(issueFile), (int) document.getLine(location.pointer));
        }
    }

    private String scoreImpact(float score) {
        int rounded = Math.abs(Math.round(score));
        if (score == 0) {
            return "";
        } else if (rounded >= 1) {
            return String.format(" (score impact %d)", rounded);
        } else {
            return " (score impact less than 1)";
        }
    }

    private void saveAuditErrorIssue(SensorContext context, InputFile inputFile, String message) {
        NewIssue newIssue = context.newIssue();
        NewIssueLocation primaryLocation = newIssue.newLocation().message(message).on(inputFile);
        RuleKey ruleKey = RuleKey.of(AuditPlugin.REPO_KEY, "AuditError");
        newIssue.forRule(ruleKey).at(primaryLocation);
        newIssue.overrideSeverity(Severity.BLOCKER);
        newIssue.save();
    }

    private void saveIssues(SensorContext context, WorkspaceImpl workspace, Issues issues, String[] index,
            Mapping mapping, Section section, InputFile inputFile, Severity defaultSeverity)
            throws IOException, InterruptedException {
        if (section == null || section.issues == null) {
            return;
        }

        for (String issueId : section.issues.keySet()) {
            Issue issue = section.issues.get(issueId);
            for (SubIssue subIssue : issue.issues) {
                // FIXME workarounds for bad line numbers and bad json pointers in assessment
                IssueLocation location = new IssueLocation(null, inputFile, 1);
                try {
                    location = getLineByPointerIndex(inputFile, workspace, mapping, index[subIssue.pointer]);
                    if (location.line == 0) {
                        location.line = 1;
                    }
                } catch (Exception e) {
                    LOG.debug("Failed to resolve a json pointer: {} {} {}", inputFile.filename(),
                            index[subIssue.pointer], e);
                }

                String message = String.format("%s%s",
                        subIssue.specificDescription != null ? subIssue.specificDescription : issue.description,
                        scoreImpact(subIssue.score));
                NewIssue newIssue = context.newIssue();
                NewIssueLocation primaryLocation = newIssue.newLocation().message(message).on(location.file)
                        .at(location.file.selectLine(location.line));

                RuleKey ruleKey = RuleKey.of(AuditPlugin.REPO_KEY,
                        issues.containsKey(issueId) ? issueId : "MissingArticle");

                newIssue.forRule(ruleKey).at(primaryLocation);

                newIssue.overrideSeverity(criticalityToSeverity(issue.criticality, defaultSeverity));
                newIssue.save();

            }
        }
    }

    static class IssueLocation {
        InputFile parent;
        InputFile file;
        int line;

        IssueLocation(InputFile parent, InputFile file, int line) {
            this.parent = parent;
            this.file = file;
            this.line = line;
        }
    }

    static class AnalysisException extends RuntimeException {
        AnalysisException(String message) {
            super(message);
        }

        AnalysisException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
