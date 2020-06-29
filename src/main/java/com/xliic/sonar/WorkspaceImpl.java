package com.xliic.sonar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.xliic.common.Workspace;
import com.xliic.cicd.audit.AuditException;
import com.xliic.cicd.audit.OpenApiFinder;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

public class WorkspaceImpl implements Workspace, OpenApiFinder {
    private FileSystem fs;
    private HashMap<String, InputFile> files = new HashMap<>();
    private ArrayList<String> names = new ArrayList<>();

    public WorkspaceImpl(FileSystem fs, Iterable<InputFile> files) {
        this.fs = fs;
        for (InputFile file : files) {
            this.files.put(absolutize(file.filename()), file);
            this.names.add(file.filename());
        }
    }

    @Override
    public String[] find() throws AuditException, IOException, InterruptedException {
        return names.toArray(new String[0]);
    }

    @Override
    public void setPatterns(String[] patterns) {
    }

    public InputFile getInputFile(String filename) {
        return files.get(absolutize(filename));
    }

    @Override
    public String read(String filename) throws IOException, InterruptedException {
        if (files.containsKey(absolutize(filename))) {
            return files.get(absolutize(filename)).contents();
        }
        return null;
    }

    @Override
    public boolean exists(String filename) throws IOException, InterruptedException {
        return files.containsKey(absolutize(filename));
    }

    @Override
    public String absolutize(String filename) {
        return fs.resolvePath(filename).getAbsolutePath();
    }
}