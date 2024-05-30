package pt.ipbeja.app.ui;


import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;

import static pt.ipbeja.app.ui.CellButton.SQUARE_SIZE;

/**
 * Game interface. Just a GridPane of buttons. No images. No menu.
 *
 * @author anonymized
 * @version 2024/04/14
 */
public class WSBoard extends GridPane {
    private final WSModel wsModel;

    /**
     * Create a board with letters
     */
    public WSBoard(WSModel wsModel) {
        this.wsModel = wsModel;
        this.buildGUI();
    }

    /**
     * Build the interface
     */
    public void buildGUI() {
        assert (this.wsModel != null);

        this.getChildren().clear();

        for (int line = 1; line <= this.wsModel.getLines(); line++) {
            Label label = new Label(String.valueOf(line));
            label.setAlignment(Pos.CENTER);
            label.setMinWidth(SQUARE_SIZE);
            label.setMinHeight(SQUARE_SIZE);
            this.add(label, 0, line);
        }

        for (int col = 1; col <= this.wsModel.getCols(); col++) {
            Label label = new Label(((char) (col - 1 + 'A')) + "");
            label.setAlignment(Pos.CENTER);
            label.setMinWidth(SQUARE_SIZE);
            label.setMinHeight(SQUARE_SIZE);
            this.add(label, col, 0);
        }

        // create one label for each position
        for (int line = 1; line <= this.wsModel.getLines(); line++) {
            for (int col = 1; col <= this.wsModel.getCols(); col++) {
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
                assert (node.getClass() == CellButton.class);
                return (CellButton) node;
            }
        }
        assert (false); // must not happen
        return null;
    }

    public void unselectAll() {
        for (Node child : this.getChildren()) {
            if (child == null) {
                continue;
            }

            try {
                CellButton btn = (CellButton) child;
                if (!btn.isPartOfWord()) {
                    btn.setStyle("");
                } else {
                    btn.setStyle("-fx-background-color: green;");
                }
            } catch (ClassCastException ignored) {
            }
        }
    }
}
