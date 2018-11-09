package hangman;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Hangman extends Application {

	@Override
	public void start(final Stage primaryStage) throws IOException {
		final Game game = new Game(primaryStage);
		game.loadsUI(game);

	}

	public static void main(String[] args) {
		launch(args);
	}

}
