package hangman;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GameController {

	private final ExecutorService executorService;
	private final Game game;	
	
	public GameController(Game game) {
		this.game = game;
		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	@FXML
	private VBox board ;
	@FXML
	private Label statusLabel ;
	@FXML
	private Label enterALetterLabel ;
    @FXML
    private Label movesLeftLabel ;
	@FXML
	private Label correctGuessLabel;
	@FXML
	private Label incorrectGuessLabel;
    @FXML
	private TextField textField ;
	@FXML
	private TextField correctGuessField ;
	@FXML
	private TextField incorrectGuessField ;
	@FXML
	private VBox titleBox;
	@FXML
	private Label title;

	// calling all the helper method here.
    public void initialize() throws IOException {
		System.out.println("in initialize");
		drawHangman();
		addTextBoxListener();
		setUpStatusLabelBindings();
		prepAnswerFields();
	}

	private void addTextBoxListener() {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				if(newValue.length() > 0 && newValue.matches("[a-zA-Z]+")) {
					int tempIndex = -1;
					int numChar = 0;
					System.out.print(newValue);
					for (int i = 0; i < game.getAnsArray().length; i++) {
						if (newValue.equals(game.getAnsArray()[i])) {
							numChar++;
						}
					}
					for (int i = 0; i < numChar-1; i++) {
						tempIndex = getValidIndex(game.getAnsArray(), newValue);
						game.makeMove(newValue);
						if (tempIndex != -1 && (game.getGameStatus() == Game.GameStatus.GOOD_GUESS || game.getGameStatus() == Game.GameStatus.WON)) {
							String tempField1 = correctGuessField.getText();
							int tempInt = tempIndex * 2;
							if (tempInt == 0) {
								String tempField2 = newValue + tempField1.substring(1);
								correctGuessField.setText(tempField2);
							} else {
								String tempField2 = tempField1.substring(0, tempInt) + newValue + tempField1.substring(tempInt + 1);
								correctGuessField.setText(tempField2);
							}
						}
					}
					tempIndex = getValidIndex(game.getAnsArray(), newValue);
					game.makeMove(newValue);
					if (tempIndex != -1 && (game.getGameStatus() == Game.GameStatus.GOOD_GUESS || game.getGameStatus() == Game.GameStatus.WON)) {
						String tempField1 = correctGuessField.getText();
						int tempInt = tempIndex * 2;
						if (tempInt == 0) {
							String tempField2 = newValue + tempField1.substring(1);
							correctGuessField.setText(tempField2);
						} else {
							String tempField2 = tempField1.substring(0, tempInt) + newValue + tempField1.substring(tempInt + 1);
							correctGuessField.setText(tempField2);
						}
					}
					else if (game.getGameStatus() == Game.GameStatus.BAD_GUESS || game.getGameStatus() == Game.GameStatus.GAME_OVER) {
						String tempField = incorrectGuessField.getText();

						drawHangman();
						if (tempField.equals("")) {
							tempField = newValue;
							incorrectGuessField.setText(tempField);
						}
						else {
							tempField = tempField + " " + newValue;
							incorrectGuessField.setText(tempField);
						}
					}
					textField.clear();
				}
				else{
					textField.clear();
				}
			}
		});
	}

	// find the value of the index self explanatory
	private int getValidIndex(String[] array, String input) {
		int index = -1;
		for(int i = 0; i < array.length; i++) {
			if(array[i].equalsIgnoreCase(input)) {
				index = i;
				break;
			}
		}
		return index;
	}

	private void setUpStatusLabelBindings() {

		System.out.println("in setUpStatusLabelBindings");
		statusLabel.textProperty().bind(Bindings.format("%s", game.gameStatusProperty()));
		enterALetterLabel.textProperty().bind(Bindings.format("%s", "Enter a letter:"));
      //  movesLeftLabel.textProperty().bind(Bindings.format("You have %s moves left", game.getMoves()));
		correctGuessLabel.textProperty().bind(Bindings.format("%s", "Correct:"));
		incorrectGuessLabel.textProperty().bind(Bindings.format("%s", "Incorrect:"));

	}

	// answer fields check point
	private void prepAnswerFields() {
		// Disable user interaction with these text fields
		correctGuessField.setDisable(true);
		incorrectGuessField.setDisable(true);
		// Customize text fields
		correctGuessField.getStyleClass().add("custom");
		incorrectGuessField.getStyleClass().add("custom");
		// Initialize Correct Guess fields
		initAnswerFields();
	}
	// initializing the answer for text field
	private void initAnswerFields() {
		correctGuessField.setText("");
		incorrectGuessField.setText("");
		for (int i = 0; i < game.getAnswer().length(); i++) {
			if (i == game.getAnswer().length() -1) {
				correctGuessField.setText(correctGuessField.getText() + "_");
			}
			else {
				correctGuessField.setText(correctGuessField.getText() + "_ ");
			}
		}
	}
	private void drawHangman() {

		Line line = new Line();
		line.setStartX(25.0f);
		line.setStartY(0.0f);
		line.setEndX(25.0f);
		line.setEndY(25.0f);

		Circle c = new Circle();
		c.setRadius(10);

		board.getChildren().add(line);
		board.getChildren().add(c);

	}
		
	@FXML 
	private void newHangman() throws IOException {
		game.reset();
	}

	@FXML
	private void quit() {
		board.getScene().getWindow().hide();
	}

}