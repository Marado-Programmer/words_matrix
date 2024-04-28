package pt.ipbeja.app.model;

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
