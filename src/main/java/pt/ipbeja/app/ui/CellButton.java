package pt.ipbeja.app.ui;

import javafx.scene.control.Button;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;

public class CellButton extends Button {
    public static final int SQUARE_SIZE = 32;
    private boolean partOfWord;

    public CellButton(WSBoard board, WSModel model, Position pos) {
        super(model.textInPosition(pos).getDisplay() + "");
        this.setMinWidth(SQUARE_SIZE);
        this.setMinHeight(SQUARE_SIZE);
        this.setOnAction(event -> board.getView().click(pos));

        this.partOfWord = false;
    }

    public boolean isPartOfWord() {
        return partOfWord;
    }

    public void setPartOfWord(boolean partOfWord) {
        this.partOfWord = partOfWord;
    }
}
