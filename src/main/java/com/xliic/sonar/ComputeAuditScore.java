package com.xliic.sonar;

import org.sonar.api.measures.Metric;

public class ComputeAuditScore extends ComputeScore {
    @Override
    Metric<Integer> getMetric() {
        return AuditMetrics.SCORE;
    }
}