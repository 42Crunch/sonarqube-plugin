package com.xliic.sonar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.xliic.cicd.audit.AuditException;
import com.xliic.cicd.audit.OpenApiFinder;

import org.sonar.api.batch.fs.InputFile;

class FinderImpl implements OpenApiFinder {

    private ArrayList<InputFile> files;

    public FinderImpl(ArrayList<InputFile> files) {
        this.files = files;
    }

    @Override
    public void setPatterns(String[] patterns) {
    }

    @Override
    public String[] find() throws AuditException, IOException, InterruptedException {
        ArrayList<String> filenames = new ArrayList<>();
        for (InputFile file : files) {
            filenames.add(file.filename());
        }
        return filenames.toArray(new String[0]);
    }
}