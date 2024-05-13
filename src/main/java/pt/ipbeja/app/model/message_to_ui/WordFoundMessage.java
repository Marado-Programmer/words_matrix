package pt.ipbeja.app.model.message_to_ui;

import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.Position;

import java.util.ArrayList;
import java.util.List;

public class WordFoundMessage extends Message {
    private final Position init;
    private final Position end;
    private final String word;

    public WordFoundMessage(Position init, Position end, String word) {
        this.init = init;
        this.end = end;
        this.word = word;
    }

    @Override
    public @NotNull String getMessage() {
        List<String> pos = new ArrayList<>();
        if (this.init.line() == this.end.line()) {
            int start = Math.min(this.init.col(), this.end.col());
            int end = Math.max(this.init.col(), this.end.col());
            for (int i = start; i <= end; i++) {
                pos.add(String.format("(%d, %c)", this.init.line(), i + 'A'));
            }
        } else if (this.init.col() == this.end.col()) {
            int start = Math.min(this.init.line(), this.end.line());
            int end = Math.max(this.init.line(), this.end.line());
            for (int i = start; i <= end; i++) {
                pos.add(String.format("(%d, %c)", i, this.init.col() + 'A'));
            }
        }
        return String.format("\"%s\" -> %s.", this.word, String.join(", ", pos));
    }
}
