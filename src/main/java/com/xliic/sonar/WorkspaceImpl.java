package com.xliic.sonar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.xliic.common.Workspace;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

public class WorkspaceImpl implements Workspace {
    private HashMap<String, InputFile> files = new HashMap<>();
    private FileSystem fs;

    public WorkspaceImpl(FileSystem fs, ArrayList<InputFile> files) {
        this.fs = fs;
        for (InputFile file : files) {
            this.files.put(abs(file.filename()), file);
        }
    }

    public InputFile getInputFile(String filename) {
        return files.get(abs(filename));
    }

    private String abs(String filename) {
        return fs.resolvePath(filename).getAbsolutePath();
    }

    @Override
    public String read(String filename) throws IOException, InterruptedException {
        if (files.containsKey(abs(filename))) {
            return files.get(abs(filename)).contents();
        }
        return null;
    }

    @Override
    public boolean exists(String filename) throws IOException, InterruptedException {
        return files.containsKey(abs(filename));
    }

    @Override
    public String absolutize(String filename) throws IOException, InterruptedException {
        return fs.resolvePath(filename).getAbsolutePath();
    }

}