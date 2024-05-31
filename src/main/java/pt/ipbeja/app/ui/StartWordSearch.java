package pt.ipbeja.app.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Start a game with a hardcoded board
 *
 * @author anonymized
 * @version 2024/04/14
 */
public class StartWordSearch extends Application {

    private static final double WIDTH = 720.0;
    private static final double HEIGHT = WIDTH * 3.0 / 4.0;

    public static final String TITLE = "TicTacToe";

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(new Scene(new App(primaryStage), WIDTH, HEIGHT));
        primaryStage.show();
    }
}
