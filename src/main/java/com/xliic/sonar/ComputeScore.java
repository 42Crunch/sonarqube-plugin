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
            computeMinForMetrics(context, AuditMetrics.SECURITY_SCORE);
        }
    }

    void computeMinForMetrics(MeasureComputerContext context, Metric<Integer> metric) {
        int minScore = 100;
        for (Measure child : context.getChildrenMeasures(metric.key())) {
            minScore = minScore < child.getIntValue() ? minScore : child.getIntValue();
        }
        context.addMeasure(metric.key(), minScore);
    }
}
