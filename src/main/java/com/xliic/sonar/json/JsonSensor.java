/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar.json;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;

public class JsonSensor implements Sensor {
    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("JSON Sensor").onlyOnLanguage(JsonLanguage.KEY);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fs = context.fileSystem();
        FilePredicate mainFilesPredicate = fs.predicates().and(fs.predicates().hasType(InputFile.Type.MAIN),
                fs.predicates().hasLanguage(JsonLanguage.KEY));

        for (InputFile file : fs.inputFiles(mainFilesPredicate)) {
            context.<Integer>newMeasure().withValue(file.lines()).forMetric(CoreMetrics.NCLOC).on(file).save();
        }
    }
}
