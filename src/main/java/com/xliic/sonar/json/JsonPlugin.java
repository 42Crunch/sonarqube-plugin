/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar.json;

import org.sonar.api.Plugin;

public class JsonPlugin implements Plugin {
        @Override
        public void define(Context context) {
                context.addExtension(JsonLanguage.class);
        }
}
