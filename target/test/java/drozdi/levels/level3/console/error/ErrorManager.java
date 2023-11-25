package drozdi.levels.level3.console.error;

import drozdi.levels.level3.console.ConsoleLogger;

public class ErrorManager {
    private static final ConsoleLogger logger = new ConsoleLogger();

    public static GameError newError(GameError.ErrorType errorType, String message) {
       GameError gameError = new GameError(errorType, message, logger);
       gameError.print();
       return gameError;
    }
}
