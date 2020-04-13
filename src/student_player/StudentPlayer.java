package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;

import java.util.ArrayList;

import Saboteur.SaboteurBoardState;
import Saboteur.cardClasses.*;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;


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

        System.out.println("The Saboteur board state turn player is: " + boardState.getTurnPlayer());
        System.out.println("Player Id: " + this.player_id);

        SaboteurMove aiMove;
        SaboteurPlayer aiPlayer;

    	ArrayList<SaboteurMove> saboteurLegalMove = boardState.getAllLegalMoves();
        ArrayList<SaboteurCard> saboteurDeck = boardState.getCurrentPlayerCards();

        int objTileNum = MyTools.getObjTileNum(boardState);
        // Is random the best you can do?
        //Move myMove = boardState.getRandomMove();

        // if the Saboteur deck contains a Destroy card and there is a dead end saboteur tile card
        if (MyTools.containsCard(saboteurDeck, "Destroy") && MyTools.existDeadEndSaboteurTile(boardState)) {
        	aiMove = MyTools.destroyDeadEndCardAndGetMove(boardState, objTileNum);
        	if (boardState.isLegal(aiMove)) {
                return aiMove;
            }
        }

        // If the Saboteur deck contains a Map card and ai doesn't know about the objective, then use a Map card
        if (MyTools.containsCard(saboteurDeck, "Map") && objTileNum == -1) {       	
        	int nextHiddenTile = MyTools.getNextHiddenTile(boardState);
            aiMove = new SaboteurMove(new SaboteurMap(), boardState.hiddenPos[nextHiddenTile][0], 
                                boardState.hiddenPos[nextHiddenTile][1], boardState.getTurnPlayer());
        	if (boardState.isLegal(aiMove)) {
                return aiMove;
            }
        }

        int x = 0, y = 0;

        // the case which the ai will play a Bonus card
        if (MyTools.containsCard(saboteurDeck, "Bonus") && boardState.getNbMalus(boardState.getTurnPlayer()) > 0) {
            aiMove = new SaboteurMove(new SaboteurBonus(), x, y, boardState.getTurnPlayer());
        	return aiMove;   	
        }
        
        // if the Saboteur deck contains Malus, then ai will play the Malus card
        if (MyTools.containsCard(saboteurDeck, "Malus")) {
        	aiMove = new SaboteurMove(new SaboteurMalus(), x, y, boardState.getTurnPlayer());
        	return aiMove;
        }
        
        /*if (boardState.getTurnNumber() <= 1) {
            Move aiMove = boardState.getRandomMove();
            return aiMove;
        }*/
    	
    	Move myMove = null;
    	try {
        	myMove = MonteCarlo.getNextMove(boardState, this.player_id);
    	} catch (Exception e) {
			e.printStackTrace();
        }
        
        if (myMove == null) {
            myMove = boardState.getRandomMove();
        }
    	
        // Return your move to be processed by the server.
        return myMove;
    }
}