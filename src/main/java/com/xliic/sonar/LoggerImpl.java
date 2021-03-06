/*
 Copyright (c) 42Crunch Ltd. All rights reserved.
 Licensed under the GNU Affero General Public License version 3. See LICENSE.txt in the project root for license information.
*/

package com.xliic.sonar;

import com.xliic.cicd.audit.Logger;

import org.sonar.api.utils.log.Loggers;

class LoggerImpl implements Logger {

    private static final org.sonar.api.utils.log.Logger LOGGER = Loggers.get(AuditSensor.class);

    LoggerImpl() {
    }

    @Override
    public void fatal(final String message) {
        LOGGER.error(message);
    }

    @Override
    public void error(final String message) {
        LOGGER.error(message);
    }

    @Override
    public void warn(final String message) {
        LOGGER.warn(message);
    }

    @Override
    public void info(final String message) {
        LOGGER.info(message);
    }

    @Override
    public void debug(final String message) {
        LOGGER.debug(message);
    }

    @Override
    public void setLevel(int level) {
        // ignore, use built in logging levels
    }
}