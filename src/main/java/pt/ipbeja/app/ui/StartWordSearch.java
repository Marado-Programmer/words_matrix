package pt.ipbeja.app.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * Start a game with a hardcoded board
 *
 * @author anonymized
 * @version 2024/04/14
 */
public class StartWordSearch extends Application {

    private static final double WIDTH = 720;
    private static final double HEIGHT = WIDTH * 3 / 4;

    private static final String TITLE = "TicTacToe";

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(@NotNull Stage stage) {
        stage.setTitle(TITLE);
        stage.setScene(new Scene(new App(stage), WIDTH, HEIGHT));
        stage.show();
    }
}
