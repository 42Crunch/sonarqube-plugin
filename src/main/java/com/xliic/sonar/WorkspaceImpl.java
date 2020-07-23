package com.xliic.sonar;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.xliic.common.Workspace;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

public class WorkspaceImpl implements Workspace {
    private FileSystem fs;
    private LinkedHashMap<URI, InputFile> files = new LinkedHashMap<>();

    public WorkspaceImpl(FileSystem fs, Iterable<InputFile> files) {
        this.fs = fs;
        for (InputFile file : files) {
            this.files.put(file.uri(), file);
        }
    }

    public InputFile getInputFile(URI uri) {
        return files.get(uri);
    }

    public Iterator<InputFile> getInputFiles() {
        return files.values().iterator();
    }

    @Override
    public String read(URI uri) throws IOException, InterruptedException {
        return files.get(uri).contents();
    }

    @Override
    public boolean exists(URI file) throws IOException, InterruptedException {
        return files.containsKey(file);
    }

    @Override
    public URI resolve(String filename) {
        try {
            String safeFilename = new URI(null, filename, null).getRawSchemeSpecificPart();
            return fs.baseDir().toURI().resolve(safeFilename);
        } catch (URISyntaxException e) {
            throw (IllegalArgumentException) new IllegalArgumentException().initCause(e);
        }
    }

    public URI resolve(URI base, String filename) {
        try {
            String safeFilename = new URI(null, filename, null).getRawSchemeSpecificPart();
            return base.resolve(safeFilename);
        } catch (URISyntaxException e) {
            throw (IllegalArgumentException) new IllegalArgumentException().initCause(e);
        }
    }

    @Override
    public URI relativize(URI uri) {
        return fs.baseDir().toURI().relativize(uri);
    }
}