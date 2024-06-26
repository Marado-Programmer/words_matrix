package pt.ipbeja.app.ui;


import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;
import pt.ipbeja.app.model.WSView;


/**
 * Game interface. Just a GridPane of buttons. No images. No menu.
 *
 * @author anonymized
 * @version 2024/04/14
 */
public class WSBoard extends GridPane {
    private final WSModel wsModel;
    private final WSView view;

    /**
     * Create a board with letters.
     * @param wsModel The model
     * @param view The view which the board is part of
     */
    public WSBoard(WSModel wsModel, WSView view) {
        super();
        this.wsModel = wsModel;
        this.view = view;
        this.buildGUI();
    }

    /**
     * Build the interface
     */
    public void buildGUI() {
        assert (null != this.wsModel);

        this.getChildren().clear();

        for (int line = 1; line <= this.wsModel.nLines(); line++) {
            Label label = new Label(String.valueOf(line));
            label.setAlignment(Pos.CENTER);
            label.setMinWidth(CellButton.SQUARE_SIZE);
            label.setMinHeight(CellButton.SQUARE_SIZE);
            this.add(label, 0, line);
        }

        for (int col = 1; col <= this.wsModel.nCols(); col++) {
            Label label = new Label(((char) (col - 1 + (int) 'A')) + "");
            label.setAlignment(Pos.CENTER);
            label.setMinWidth(CellButton.SQUARE_SIZE);
            label.setMinHeight(CellButton.SQUARE_SIZE);
            this.add(label, col, 0);
        }

        // create one label for each position
        for (int line = 1; line <= this.wsModel.nLines(); line++) {
            for (int col = 1; col <= this.wsModel.nCols(); col++) {
                Position pos = new Position(line - 1, col - 1);
                CellButton button = new CellButton(this, this.wsModel, pos);
                this.add(button, col, line);
            }
        }
        this.requestFocus();
    }

    /**
     * Can be optimized using an additional matrix with all the buttons
     *
     * @param line line of label in board
     * @param col  column of label in board
     * @return the button at line, col
     */
    public CellButton getButton(int line, int col) {
        ObservableList<Node> children = this.getChildren();
        for (Node node : children) {
            if (GridPane.getRowIndex(node) == line + 1 && GridPane.getColumnIndex(node) == col + 1) {
                assert (CellButton.class == node.getClass());
                return (CellButton) node;
            }
        }
        assert (false); // must not happen
        return null;
    }

    /**
     * Can be optimized using an additional matrix with all the buttons
     *
     * @param pos The position of the button you want
     * @return the button at line, col
     */
    public CellButton getButton(Position pos) {
        return this.getButton(pos.line(), pos.col());
    }

    public void unselectAll() {
        for (Node child : this.getChildren()) {
            if (null == child) {
                continue;
            }

            try {
                CellButton btn = (CellButton) child;
                if (btn.isPartOfWord()) {
                    btn.setStyle("-fx-background-color: green;");
                } else {
                    btn.setStyle("");
                }
            } catch (ClassCastException ignored) {
            }
        }
    }

    public WSView getView() {
        return this.view;
    }
}
