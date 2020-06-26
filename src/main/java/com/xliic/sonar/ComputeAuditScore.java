package com.xliic.sonar;

import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.Component;

public class ComputeAuditScore implements MeasureComputer {
    @Override
    public MeasureComputerDefinition define(MeasureComputerDefinitionContext def) {
        return def.newDefinitionBuilder().setOutputMetrics(AuditMetrics.SCORE.key()).build();
    }

    @Override
    public void compute(MeasureComputerContext context) {
        if (context.getComponent().getType() != Component.Type.FILE) {
            double min = 100;
            for (Measure child : context.getChildrenMeasures(AuditMetrics.SCORE.key())) {
                min = min < child.getDoubleValue() ? min : child.getDoubleValue();
            }
            context.addMeasure(AuditMetrics.SCORE.key(), min);
        }
    }

}