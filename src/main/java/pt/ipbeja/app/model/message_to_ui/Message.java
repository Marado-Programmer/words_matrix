package pt.ipbeja.app.model.message_to_ui;

import org.jetbrains.annotations.NotNull;

public abstract class Message implements MessageToUI {
    @Override
    public @NotNull String toString() {
        return this.getMessage();
    }
}
