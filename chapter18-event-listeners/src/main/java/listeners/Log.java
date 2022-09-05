package listeners;

import org.jetbrains.annotations.NotNull;

public class Log {
    public final static EventListener<LoggerActionEventArgs> LOGGED_ENTRY = new EventListener<>();
    public final static EventListener<LoggerActionEventArgs> LOGGED_INFO = new EventListener<>();
    public final static EventListener<LoggerActionEventArgs> LOGGED_ERROR = new EventListener<>();

    public static void info(@NotNull String format, Object... args) {
        String info = String.format(format, args);
        LOGGED_ENTRY.broadcast(new LoggerActionEventArgs(info));
        LOGGED_INFO.broadcast(new LoggerActionEventArgs(info));
        System.out.println(info);
    }

    public static void error(@NotNull String format, Object... args) {
        String error = String.format(format, args);
        LOGGED_ENTRY.broadcast(new LoggerActionEventArgs(error));
        LOGGED_ERROR.broadcast(new LoggerActionEventArgs(error));
        System.err.println(error);
    }
}