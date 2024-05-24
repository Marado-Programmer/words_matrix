package pt.ipbeja.app.ui;


import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.cell.BaseCell;
import pt.ipbeja.app.model.cell.Cell;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;
import pt.ipbeja.app.model.throwables.NotInGameException;

/**
 * Game interface. Just a GridPane of buttons. No images. No menu.
 *
 * @author anonymized
 * @version 2024/04/14
 */
public class WSBoard extends GridPane {
    private static final int SQUARE_SIZE = 64;
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
                BaseCell textForButton = this.wsModel.textInPosition(new Position(line, col));
                if (textForButton == null) {
                    textForButton = new Cell(' ');
                }
                Button button = createBtn(textForButton, line, col);
                this.add(button, col, line); // add button to GridPane
            }
        }
        this.requestFocus();
    }

    @NotNull
    private Button createBtn(@NotNull BaseCell textForButton, int line, int col) {
        Button button = new Button(String.valueOf(textForButton.getDisplay()));
        button.setMinWidth(SQUARE_SIZE);
        button.setMinHeight(SQUARE_SIZE);
        button.setOnAction(event -> {
            try {
                if (this.wsModel.findWord(new Position(line, col))) {
                    if (!button.getStyle().contains("green")) {
                        button.setStyle("-fx-background-color: yellow;");
                    }
                } else {
                    this.unselectAll();
                }
            } catch (NotInGameException e) {
                throw new RuntimeException(e);
            }
        });
        return button;
    }

    /**
     * Can be optimized using an additional matrix with all the buttons
     *
     * @param line line of label in board
     * @param col  column of label in board
     * @return the button at line, col
     */
    public @NotNull Button getButton(int line, int col) {
        ObservableList<Node> children = this.getChildren();
        for (Node node : children) {
            if (GridPane.getRowIndex(node) == line && GridPane.getColumnIndex(node) == col) {
                assert (node.getClass() == Button.class);
                return (Button) node;
            }
        }
        assert (false); // must not happen
        return null;
    }

    private void unselectAll() {
        for (Node child : this.getChildren()) {
            Button btn = (Button) child;
            if (btn.getStyle().contains("yellow")) {
                btn.setStyle("");
            }
        }
    }
}
