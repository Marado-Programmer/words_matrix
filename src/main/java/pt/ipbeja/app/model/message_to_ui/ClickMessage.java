package pt.ipbeja.app.model.message_to_ui;

import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.Position;

public class ClickMessage extends Message {
    private final Position pos;
    private final char c;

    public ClickMessage(Position pos, char c) {
        this.pos = pos;
        this.c = c;
    }

    @Override
    public @NotNull String getMessage() {
        return String.format("(%d, %c) -> %c", this.pos.line(), this.pos.col() + 'A', this.c);
    }
}
