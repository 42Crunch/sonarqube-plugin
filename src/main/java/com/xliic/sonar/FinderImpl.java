package com.xliic.sonar;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.sonar.api.batch.fs.InputFile;

import com.xliic.cicd.audit.AuditException;
import com.xliic.cicd.audit.OpenApiFinder;

public class FinderImpl implements OpenApiFinder {
    private ArrayList<URI> files = new ArrayList<>();

    FinderImpl(Iterator<InputFile> files, int maxBatchSize) {
        for (int i = 0; i < maxBatchSize && files.hasNext(); i++) {
            this.files.add(files.next().uri());
        }
    }

    @Override
    public void setPatterns(String[] patterns) {
    }

    @Override
    public List<URI> find() throws AuditException, IOException, InterruptedException {
        return files;
    }

}