

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

		Circle c = new Circle();
		c.setRadius(10);

		board.getChildren().add(line);
		board.getChildren().add(c);

	}

	public void addBodyPart() {
		Line line = new Line();
		line.setStartX(35.0f);
		line.setStartY(0.0f);
		line.setEndX(35.0f);
		line.setEndY(35.0f);

		board.getChildren().add(line);
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