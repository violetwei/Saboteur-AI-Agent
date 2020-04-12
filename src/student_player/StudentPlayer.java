package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;
import Saboteur.cardClasses.*;
import Saboteur.SaboteurMove;


/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("Version1");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        MyTools.getSomething();

        // Is random the best you can do?
        //Move myMove = boardState.getRandomMove();

        System.out.println("The board state turn player is: " + boardState.getTurnPlayer());
        System.out.println("Player Id: " + this.player_id);
        
        if (boardState.getTurnNumber() <= 1) {
            Move myMove = boardState.getRandomMove();
            return myMove;
        }
    	
    	Move myMove = null;
    	try {
        	myMove = MonteCarlo.getNextMove(boardState, this.player_id);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
        // Return your move to be processed by the server.
        return myMove;
    }
}