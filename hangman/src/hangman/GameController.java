

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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;

public class GameController {

	private final ExecutorService executorService;
	private final Game game;
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

	public GameController(Game game) {
		this.game = game;
		bodyPartNumber = 0;
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

	@FXML
	private VBox board ;
	@FXML
	private Label statusLabel ;
	@FXML
	private Label enterALetterLabel ;
    @FXML
    private Label movesLeftLabel ;
	@FXML
	private TextField textField ;
	@FXML
	private VBox titleBox;
	@FXML
	private Label title;

    public void initialize() throws IOException {
		System.out.println("in initialize");
		drawHangman();
		addTextBoxListener();
		setUpStatusLabelBindings();
		board.getChildren().add(stickFigure);
	}

	private void addTextBoxListener() {
        //textField.clear();
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov,
                                final String oldValue, final String newValue) {
				if(newValue.length() >= 1) {
                    if (!newValue.matches("\\sa-zA-Z*")) {
                        try{
                        textField.setText(textField.getText().substring(0, 1));
                        //textField.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
                        game.makeMove(newValue);
                        if (game.getGameStatus() == game.getGameStatus().BAD_GUESS) {
                        	addBodyPart();
                        	bodyPartNumber++;
                        }
                        } catch(Exception e) {
                            game.makeMove(oldValue);
                            System.out.println(e);
                        }

                    }
				}

			}
		});

	}

	private void setUpStatusLabelBindings() {

		System.out.println("in setUpStatusLabelBindings");
		statusLabel.textProperty().bind(Bindings.format("%s", game.gameStatusProperty()));
		enterALetterLabel.textProperty().bind(Bindings.format("%s", "Enter a letter:"));
        movesLeftLabel.textProperty().bind(Bindings.format("You have %s moves left", game.getMoves()));

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

	private void drawHangman() {

		Line line = new Line();
		line.setStartX(25.0f);
		line.setStartY(0.0f);
		line.setEndX(25.0f);
		line.setEndY(25.0f);

		board.getChildren().add(line);

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

		stickFigure.getChildren().add(base);
		stickFigure.getChildren().add(post);
		stickFigure.getChildren().add(overhang);
		stickFigure.getChildren().add(ropePost);
		stickFigure.getChildren().add(rope);
	}

	public void addBodyPart() {
		if(bodyPartNumber == 0) {
			stickFigure.getChildren().add(head);
		} else if (bodyPartNumber == 2) {
			stickFigure.getChildren().add(beakOne);
			stickFigure.getChildren().add(beakTwo);
		} else if (bodyPartNumber == 4) {
			stickFigure.getChildren().add(neck);
		} else if (bodyPartNumber == 6) {
			stickFigure.getChildren().add(body);
		} else if (bodyPartNumber == 8) {
			stickFigure.getChildren().add(tail);
		} else if (bodyPartNumber == 10) {
			stickFigure.getChildren().add(legL);
		} else if (bodyPartNumber == 11) {
			stickFigure.getChildren().add(legR);
		}
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