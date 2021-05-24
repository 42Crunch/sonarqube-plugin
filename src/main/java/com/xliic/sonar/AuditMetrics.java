/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import static java.util.Arrays.asList;

public class AuditMetrics implements Metrics {

        public static final Metric<Integer> SCORE = new Metric.Builder("audit_score", "Audit Score",
                        Metric.ValueType.INT).setDescription("API Security Audit Score")
                                        .setDirection(Metric.DIRECTION_BETTER).setDomain(CoreMetrics.DOMAIN_SECURITY)
                                        .setQualitative(true).create();

        public static final Metric<Integer> SECURITY_SCORE = new Metric.Builder("audit_score_security",
                        "Audit Score (security)", Metric.ValueType.INT)
                                        .setDescription("API Security Audit Score (security)")
                                        .setDirection(Metric.DIRECTION_BETTER).setDomain(CoreMetrics.DOMAIN_SECURITY)
                                        .setQualitative(true).create();

        public static final Metric<Integer> DATA_SCORE = new Metric.Builder("audit_score_data", "Audit Score (data)",
                        Metric.ValueType.INT).setDescription("API Security Audit Score (data)")
                                        .setDirection(Metric.DIRECTION_BETTER).setDomain(CoreMetrics.DOMAIN_SECURITY)
                                        .setQualitative(true).create();

        @Override
        public List<Metric> getMetrics() {
                return asList(SCORE, SECURITY_SCORE, DATA_SCORE);
        }
}
