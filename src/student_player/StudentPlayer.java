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
        super("260664027");
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
        int nuggetY = MyTools.getGoalNuggetY(boardState.getHiddenBoard());
        int bestXPos = MyTools.findBestX(boardState.getHiddenIntBoard(), saboteurLegalMove);
        // Is random the best you can do?
        //Move myMove = boardState.getRandomMove();

        if (boardState.getNbMalus(boardState.getTurnPlayer()) > 0) {
            if (MyTools.containsCard(saboteurDeck, "Bonus")) {
                return MyTools.playBonusCard(saboteurDeck, boardState);
            } else if (MyTools.containsCard(saboteurDeck, "Map")) {
                return MyTools.playMapCard(saboteurDeck, boardState);
            } else {
                return MyTools.removeUnnecessaryCardFromDeck(saboteurDeck, boardState);
            }
        } 
        
        if (MyTools.containsCard(saboteurDeck, "Map")) {
            return MyTools.playMapCard(saboteurDeck, boardState);
        }

        // hard code strategy
        if (bestXPos > 8) {
            if (MyTools.containsCard(saboteurDeck, "Malus")) {
                SaboteurMove malusMove = MyTools.playMalusCard(saboteurDeck, boardState);
                return malusMove;
            }
        } 
        // update 
        bestXPos = Math.min(bestXPos, 12);

        if (bestXPos <= 10) {
            if (MyTools.tileCardAsNextMove(boardState.getHiddenIntBoard(), saboteurLegalMove, bestXPos) != null) {
                return MyTools.tileCardAsNextMove(boardState.getHiddenIntBoard(), saboteurLegalMove, bestXPos);
            }
        } else if (bestXPos == 11) {
            // MyTools.tileCardAsNextMoveSpecialCaseOne(boardState.getHiddenIntBoard(), saboteurLegalMove, bestXPos, nuggetY);
            if (MyTools.tileCardAsNextMoveSpecialCaseOne(boardState.getHiddenIntBoard(), saboteurLegalMove, bestXPos, nuggetY) != null) {
                return MyTools.tileCardAsNextMoveSpecialCaseOne(boardState.getHiddenIntBoard(), saboteurLegalMove, bestXPos, nuggetY);
            }
        } else if (bestXPos == 12) {
            if (MyTools.tileCardAsNextMoveSpecialCaseTwo(boardState.getHiddenIntBoard(), saboteurLegalMove, bestXPos, nuggetY) != null) {
                return MyTools.tileCardAsNextMoveSpecialCaseTwo(boardState.getHiddenIntBoard(), saboteurLegalMove, bestXPos, nuggetY);
            }
        } else {
            if (MyTools.tileCardAsNextMove(boardState.getHiddenIntBoard(), saboteurLegalMove, bestXPos) != null) {
                return MyTools.tileCardAsNextMove(boardState.getHiddenIntBoard(), saboteurLegalMove, bestXPos);
            }
        }
   
        if (MyTools.removeUnnecessaryCardFromDeck(saboteurDeck, boardState) != null) {
            return MyTools.removeUnnecessaryCardFromDeck(saboteurDeck, boardState);
        }

        // if the Saboteur deck contains a Destroy card and there is a dead end saboteur tile card
        if (MyTools.containsCard(saboteurDeck, "Destroy") && MyTools.existDeadEndSaboteurTile(boardState)) {
        	aiMove = MyTools.destroyDeadEndCardAndGetMove(boardState, objTileNum);
        	if (boardState.isLegal(aiMove)) {
                return aiMove;
            }
        }

        // If the Saboteur deck contains a Map card and the objective is unknown, then use a Map card
        if (MyTools.containsCard(saboteurDeck, "Map") && objTileNum == -1) {       	
        	int nextHiddenTile = MyTools.getNextHiddenTile(boardState);
            aiMove = new SaboteurMove(new SaboteurMap(), boardState.hiddenPos[nextHiddenTile][0], 
                                boardState.hiddenPos[nextHiddenTile][1], boardState.getTurnPlayer());
        	if (boardState.isLegal(aiMove)) {
                return aiMove;
            }
        }

        int x = 0, y = 0;
        
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
            myMove = MonteCarlo.getNextMove(boardState, boardState.getTurnPlayer());
            return myMove;
    	} catch (Exception e) {
			e.printStackTrace();
        }
        
        return boardState.getRandomMove();
    	
        // Return your move to be processed by the server.
    }
}