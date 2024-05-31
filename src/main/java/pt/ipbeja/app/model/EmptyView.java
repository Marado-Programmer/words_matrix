package pt.ipbeja.app.model;

public class EmptyView implements WSView {
    @Override
    public void update(MessageToUI messageToUI) {

    }

    @Override
    public void updatePoints(Word word) {

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

    @Override
    public void click(Position pos) {

    }
}
