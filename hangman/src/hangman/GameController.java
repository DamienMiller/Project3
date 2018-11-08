

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
import javafx.scene.shape.Ellipse;

public class GameController {

	private final ExecutorService executorService;
	private final Game game;
	private int bodyPartNumber;
	private Pane stickFigure;

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
                        addBodyPart();
                        } catch(Exception e) {
                            game.makeMove(oldValue);

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

		Circle c = new Circle(236, 0, 10);

		board.getChildren().add(line);
		board.getChildren().add(c);

		Line body = new Line();
		body.setStartX(0);
		body.setStartY(c.getCenterY());
		body.setEndX(0.0f);
		body.setEndY(45.0f);

		Line beakOne = new Line(230, -5, 220, 12);
		Line beakTwo = new Line(230, 8, 220, 12);
		Line neck = new Line(236, 0, 260, 30);

		Line armR = new Line();
		armR.setStartX(0);
		armR.setStartY(c.getCenterY());
		armR.setEndX(20.0f);
		armR.setEndY(45.0f);

		stickFigure.getChildren().add(c);
		stickFigure.getChildren().add(beakOne);
		stickFigure.getChildren().add(beakTwo);
		stickFigure.getChildren().add(neck);

	}

	public void addBodyPart() {
		if(bodyPartNumber == 0) {
			Circle c = new Circle();
			c.setRadius(10);
		} else if (bodyPartNumber == 1) {

		} else if (bodyPartNumber == 2) {

		} else if (bodyPartNumber == 3) {

		} else if (bodyPartNumber == 4) {

		} else if (bodyPartNumber == 5) {

		} else if (bodyPartNumber == 6) {

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