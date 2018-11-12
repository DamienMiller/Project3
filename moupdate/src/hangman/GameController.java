package src.hangman;



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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class GameController {
	private int bodyPartNumber;
	private Pane stickFigure;

	private Circle head;
	private Line beakOne;
	private Line beakTwo;
	private Line neck;
	private Ellipse body;
	private Line tail;
	private Line legL;
	private Line legR;
	private final ExecutorService executorService;
	private final Game game;
	private int numwrong = 0;

	public GameController(Game game) {
		this.game = game;
		stickFigure = new Pane();
		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	public static GameController getInstance() {
		return null;
	}


	@FXML
	private VBox board ;
	@FXML
	private Label statusLabel ;
	@FXML
	private Label enterALetterLabel ;
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

    public void initialize() throws IOException {
		System.out.println("in initialize");
		drawHangman(numwrong);
		addTextBoxListener();
		setUpStatusLabelBindings();
		prepAnswerFields();
		board.getChildren().add(stickFigure);
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
						numwrong++;
						drawHangman(numwrong);
						if(numwrong <= 9) {
							if (tempField.equals("")) {
								tempField = newValue;
								incorrectGuessField.setText(tempField);
							}
							else {
								tempField = tempField + " " + newValue;
								incorrectGuessField.setText(tempField);
							}
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
		correctGuessLabel.textProperty().bind(Bindings.format("%s", "Correct:"));
		incorrectGuessLabel.textProperty().bind(Bindings.format("%s", "Incorrect:"));
		/*	Bindings.when(
					game.currentPlayerProperty().isNotNull()
			).then(
				Bindings.format("To play: %s", game.currentPlayerProperty())
			).otherwise(
				""
			)
		);
		*/
	}

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

	private void drawHangman(int wrong) {

		Line line = new Line();
		line.setStartX(25.0f);
		line.setStartY(0.0f);
		line.setEndX(25.0f);
		line.setEndY(25.0f);

		//stickFigure.getChildren().add(line);

		head = new Circle(236, 0, 10);
		beakOne = new Line(230, -5, 220, 12);
		beakTwo = new Line(230, 8, 220, 12);
		neck = new Line(236, 0, 260, 30);
		body = new Ellipse(260, 33, 30, 18);
		tail = new Line(280, 28, 310, 10);
		legL = new Line(250, 40, 240, 60);
		legR = new Line(270, 40, 280, 60);

		Line rope = new Line(236, -100, 236, 0);
		Rectangle base = new Rectangle(80, 100, 100, 10);
		Rectangle post = new Rectangle(125, -150, 10, 250);
		Rectangle overhang = new Rectangle(125, -150, 110, 10);
		Rectangle ropePost = new Rectangle(232, -150, 10, 50);
        if(wrong == 0) {
        	stickFigure.getChildren().add(base);
        	stickFigure.getChildren().add(post);
        	stickFigure.getChildren().add(overhang);
        	stickFigure.getChildren().add(ropePost);
        	stickFigure.getChildren().add(rope);
        }

        if(wrong == 1) {
        	stickFigure.getChildren().add(head);
        }

        if(wrong == 2) {
        	stickFigure.getChildren().add(beakOne);
        	stickFigure.getChildren().add(beakTwo);
        }

        if(wrong == 3) {
        	stickFigure.getChildren().add(neck);
        }

        if(wrong == 4) {
        	stickFigure.getChildren().add(body);
        }

        if(wrong == 5) {
        	stickFigure.getChildren().add(tail);
        }

        if(wrong == 6) {
        	stickFigure.getChildren().add(legL);
        }

        if(wrong == 7) {
        	stickFigure.getChildren().add(legR);
        }


	    }


	@FXML
	private void newHangman() {
		game.reset();
        board.getChildren().clear();
		numwrong = 0;
		drawHangman(numwrong);
		initAnswerFields();
	}

	@FXML
	private void quit() {
        board.getScene().getWindow().hide();
	}

}