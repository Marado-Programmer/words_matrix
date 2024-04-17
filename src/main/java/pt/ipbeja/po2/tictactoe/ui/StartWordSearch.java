package pt.ipbeja.po2.tictactoe.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.po2.tictactoe.model.WSModel;

import java.io.File;

import static pt.ipbeja.po2.tictactoe.model.WSModel.MAX_SIDE_LEN;

/**
 * Start a game with a hardcoded board
 * @author anonymized
 * @version 2024/04/14
 */
public class StartWordSearch extends Application {

    /**
     * @param args  not used
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(@NotNull Stage stage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Which DB file would you like to use?");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(stage);
        WSModel model = null;
        if (file != null) {
            model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN, file.toPath());
        } else {
            Platform.exit();
        }

        WSBoard WSBoard = new WSBoard(model);
        stage.setScene(new Scene(WSBoard));

        if (model != null) {
            model.registerView(WSBoard);
        }
        WSBoard.requestFocus(); // to remove focus from first button
        stage.show();
    }
}
