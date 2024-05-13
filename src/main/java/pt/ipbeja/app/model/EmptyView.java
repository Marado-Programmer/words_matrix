package pt.ipbeja.app.model;

import pt.ipbeja.app.model.message_to_ui.MessageToUI;

final public class EmptyView implements WSView {
    @Override
    public void update(MessageToUI messageToUI) {

    }

    @Override
    public void gameStarted() {

    }

    @Override
    public void wordFound(Position start, Position end) {

    }

    @Override
    public void gameEnded(GameResults res) {

    }
}
