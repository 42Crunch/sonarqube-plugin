package com.xliic.sonar;

import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import static java.util.Arrays.asList;

public class AuditMetrics implements Metrics {

        public static final Metric<Integer> SCORE = new Metric.Builder("audit_score", "Security Audit Score",
                        Metric.ValueType.INT).setDescription("API Contract Security Audit Score")
                                        .setDirection(Metric.DIRECTION_BETTER).setDomain(CoreMetrics.DOMAIN_SECURITY)
                                        .setQualitative(true).create();

        public static final Metric<Integer> SECURITY_SCORE = new Metric.Builder("audit_score_security",
                        "Security Audit Score (security)", Metric.ValueType.INT)
                                        .setDescription("API Contract Security Audit (security)")
                                        .setDirection(Metric.DIRECTION_BETTER).setDomain(CoreMetrics.DOMAIN_SECURITY)
                                        .setQualitative(true).create();

        public static final Metric<Integer> DATA_SCORE = new Metric.Builder("audit_score_data",
                        "Security Audit Score (data)", Metric.ValueType.INT)
                                        .setDescription("API Contract Security Audit (data)")
                                        .setDirection(Metric.DIRECTION_BETTER).setDomain(CoreMetrics.DOMAIN_SECURITY)
                                        .setQualitative(true).create();

        @Override
        public List<Metric> getMetrics() {
                return asList(SCORE, SECURITY_SCORE, DATA_SCORE);
        }
}