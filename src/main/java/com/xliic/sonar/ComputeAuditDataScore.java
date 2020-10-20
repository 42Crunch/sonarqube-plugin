/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import org.sonar.api.measures.Metric;

public class ComputeAuditDataScore extends ComputeScore {
    @Override
    Metric<Integer> getMetric() {
        return AuditMetrics.DATA_SCORE;
    }
}