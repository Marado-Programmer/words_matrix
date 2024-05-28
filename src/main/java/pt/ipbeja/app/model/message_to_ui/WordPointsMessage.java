package pt.ipbeja.app.model.message_to_ui;

import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;

import java.util.ArrayList;
import java.util.List;

public class WordPointsMessage extends Message {
    private final String word;
    private final int points;
    public WordPointsMessage(String word, int points) {
        this.word = word;
        this.points = points;
    }

    @Override
    public @NotNull String getMessage() {
        return String.format("\"%s\" = %d pontos.", this.word, this.points);
    }
}
