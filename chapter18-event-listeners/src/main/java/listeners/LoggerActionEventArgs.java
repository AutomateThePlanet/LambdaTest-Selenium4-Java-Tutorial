package listeners;

import lombok.Getter;

public class LoggerActionEventArgs {
    @Getter private final String entry;

    public LoggerActionEventArgs(String entry) {
        this.entry = entry;
    }
}
