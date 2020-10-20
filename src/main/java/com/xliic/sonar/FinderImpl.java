/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        // ignore set patterns, matching files provided by SQ
    }

    @Override
    public List<URI> find() throws AuditException, IOException, InterruptedException {
        return files;
    }

}