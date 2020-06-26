package com.xliic.sonar;

import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.Component;

public class ComputeAuditDataScore extends ComputeScore implements MeasureComputer {
    @Override
    public MeasureComputerDefinition define(MeasureComputerDefinitionContext def) {
        return def.newDefinitionBuilder().setOutputMetrics(AuditMetrics.DATA_SCORE.key()).build();
    }

    @Override
    public void compute(MeasureComputerContext context) {
        if (context.getComponent().getType() != Component.Type.FILE) {
            computeMinForMetrics(context, AuditMetrics.DATA_SCORE);
        }
    }

}