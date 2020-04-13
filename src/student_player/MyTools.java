package student_player;

import java.util.*;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.*;

// this class contains a bunch of helper methods
public class MyTools {

    public static double getSomething() {
        return Math.random();
    }

    // the list of Saboteur tiles without a dead end (observed from the Saboteur tiles' pics in the tiles folder)
    private static final String[] saboteurTilesWithoutDeadEnd = {"0", "5", "5_flip", "6", "6_flip", "7_flip", "8", "9", "9_flip", "10"};
    private static final List<String> withoutDeadEndSaboteurTiles = Arrays.asList(saboteurTilesWithoutDeadEnd);
    
    // the list of Saboteur tiles with a dead end (observed from the Saboteur tiles' pics in the tiles folder)
	private static final String[] saboteurTilesWithDeadEnd = {"1", "2", "2_flip", "3", "3_flip", "4", "4_flip", "11", "11_flip", "12", "12_flip", "13", "14", "14_flip", "15"};
	private static final List<String> withDeadEndSaboteurTiles = Arrays.asList(saboteurTilesWithDeadEnd);

    public static boolean containsCard(ArrayList<SaboteurCard> saboteurDeck, String expectCardType) {
    	List<String> cards = getSaboteurCardNamesInDeck(saboteurDeck);
    	for (int i = 0; i < cards.size(); i++) {
            // Eg: "Tile:3", "Map", "Malus"
    		String saboteurCardType = cards.get(i).split(":")[0];
    		if (saboteurCardType.contains(expectCardType)) {
                return true;   	
            }	
    	}
    	return false;
    }

    public static List<String> getSaboteurCardNamesInDeck(ArrayList<SaboteurCard> saboteurDeck){
    	List<String> cards = new ArrayList<String>();
    	for (int i = 0; i < saboteurDeck.size(); i++) {
            String saboteurCardName = saboteurDeck.get(i).getName();
    		cards.add(saboteurCardName);
    	}
    	return cards;
    }

    public static int getObjTileNum(SaboteurBoardState boardState) {
        SaboteurTile hiddenTile = boardState.getHiddenBoard()[boardState.hiddenPos[1][0]][boardState.hiddenPos[1][1]];
        String hiddenTileIdx = hiddenTile.getIdx();
    	if (hiddenTileIdx.equals("hidden2") || getNextHiddenTile(boardState) == 2) {
            return 2;
        }
    	for (int i = 0; i <= 2; i++) {
            SaboteurTile hiddenSaboteurTile = boardState.getHiddenBoard()[boardState.hiddenPos[i][0]][boardState.hiddenPos[i][1]];
            String hiddenSaboteurTileIdx = hiddenSaboteurTile.getIdx();
    		if(hiddenSaboteurTileIdx.equals("nugget")) {
    			return i;
    		}
    	}    	 	
    	return -1;    	
    }
    
    public static int getNextHiddenTile(SaboteurBoardState boardState) {
    	for (int i = 0; i <= 2; i++) {
            SaboteurTile hiddenSaboteurTile = boardState.getHiddenBoard()[boardState.hiddenPos[i][0]][boardState.hiddenPos[i][1]];
            // if unrevealed, it will be "tile 8" instead of hidden 1, hidden 2 or nugget
            String hiddenSaboteurTileName = hiddenSaboteurTile.getName();
    		if (hiddenSaboteurTileName.contains("8")) {
    			return i;
    		}
    	}  	
    	return 0;
    }

    public static boolean containsSaboteurTileWithoutDeadEnd(ArrayList<SaboteurMove> curLegalMoves) {
        boolean contains = false;
    	for (int i = 0; i < curLegalMoves.size(); i++) {
            SaboteurCard curSaboteurCard = curLegalMoves.get(i).getCardPlayed();
    		if (curSaboteurCard instanceof SaboteurTile) {
                SaboteurTile sabTile = (SaboteurTile)curLegalMoves.get(i).getCardPlayed();
                String sabTileIdx = sabTile.getIdx();
    			if (withoutDeadEndSaboteurTiles.contains(sabTileIdx))  {
                    contains = true;
                }
    		}
    	}
    	return contains;
    }

    public static SaboteurMove destroyDeadEndCardAndGetMove(SaboteurBoardState boardState, int objTileNum) {
        int optimalMoveIdx = 0;
		ArrayList<SaboteurMove> allLegalMoves = boardState.getAllLegalMoves();
		if (objTileNum == -1) {
            objTileNum = 1; 
        }

        int[] targetPos = new int[2];
        targetPos[0] = boardState.hiddenPos[objTileNum][0];
        targetPos[1] = boardState.hiddenPos[objTileNum][1];
        
        int min = 500;
		for (int i = 0; i < allLegalMoves.size(); i++) {
            SaboteurMove curMove = allLegalMoves.get(i);
            SaboteurCard curSaboteurCard = curMove.getCardPlayed();
            // if the current Saboteur card is a Destroy card
    		if (curSaboteurCard instanceof SaboteurDestroy) {
    			int[] posPlayed = curMove.getPosPlayed();
                SaboteurTile saboteurTileToBeDestroyed = boardState.getHiddenBoard()[posPlayed[0]][posPlayed[1]];
                String saboteurTileToBeDestroyedIdx = saboteurTileToBeDestroyed.getIdx();
    			if (withDeadEndSaboteurTiles.contains(saboteurTileToBeDestroyedIdx)) {
    				int distance = getDistanceBetweenMoveAndTarget(curMove, targetPos);
		    		// update optimalMove
		    		if (distance < min) {
		    			optimalMoveIdx = i;
		    			min = distance;
		    		}
    			}
    			
    		}
    	}
		return allLegalMoves.get(optimalMoveIdx);
    }
    
    public static int getDistanceBetweenMoveAndTarget(SaboteurMove move, int[] targetPos) {
        int[] saboteurMovePos = new int[2];
        saboteurMovePos[0] = move.getPosPlayed()[0];
        saboteurMovePos[1] = move.getPosPlayed()[1];
        int xDiff = Math.abs(saboteurMovePos[0] - targetPos[0]);
        int yDiff = Math.abs(saboteurMovePos[1] - targetPos[1]);
		int distance = (int)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
		return distance;
	}
	
	public static boolean existDeadEndSaboteurTile(SaboteurBoardState boardState) {
        boolean isExists = false;
		ArrayList<SaboteurMove> allLegalMoves = boardState.getAllLegalMoves();
		for (int i = 0; i < allLegalMoves.size(); i++) {
            SaboteurMove move = allLegalMoves.get(i);
            SaboteurCard curSaboteurCard = move.getCardPlayed();
            // if current Saboteur card is a Destroy card
    		if (curSaboteurCard instanceof SaboteurDestroy) {
    			int[] posPlayed = move.getPosPlayed();
                SaboteurTile saboteurTileToBeDestroyed = boardState.getHiddenBoard()[posPlayed[0]][posPlayed[1]];
                String saboteurTileToBeDestroyedIdx = saboteurTileToBeDestroyed.getIdx();
    			if (posPlayed[0] > 5 && containsSaboteurTileWithDeadEnd(saboteurTileToBeDestroyedIdx)) {
    				isExists = true;
	    		}
    		}		
    	}
		return isExists;
    }

    public static boolean containsSaboteurTileWithDeadEnd(String saboteurTileIdx) {
        boolean contains = withDeadEndSaboteurTiles.contains(saboteurTileIdx);
    	return contains;
    }
}