package com.xliic.sonar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.xliic.cicd.audit.AuditException;
import com.xliic.cicd.audit.Auditor;
import com.xliic.cicd.audit.model.assessment.AssessmentReport;
import com.xliic.cicd.audit.model.assessment.AssessmentReport.Issue;
import com.xliic.cicd.audit.model.assessment.AssessmentReport.SubIssue;
import com.xliic.openapi.bundler.Mapping;
import com.xliic.openapi.bundler.Mapping.Location;
import com.xliic.openapi.bundler.reverse.Document;
import com.xliic.openapi.bundler.reverse.Parser;
import com.xliic.sonar.ResultCollectorImpl.Result;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

import static com.google.common.collect.Lists.newArrayList;

public class AuditSensor implements Sensor {

    private FileSystem fileSystem;
    private FilePredicate mainFilePredicate;

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Fooh fah foom");
    }

    @Override
    public void execute(SensorContext context) {
        this.fileSystem = context.fileSystem();
        this.mainFilePredicate = fileSystem.predicates().or(fileSystem.predicates().hasExtension("yaml"),
                fileSystem.predicates().hasExtension("yml"), fileSystem.predicates().hasExtension("json"));

        Iterable<InputFile> inputFiles = fileSystem.inputFiles(mainFilePredicate);

        ArrayList<InputFile> files = newArrayList(fileSystem.inputFiles(mainFilePredicate));

        // Collection<String> files = StreamSupport.stream(inputFiles.spliterator(),
        // false).map(InputFile::toString)
        // .collect(Collectors.toList());

        // context.newAdHocRule().
        // context.newIssue()

        System.out.println("executed");
        System.out.println("files:" + files);

        FinderImpl finder = new FinderImpl(files);
        WorkspaceImpl workspace = new WorkspaceImpl(context.fileSystem(), files);
        LoggerImpl logger = new LoggerImpl();
        SecretImpl apiKey = new SecretImpl("851d8b6e-0584-4909-b5a8-a88528d8f81d");
        ResultCollectorImpl results = new ResultCollectorImpl();
        Auditor auditor = new Auditor(finder, logger, apiKey);
        auditor.setResultCollector(results);
        try {
            auditor.audit(workspace, "sq", 10);
        } catch (IOException | InterruptedException | AuditException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (Entry<String, Result> result : results.results.entrySet()) {
            InputFile inputFile = workspace.getInputFile(result.getKey());
            AssessmentReport report = result.getValue().report;
            double score = result.getValue().score;
            context.<Double>newMeasure().withValue(score).forMetric(AuditMetrics.SCORE).on(inputFile).save();

            Mapping mapping = result.getValue().mapping;
            System.out.println("r d: " + report.data + " r s: " + report.security);
            if (report.data != null && report.data.issues != null) {
                for (Entry<String, Issue> entry : report.data.issues.entrySet()) {
                    Issue issue = entry.getValue();
                    String issueId = entry.getKey().toLowerCase().replace(".", "-");
                    for (SubIssue subissue : issue.issues) {
                        String pointer = report.index[subissue.pointer];

                        try {
                            int line;
                            Location location = mapping.find(pointer);
                            Document document;
                            if (location == null) {
                                // issue in the main file
                                if (inputFile.filename().toLowerCase().endsWith(".json")) {
                                    document = Parser.parseJson(inputFile.contents());
                                } else {
                                    document = Parser.parseYaml(inputFile.contents());
                                }
                                line = (int) document.getLine(pointer);
                            } else if (location.file.toLowerCase().endsWith(".json")) {
                                document = Parser.parseJson(workspace.read(location.file));
                                line = (int) document.getLine(location.pointer);
                            } else {
                                document = Parser.parseYaml(workspace.read(location.file));
                                line = (int) document.getLine(location.pointer);
                            }

                            System.out.println("data issue: " + issueId);
                            NewIssue newIssue = context.newIssue();
                            NewIssueLocation primaryLocation = newIssue.newLocation()
                                    .message(subissue.specificDescription != null ? subissue.specificDescription
                                            : issue.description)
                                    .on(inputFile).at(inputFile.selectLine(line));
                            RuleKey ruleKey = RuleKey.of("openapi-audit", issueId);
                            newIssue.forRule(ruleKey).at(primaryLocation);
                            newIssue.save();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

            }

        }

    }

}