package sirup.service.user.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SirupLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(SirupLogger.class);
    private static SirupLogger instance;

    private SirupLogger() {
    }

    public static SirupLogger getInstance() {
        return instance == null ? instance = new SirupLogger() : instance;
    }

    public void debug(String message) {
        LOGGER.debug(ConsoleColors.BLUE_BRIGHT + message + ConsoleColors.RESET);
    }

    public void info(String message) {
        LOGGER.info(ConsoleColors.GREEN + message + ConsoleColors.RESET);
    }

    public void warn(String message) {
        LOGGER.warn(ConsoleColors.PURPLE + message + ConsoleColors.RESET);
    }

    public void error(String message) {
        LOGGER.error(ConsoleColors.RED + message + ConsoleColors.RESET);
    }
}
