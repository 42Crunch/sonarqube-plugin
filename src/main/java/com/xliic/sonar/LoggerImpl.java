package com.xliic.sonar;

import com.xliic.cicd.audit.Logger;

import org.sonar.api.utils.log.Loggers;

class LoggerImpl implements Logger {

    private static final org.sonar.api.utils.log.Logger LOGGER = Loggers.get(AuditSensor.class);

    LoggerImpl() {
    }

    @Override
    public void log(final String message) {
        LOGGER.info(message);
    }
}