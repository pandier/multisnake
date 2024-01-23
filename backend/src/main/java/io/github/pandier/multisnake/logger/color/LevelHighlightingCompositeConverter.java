package io.github.pandier.multisnake.logger.color;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class LevelHighlightingCompositeConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {
    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        final Level level = event.getLevel();
        return switch (level.toInt()) {
            case Level.ERROR_INT -> "31";
            case Level.WARN_INT -> "33";
            case Level.INFO_INT -> "34";
            default -> "39";
        };
    }
}
