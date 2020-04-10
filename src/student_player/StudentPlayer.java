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
        //MyTools.getSomething();

        // Is random the best you can do?
        //Move myMove = boardState.getRandomMove();

        // Return your move to be processed by the server.
        //return myMove;

        if(boardState.getTurnNumber() == 0) {
    		MonteCarlo.readData();
    	}
    	if(boardState.getTurnNumber() <= 1) {
    		Move myMove = boardState.getRandomMove();
    		if (boardState.isLegal(new SaboteurMove(new SaboteurTile("8"), 7, 8, player_id))) {
    			myMove = new SaboteurMove(new SaboteurTile("8"), 7, 8, 1);
    		} else if(boardState.isLegal(new SaboteurMove(new SaboteurBonus(), 0, 0, player_id))) {
    			myMove = new SaboteurMove(new SaboteurBonus(), 0, 0, 1);
    		} else if(boardState.isLegal(new SaboteurMove(new SaboteurMap(), 12, 3, player_id))) {
    			myMove = new SaboteurMove(new SaboteurMap(), 12, 3, playerId);
    		} else if(boardState.isLegal(new SaboteurMove(new SaboteurDrop(), 4, 0, player_id))) {
    			myMove = new SaboteurMove(new SaboteurDrop(), 4, 0, playerId);
    		}
    		return myMove;
    	}
    	Move myMove = null;
    	try {
        	myMove = MonteCarlo.random(boardState, player_id);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
        // Return your move to be processed by the server.
        return myMove;
    }
}
