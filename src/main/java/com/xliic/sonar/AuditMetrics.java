package com.xliic.sonar;

import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import static java.util.Arrays.asList;

public class AuditMetrics implements Metrics {

    public static final Metric<Double> SCORE = new Metric.Builder("audit_score", "Audit Score",
            Metric.ValueType.PERCENT).setDescription("Rating based on audit score")
                    .setDirection(Metric.DIRECTION_BETTER).setDomain(CoreMetrics.DOMAIN_SECURITY).create();

    @Override
    public List<Metric> getMetrics() {
        return asList(SCORE);
    }
}