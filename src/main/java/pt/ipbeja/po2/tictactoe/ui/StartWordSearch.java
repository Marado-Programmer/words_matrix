package pt.ipbeja.po2.tictactoe.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import pt.ipbeja.po2.tictactoe.model.WSModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static pt.ipbeja.po2.tictactoe.model.WSModel.MAX_SIDE_LEN;

/**
 * Start a game with a hardcoded board
 *
 * @author anonymized
 * @version 2024/04/14
 */
public class StartWordSearch extends Application {

    /**
     * @param args not used
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
        WSModel model;
        if (file != null) {
            model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN, file.toPath());
        } else {
            model = null;
            Platform.exit();
        }

        WSBoard WSBoard = new WSBoard(model);
        Button end = new Button("End Game Now");
        end.setOnAction(event -> {
            assert model != null;
            model.endGame();
        });
        stage.setScene(new Scene(new VBox(WSBoard, end)));
        assert model != null;
        model.registerView(WSBoard);
        WSBoard.requestFocus(); // to remove focus from first button
        stage.show();

        model.setSaver(res -> {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    file.getParentFile().toPath().resolve("scores.txt")
            )) {
                writer.write(String.format("%.2f%%\n", 100.0 * res.words_found().size() / res.words().size()));
            } catch (IOException ignored) {
            }
        });
    }
}
