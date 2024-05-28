package pt.ipbeja.app.model;

import pt.ipbeja.app.model.message_to_ui.MessageToUI;

/**
 * View
 *
 * @author anonymized
 * @version 2024/04/14
 */
public interface WSView {
    void update(MessageToUI messageToUI);

    void gameStarted();

    void wordFound(Position start, Position end);

    void gameEnded(GameResults res);
}
