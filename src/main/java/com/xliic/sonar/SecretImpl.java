/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import com.xliic.cicd.audit.Secret;

class SecretImpl implements Secret {

    private String secret;

    public SecretImpl(String secret) {
        this.secret = secret;
    }

    @Override
    public String getPlainText() {
        return secret;
    }
}