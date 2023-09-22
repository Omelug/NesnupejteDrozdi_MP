package org.drozdi.levels.level3.error;

import org.drozdi.levels.level3.console.ConsoleLogger;

import javax.lang.model.type.ErrorType;

public class GameError {
    private final ErrorType errorType;
    private final String message;
    private final ConsoleLogger logger;

    public GameError(ErrorType errorType, String message, ConsoleLogger logger) {
        this.errorType = errorType;
        this.message = message;
        this.logger = logger;

    }

    public void print() {
        logger.printError( ErrorType.TCP + "  " + message);
    }
    public enum ErrorType {
        UNDEFINED, TCP, UDP
    }
}
