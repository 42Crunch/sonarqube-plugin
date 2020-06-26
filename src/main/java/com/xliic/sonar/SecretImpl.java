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