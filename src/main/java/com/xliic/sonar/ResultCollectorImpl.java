package com.xliic.sonar;

import java.net.URI;
import java.util.HashMap;
import java.util.Set;

import com.xliic.cicd.audit.ResultCollector;
import com.xliic.cicd.audit.model.assessment.AssessmentReport;
import com.xliic.openapi.bundler.Mapping;

class ResultCollectorImpl implements ResultCollector {
    HashMap<URI, Result> results = new HashMap<>();

    @Override
    public void collect(URI file, int score, AssessmentReport report, Mapping mapping, String[] failures,
            String reportUrl) {
        results.put(file, new Result(score, report, mapping, failures, reportUrl));
    }

    public Result get(URI file) {
        return results.get(file);
    }

    public Set<URI> filenames() {
        return results.keySet();
    }

    public static class Result {
        int score;
        AssessmentReport report;
        Mapping mapping;
        String[] failures;
        String reportUrl;

        Result(int score, AssessmentReport report, Mapping mapping, String[] failures, String reportUrl) {
            this.score = score;
            this.report = report;
            this.mapping = mapping;
            this.failures = failures;
            this.reportUrl = reportUrl;
        }
    }
}