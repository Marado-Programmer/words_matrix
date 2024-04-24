package pt.ipbeja.app.ui;


import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.app.model.*;

/**
 * Game interface. Just a GridPane of buttons. No images. No menu.
 * @author anonymized
 * @version 2024/04/14
 */
public class WSBoard extends GridPane implements WSView {
    private final WSModel wsModel;
    private static final int SQUARE_SIZE = 80;

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
    private void buildGUI() {
        assert (this.wsModel != null);

        // create one label for each position
        for (int line = 0; line < this.wsModel.getLines(); line++) {
            for (int col = 0; col < this.wsModel.getCols(); col++) {
                Cell textForButton = this.wsModel.textInPosition(new Position(line, col));
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
    private Button createBtn(@NotNull Cell textForButton, int line, int col) {
        Button button = new Button(String.valueOf(textForButton.letter()));
        button.setMinWidth(SQUARE_SIZE);
        button.setMinHeight(SQUARE_SIZE);
        button.setOnAction(event -> {
            if (this.wsModel.findWord(new Position(line, col))) {
                if (button.getBackground().getFills().stream().noneMatch(backgroundFill -> backgroundFill.getFill() == Color.GREEN)) {
                    button.setBackground(Background.fill(Color.YELLOW));
                }
            } else {
                this.unselectAll();
            }
        });
        return button;
    }

    /**
     * Can be optimized using an additional matrix with all the buttons
     * @param line line of label in board
     * @param col column of label in board
     * @return the button at line, col
     */
    public @NotNull Button getButton(int line, int col) {
        ObservableList<Node> children = this.getChildren();
        for (Node node : children) {
            if(GridPane.getRowIndex(node) == line && GridPane.getColumnIndex(node) == col) {
                assert(node.getClass() == Button.class);
                return (Button)node;
            }
        }
        assert(false); // must not happen
        return null;
    }

    /**
     * Simply updates the text for the buttons in the received positions
     *
     * @param messageToUI the WS model
     */
    @Override
    public void update(@NotNull MessageToUI messageToUI) {
        for (Position p : messageToUI.positions()) {
            Cell s = this.wsModel.textInPosition(p);
            this.getButton(p.line(), p.col()).setText(String.valueOf(s.letter()));
        }
        if (this.wsModel.allWordsWereFound()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("");
            alert.setContentText("Level completed!");
            alert.showAndWait();
            System.exit(0);
        }
    }

    @Override
    public void wordFound(@NotNull Position start, @NotNull Position end) {
        if (start.line() == end.line()) {
            int start_pos = Math.min(start.col(), end.col());
            int end_pos = Math.max(start.col(), end.col());
            for (int i = start_pos; i <= end_pos; i++) {
                Button btn = this.getButton(end.line(), i);
                btn.setBackground(Background.fill(Color.GREEN));
            }
        } else if (start.col() == end.col()) {
            int start_pos = Math.min(start.line(), end.line());
            int end_pos = Math.max(start.line(), end.line());
            for (int i = start_pos; i <= end_pos; i++) {
                Button btn = this.getButton(i, end.col());
                btn.setBackground(Background.fill(Color.GREEN));
            }
        }

        this.wsModel.gameEnded();
    }

    @Override
    public void gameEnded(@NotNull GameResults res) {
        System.out.println(res.words_found().size());
        System.out.println(res.words().size());
        System.out.printf("%.2f%%\n", 100.0 * res.words_found().size() / res.words().size());
    }

    private void unselectAll() {
        for (Node child : this.getChildren()) {
            Button btn = (Button) child;
            if (btn.getBackground().getFills().stream().anyMatch(backgroundFill -> backgroundFill.getFill() == Color.YELLOW)) {
                btn.setBackground(Background.fill(Color.TRANSPARENT));
            }
        }
    }
}
