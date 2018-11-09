package hangman;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {

    private static String answer;
    private final ReadOnlyObjectWrapper<GameStatus> gameStatus;
    private String tmpAnswer;
    private String[] letterAndPosArray;
    //private String[] words;
    private ArrayList<String> words = new ArrayList<String>();
    private Scanner scan;
    private int moves;
    //private int count;
    //private ReadOnlyObjectWrapper moves;
    private int index;
    private ObjectProperty<Boolean> gameState = new ReadOnlyObjectWrapper<Boolean>();
    private Stage primaryStage;


    public Game() {
        //this.primaryStage = primaryStage;
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
        fillArrayOfWords();
        setRandomWord();
        prepTmpAnswer();
        prepLetterAndPosArray();
        //count = numOfTries();
        //moves = new ReadOnlyObjectWrapper(this, "moves", count);
        moves = 0;

        gameState.setValue(false); // initial state
        createGameStatusBinding();
    }

        public static void log(String s) {
        System.out.println(s);
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
                if (check != null) {
                    return check;
                }

                if (tmpAnswer.trim().length() == 0) {
                    log("new game");
                    return GameStatus.OPEN;
                } else if (index != -1) {
                    log("good guess");
                    return GameStatus.GOOD_GUESS;
                } else {
                    moves++;
                    log("bad guess");
                    return GameStatus.BAD_GUESS;
                    //printHangman();
                }
            }
        };
        gameStatus.bind(gameStatusBinding);
        //moves = getMoves();
    }



    public String getAnswer() {
        return answer;
    }

    public String[] getAnsArray() {
        return letterAndPosArray;
    }

    private void setRandomWord() {
        //int idx = (int) (Math.random() * words.length);
        //answer = "apple";//words[idx].trim(); // remove new line character
        answer = words.get(((int) (Math.random() * words.size())));
        log(answer);
    }

    private void fillArrayOfWords() {
        try {
            scan = new Scanner(new File("words.txt"));
        } catch (FileNotFoundException e) {
            log("File not found");
        }
        while(scan.hasNext()){
            words.add(scan.nextLine());
        }
    }
        private void prepTmpAnswer () {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < answer.length(); i++) {
                sb.append(" ");
            }
            tmpAnswer = sb.toString();
        }

        private void prepLetterAndPosArray () {
            letterAndPosArray = new String[answer.length()];
            for (int i = 0; i < answer.length(); i++) {
                letterAndPosArray[i] = answer.substring(i, i + 1);
            }
        }

        private int getValidIndex (String input){
            int index = -1;
            for (int i = 0; i < letterAndPosArray.length; i++) {
               if( letterAndPosArray[i].equalsIgnoreCase(input)) {
                    index = i;
                    letterAndPosArray[i] = "";
                    break;
                }
            }
            return index;
        }

        private int update (String input){
            int index = getValidIndex(input);
            if (index != -1) {
                StringBuilder sb = new StringBuilder(tmpAnswer);
                sb.setCharAt(index, input.charAt(0));
                tmpAnswer = sb.toString();
            }
            return index;
        }

        private static void drawHangmanFrame () {
        }


        public void makeMove (String letter){
            log("\nin makeMove: " + letter);
            index = update(letter);
            // this will toggle the state of the game
            gameState.setValue(!gameState.getValue());
            //	if(!(letter.length() > 1) && letter.matches("[a-zA-Z]+")) {
            //log("\nin makeMove: " + letter);
            //	index = update(letter);
            // this will toggle the state of the game
            //	gameState.setValue(!gameState.getValue());
        }


    public void reset() throws IOException {
            //primaryStage.close();
      //  final Game game = new Game(primaryStage);
        //loadsUI(game);
        index = 0;
        setRandomWord();
        prepTmpAnswer();
        prepLetterAndPosArray();
        moves = 0;

        gameState.setValue(false);
        createGameStatusBinding();
    }

    public int numOfTries() {
        return 5; // TODO, fix me
    }

    private GameStatus checkForWinner(int status) {
        log("in checkForWinner");
        if(tmpAnswer.equalsIgnoreCase(answer)) {
            log("won");
            return GameStatus.WON;
        }
        else if(moves == numOfTries()) {
            log("game over");
            return GameStatus.GAME_OVER;
        }
        else {
            return null;
        }
    }

    public ReadOnlyObjectProperty<GameStatus> gameStatusProperty() {
        return gameStatus.getReadOnlyProperty();
    }
    public GameStatus getGameStatus() {
        return gameStatus.get();
    }
    /*public ReadOnlyObjectProperty getMoves() {
        return moves.getReadOnlyProperty();
    }
*/
    public enum GameStatus {
        GAME_OVER {
            @Override
            public String toString() {
                String gameOver = "Game Over\nCorrect Answer: " + answer;
                return gameOver;
            }
        },
        BAD_GUESS {
            @Override
            public String toString() {
                return "Bad guess...";
            }
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
}
