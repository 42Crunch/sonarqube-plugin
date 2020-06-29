package com.xliic.sonar;

import org.sonar.api.measures.Metric;

public class ComputeAuditSecurityScore extends ComputeScore {
    @Override
    Metric<Integer> getMetric() {
        return AuditMetrics.SECURITY_SCORE;
    }
}