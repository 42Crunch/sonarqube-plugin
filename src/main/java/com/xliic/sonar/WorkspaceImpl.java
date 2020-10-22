/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.xliic.cicd.audit.config.ConfigReader;
import com.xliic.common.Workspace;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

public class WorkspaceImpl implements Workspace {
    private FileSystem fs;
    private LinkedHashMap<URI, InputFile> files = new LinkedHashMap<>();
    private URI config;

    public WorkspaceImpl(FileSystem fs, Iterable<InputFile> files) {
        this.fs = fs;
        this.config = resolve(ConfigReader.CONFIG_FILE_NAME);
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
        if (file.equals(config)) {
            return false;
        }
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