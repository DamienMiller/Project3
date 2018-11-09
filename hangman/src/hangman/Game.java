package hangman;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

	private String answer;
	private String tmpAnswer;
	private String[] letterAndPosArray;
	private List words;
	private int count;
	private ReadOnlyObjectWrapper moves;
	private int index;
	private final ReadOnlyObjectWrapper<GameStatus> gameStatus;
	private ObjectProperty<Boolean> gameState = new ReadOnlyObjectWrapper<Boolean>();
	private Stage primaryStage;


	public enum GameStatus {
		GAME_OVER {
			@Override
			public String toString() {
				return "Game over!";
			}
		},
		BAD_GUESS {
			@Override
			public String toString() { return "Bad guess..."; }
		},
		GOOD_GUESS {
			@Override
			public String toString() {
				return "Good guess!";
			}
		},
		WON {
			@Override
			public String toString() {
				return "You won!";
			}
		},
		OPEN {
			@Override
			public String toString() {
				return "Game on, let's go!";
			}
		}
	}

	public Game(Stage primaryStage) {
		this.primaryStage = primaryStage;
		gameStatus = new ReadOnlyObjectWrapper<GameStatus>(this, "gameStatus", GameStatus.OPEN);
		gameStatus.addListener(new ChangeListener<GameStatus>() {
			@Override
			public void changed(ObservableValue<? extends GameStatus> observable,
								GameStatus oldValue, GameStatus newValue) {
				if (gameStatus.get() != GameStatus.OPEN) {
					log("in Game: in changed");
					//currentPlayer.set(null);
				}
			}

		});
		openWordFile();
		setRandomWord();
		prepTmpAnswer();
		prepLetterAndPosArray();
		count = numOfTries();
		moves = new ReadOnlyObjectWrapper(this, "moves", count);
		//moves.setValue(count);

		gameState.setValue(false); // initial state
		createGameStatusBinding();
	}

	private void createGameStatusBinding() {
		List<Observable> allObservableThings = new ArrayList<>();
		ObjectBinding<GameStatus> gameStatusBinding = new ObjectBinding<GameStatus>() {
			{
				super.bind(gameState);
			}
			@Override
			public GameStatus computeValue() {
				log("in computeValue");
				GameStatus check = checkForWinner(index);
				if(check != null ) {
					return check;
				}

				if(tmpAnswer.trim().length() == 0){
					log("new game");
					return GameStatus.OPEN;
				}
				else if (index != -1){
					log("good guess");
					return GameStatus.GOOD_GUESS;
				}
				else {
					if(count == numOfTries())
							count--;
					moves.set(count--);
					log("bad guess");
					return GameStatus.BAD_GUESS;
					//printHangman();
				}
			}
		};
		gameStatus.bind(gameStatusBinding);
		//moves = getMoves();
	}

	public ReadOnlyObjectProperty<GameStatus> gameStatusProperty() {
		return gameStatus.getReadOnlyProperty();
	}
	public GameStatus getGameStatus() {
		return gameStatus.get();
	}

	private void openWordFile() {
		try{
			BufferedReader reader = new BufferedReader(new FileReader("words.txt"));
			String line = reader.readLine();
			words = new ArrayList<String>();
			while(line != null) {
				String[] wordsLine = line.split(" ");
				for(String word : wordsLine) {
					words.add(word);
				}
				line = reader.readLine();
			}


		} catch (Exception e) {
			System.out.println("Exception occurred");
		}

	}

	private void setRandomWord() {
		Random rand = new Random(System.currentTimeMillis());
		answer = words.get(rand.nextInt(words.size())).toString();
		log(answer);
		//int idx = (int) (Math.random() * words.length);
		//answer = "apple";//words[idx].trim(); // remove new line character
	}

	private void prepTmpAnswer() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < answer.length(); i++) {
			sb.append(" ");
		}
		tmpAnswer = sb.toString();
	}

	private void prepLetterAndPosArray() {
		letterAndPosArray = new String[answer.length()];
		for(int i = 0; i < answer.length(); i++) {
			letterAndPosArray[i] = answer.substring(i,i+1);
		}
	}

	private int getValidIndex(String input) {
		int index = -1;
		for(int i = 0; i < letterAndPosArray.length; i++) {
			if(letterAndPosArray[i].equals(input)) {
				index = i;
				letterAndPosArray[i] = "";
				break;
			}
		}
		return index;
	}

	private int update(String input) {
		int index = getValidIndex(input);
		if(index != -1) {
			StringBuilder sb = new StringBuilder(tmpAnswer);
			sb.setCharAt(index, input.charAt(0));
			tmpAnswer = sb.toString();
		}
		return index;
	}

	private static void drawHangmanFrame() {}

	public void makeMove(String letter) {
	    if(!(letter.length() > 1) && letter.matches("[a-zA-Z]+")) {
            log("\nin makeMove: " + letter);
            index = update(letter);
            // this will toggle the state of the game
            gameState.setValue(!gameState.getValue());
        }
	}

	public void loadsUI(Game game) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Hangman.fxml"));
		loader.setController(new GameController(game));
		Parent root = loader.load();
		Scene scene = new Scene(root, 500, 800);
		scene.getStylesheets().add(getClass().getResource("Hangman.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public void reset() throws IOException {
		primaryStage.close();
		final Game game = new Game(primaryStage);
		loadsUI(game);
	}

	public int numOfTries() {
		return answer.length();
	}

	public static void log(String s) {
		System.out.println(s);
	}

	private GameStatus checkForWinner(int status) {
		log("in checkForWinner");
		if(tmpAnswer.equals(answer)) {
			log("won");
			return GameStatus.WON;
		}
		else if(count == 0) {
			log("game over");
			return GameStatus.GAME_OVER;
		}
		else {
			return null;
		}
	}
	public ReadOnlyObjectProperty getMoves() {
	    return moves.getReadOnlyProperty();
    }
}
