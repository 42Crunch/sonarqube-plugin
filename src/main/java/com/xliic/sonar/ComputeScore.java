package com.xliic.sonar;

import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.measures.Metric;
import org.sonar.api.ce.measure.Component;

public abstract class ComputeScore implements MeasureComputer {
    abstract Metric<Integer> getMetric();

    @Override
    public MeasureComputerDefinition define(MeasureComputerDefinitionContext def) {
        return def.newDefinitionBuilder().setOutputMetrics(getMetric().key()).build();
    }

    @Override
    public void compute(MeasureComputerContext context) {
        if (context.getComponent().getType() != Component.Type.FILE) {
            int minScore = 100;
            for (Measure child : context.getChildrenMeasures(getMetric().key())) {
                minScore = minScore < child.getIntValue() ? minScore : child.getIntValue();
            }
            context.addMeasure(getMetric().key(), minScore);
        }
    }

}
