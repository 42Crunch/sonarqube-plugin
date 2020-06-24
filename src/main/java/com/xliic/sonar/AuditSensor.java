package com.xliic.sonar;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

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
        Collection<String> files = StreamSupport.stream(inputFiles.spliterator(), false).map(InputFile::toString)
                .collect(Collectors.toList());

        // context.newAdHocRule().
        // context.newIssue()
        System.out.println("executed");
        System.out.println("files:" + files);

        for (InputFile inputFile : inputFiles) {
            System.out.println("processing: " + inputFile.filename());

            if (context.isCancelled()) {
                // throw new CancellationException("Analysis interrupted because the
                // SensorContext is in cancelled state");
            }

            NewIssue newIssue = context.newIssue();

            NewIssueLocation primaryLocation = newIssue.newLocation().message("badabumxx").on(inputFile)
                    .at(inputFile.selectLine(2));

            RuleKey ruleKey = RuleKey.of("openapi-audit", "global-http-clear");
            newIssue.forRule(ruleKey).at(primaryLocation);

            // if (issue.cost() != null) {
            /// newIssue.gap(issue.cost());
            // }

            newIssue.save();
        }
    }

}