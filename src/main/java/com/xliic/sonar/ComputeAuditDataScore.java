package com.xliic.sonar;

import org.sonar.api.measures.Metric;

public class ComputeAuditDataScore extends ComputeScore {
    @Override
    Metric<Integer> getMetric() {
        return AuditMetrics.DATA_SCORE;
    }
}