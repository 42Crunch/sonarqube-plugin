/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar.json;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class JsonQualityProfile implements BuiltInQualityProfilesDefinition {
    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("JSON", JsonLanguage.KEY);
        profile.setDefault(true);
        profile.done();
    }
}