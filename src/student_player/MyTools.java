package student_player;

import java.util.*;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.*;


public class MyTools {
    public static double getSomething() {
        return Math.random();
    }

    private static final String[] tilesNoDeadEnd = {"0", "5", "5_flip", "6", "6_flip", "7_flip", "8", "9", "9_flip", "10"};
	private static final List<String> tilesNoDeadEndList = Arrays.asList(tilesNoDeadEnd);
	private static final String[] tilesDeadEnd = {"1", "2", "2_flip", "3", "3_flip", "4", "4_flip", "11", "11_flip","12", 
			"12_flip", "13", "14", "14_flip", "15"};
	private static final List<String> tilesDeadEndList = Arrays.asList(tilesDeadEnd);

    public static boolean containsCard(ArrayList<SaboteurCard> saboteurDeck, String cardTypeName) {
    	List<String> cards = getCardsName(saboteurDeck);
    	for (int i = 0; i < cards.size(); i++) {
    		String cardType = cards.get(i).split(":")[0];
    		if (cardType.contains(cardTypeName)) {
                return true;   	
            }	
    	}
    	return false;
    }

    public static List<String> getCardsName(ArrayList<SaboteurCard> saboteurDeck){
    	List<String> cards = new ArrayList<String>();
    	for (int i = 0; i < saboteurDeck.size(); i++) {
    		cards.add(i, saboteurDeck.get(i).getName());
    	}
    	return cards;
    }

    public static int getObjective(SaboteurBoardState boardState) {
    	if (getNextTileHidden(boardState) == 2 || 
    			boardState.getHiddenBoard()[boardState.hiddenPos[1][0]][boardState.hiddenPos[1][1]].getIdx().equals("hidden2")) return 2;
    	for (int i = 0; i <= 2; i++) {
    		SaboteurTile hiddenTile = boardState.getHiddenBoard()[boardState.hiddenPos[i][0]][boardState.hiddenPos[i][1]];
    		if(hiddenTile.getIdx().equals("nugget")) {
    			return i;
    		}
    	}    	 	
    	return -1;    	
    }
    
    public static int getNextTileHidden(SaboteurBoardState boardState) {
    	for (int i = 0; i <= 2; i++) {
    		SaboteurTile hiddenSaboteurTile = boardState.getHiddenBoard()[boardState.hiddenPos[i][0]][boardState.hiddenPos[i][1]];
    		if(hiddenSaboteurTile.getIdx() == "8") {
    			return i;
    		}
    	}  	
    	return 0;
    }

    public static boolean hasTileWithNoDeadEndCard(ArrayList<SaboteurMove> myLegalMoves) {
    	
    	for (int i = 0; i < myLegalMoves.size(); i++) {
    		if (myLegalMoves.get(i).getCardPlayed() instanceof SaboteurTile) {
    			SaboteurTile tile = (SaboteurTile) myLegalMoves.get(i).getCardPlayed();
    			if(tilesNoDeadEndList.contains(tile.getIdx())) return true;
    		}
    	}
    	
    	return false;
    }

    public static SaboteurMove destroyDeadEndAndGetMove(SaboteurBoardState boardState, int objTileNum) {
		ArrayList<SaboteurMove> myLegalMoves = boardState.getAllLegalMoves();
		int minDistance = 1000;
		int bestMove = 0;
		if (objTileNum == -1) objTileNum = 1; //if objective is unknown, Goal is middle tile
		int[] goalXY = {boardState.hiddenPos[objTileNum][0], boardState.hiddenPos[objTileNum][1]};
		
		for (int i = 0; i < myLegalMoves.size(); i++) {
			SaboteurMove move = myLegalMoves.get(i);

    		if(move.getCardPlayed() instanceof SaboteurDestroy){
    			int[] posMov = move.getPosPlayed();
    			SaboteurTile tileToDestroy = boardState.getHiddenBoard()[posMov[0]][posMov[1]];
    			if (tilesDeadEndList.contains(tileToDestroy.getIdx())) {
    				int newDistance = getDistance(move, goalXY);
		    		
		    		if (newDistance < minDistance) {
		    			bestMove = i;
		    			minDistance = newDistance;
		    		}
    			}
    			
    		}
    	}
		
		return myLegalMoves.get(bestMove);
    }
    
    public static int getDistance(SaboteurMove move, int[] goalXY) {
		int[] moveXY = {move.getPosPlayed()[0], move.getPosPlayed()[1]};
		int distance = Math.abs(moveXY[0] - goalXY[0]) + Math.abs(moveXY[1] - goalXY[1]);
		return distance;
	}
	
	public static boolean existDeadEnd(SaboteurBoardState boardState) {
		ArrayList<SaboteurMove> myLegalMoves = boardState.getAllLegalMoves();
		for (int i = 0; i < myLegalMoves.size(); i++) {
			SaboteurMove move = myLegalMoves.get(i);
    		if(move.getCardPlayed() instanceof SaboteurDestroy){
    			int[] posMov = move.getPosPlayed();
    			SaboteurTile tileToDestroy = boardState.getHiddenBoard()[posMov[0]][posMov[1]];
    			if (tilesDeadEndList.contains(tileToDestroy.getIdx()) && move.getPosPlayed()[0] > 5) {
    				return true;
	    		}
    		}
    			
    	}
		return false;
    }
}