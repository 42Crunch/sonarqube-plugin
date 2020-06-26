package com.xliic.sonar;

import org.sonar.api.measures.Metric;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerContext;

public abstract class ComputeScore {
    void computeMinForMetrics(MeasureComputerContext context, Metric<Double> metric) {
        double minScore = 100;
        for (Measure child : context.getChildrenMeasures(metric.key())) {
            minScore = minScore < child.getDoubleValue() ? minScore : child.getDoubleValue();
        }
        context.addMeasure(metric.key(), minScore);
    }
}
