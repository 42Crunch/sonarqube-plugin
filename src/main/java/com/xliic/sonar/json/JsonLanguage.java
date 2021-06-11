/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar.json;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

public class JsonLanguage extends AbstractLanguage {
    public static final String NAME = "JSON";
    public static final String KEY = "json";

    public JsonLanguage(Configuration config) {
        super(KEY, NAME);
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] { ".json" };
    }
}