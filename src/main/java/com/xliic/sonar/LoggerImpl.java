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
    public void log(final String message) {
        LOGGER.info(message);
    }

    @Override
    public void progress(String message) {
        LOGGER.info(message);
    }

    @Override
    public void report(String message) {
        // do not print any report lines in SQ plugin
    }
}