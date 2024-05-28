package pt.ipbeja.app.ui;


import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;

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

        // create one label for each position
        for (int line = 0; line < this.wsModel.getLines(); line++) {
            for (int col = 0; col < this.wsModel.getCols(); col++) {
                Position pos = new Position(line, col);
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
    public @NotNull CellButton getButton(int line, int col) {
        ObservableList<Node> children = this.getChildren();
        for (Node node : children) {
            if (GridPane.getRowIndex(node) == line && GridPane.getColumnIndex(node) == col) {
                assert (node.getClass() == CellButton.class);
                return (CellButton) node;
            }
        }
        assert (false); // must not happen
        return null;
    }

    public void unselectAll() {
        for (Node child : this.getChildren()) {
            CellButton btn = (CellButton) child;
            if (!btn.isPartOfWord()) {
                btn.setStyle("");
            } else {
                btn.setStyle("-fx-background-color: green;");
            }
        }
    }
}
