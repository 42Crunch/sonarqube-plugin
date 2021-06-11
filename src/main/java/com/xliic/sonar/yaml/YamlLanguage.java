/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar.yaml;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

public class YamlLanguage extends AbstractLanguage {
    public static final String NAME = "YAML";
    public static final String KEY = "yaml";

    public YamlLanguage(Configuration config) {
        super(KEY, NAME);
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] { ".yaml", ".yml" };
    }
}