/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFileFilter;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.WildcardPattern;

public class OpenApiExclusionsFileFilter implements InputFileFilter {

    private final Configuration configuration;

    public OpenApiExclusionsFileFilter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean accept(InputFile inputFile) {
        if (isExcludedWithProperty(inputFile, AuditPlugin.EXCLUSIONS_KEY)) {
            return false;
        }

        return true;
    }

    private boolean isExcludedWithProperty(InputFile inputFile, String property) {
        String[] excludedPatterns = this.configuration.getStringArray(property);
        String relativePath = inputFile.uri().toString();
        return WildcardPattern.match(WildcardPattern.create(excludedPatterns), relativePath);
    }
}