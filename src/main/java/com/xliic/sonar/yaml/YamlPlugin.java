/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar.yaml;

import org.sonar.api.Plugin;

public class YamlPlugin implements Plugin {
        @Override
        public void define(Context context) {
                context.addExtension(YamlLanguage.class);
        }
}
