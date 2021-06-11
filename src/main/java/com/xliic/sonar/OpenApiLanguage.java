/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

public class OpenApiLanguage extends AbstractLanguage {
    public static final String NAME = "OpenAPI";
    public static final String KEY = "openapi";

    static final String FILE_SUFFIXES_KEY = "sonar.openapi.file.suffixes";
    static final String FILE_SUFFIXES_DEFVALUE = ".json,.yaml,.yml";

    public OpenApiLanguage(Configuration config) {
        super(KEY, NAME);
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {};
    }
}