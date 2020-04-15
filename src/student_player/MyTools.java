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
    private static final List<String> withoutDeadEndSaboteurTiles = Arrays.asList(new String[]{"0", "5", "5_flip", "6", "6_flip", "7_flip", "8", "9", "9_flip", "10"});
    
    // the list of Saboteur tiles with a dead end (observed from the Saboteur tiles' pics in the tiles folder)
    private static final List<String> withDeadEndSaboteurTiles = Arrays.asList(new String[]{"1", "2", "2_flip", "3", "3_flip", "4", "4_flip", "11", "11_flip", "12", "12_flip", "13", "14", "14_flip", "15"});
    

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

    public static SaboteurMove playDestroyCard(ArrayList<SaboteurCard> deck, SaboteurBoardState saboteurBoardState) {
        SaboteurMove destroyMove = null;
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            if (card instanceof SaboteurDestroy) {
                destroyMove = new SaboteurMove(card, 0, 0, saboteurBoardState.getTurnPlayer());
            }
        }
        return destroyMove;
    }

    public static SaboteurMove playBonusCard(ArrayList<SaboteurCard> deck, SaboteurBoardState saboteurBoardState) {
        SaboteurMove bonusMove = null;
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            if (card instanceof SaboteurBonus) {
                bonusMove = new SaboteurMove(card, 0, 0, saboteurBoardState.getTurnPlayer());
            }
        }
        return bonusMove;
    }

    public static SaboteurMove playMalusCard(ArrayList<SaboteurCard> deck, SaboteurBoardState saboteurBoardState) {
        SaboteurMove malusMove = null;
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            if (card instanceof SaboteurMalus) {
                malusMove = new SaboteurMove(card, 0, 0, saboteurBoardState.getTurnPlayer());
            }
        }
        return malusMove;
    }

    public static SaboteurMove playMapCard(ArrayList<SaboteurCard> deck, SaboteurBoardState saboteurBoardState) {
        SaboteurTile[][] hiddenBoard = saboteurBoardState.getHiddenBoard();
        SaboteurMove mapMove = null;
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            if (card instanceof SaboteurMap) {
                if (hiddenBoard[12][3].getIdx().equals("8")) {
                    mapMove = new SaboteurMove(card, 12, 3, saboteurBoardState.getTurnPlayer());
                } else if (hiddenBoard[12][5].getIdx().equals("8")) {
                    mapMove = new SaboteurMove(card, 12, 5, saboteurBoardState.getTurnPlayer());
                } else if (hiddenBoard[12][7].getIdx().equals("8")) {
                    mapMove = new SaboteurMove(card, 12, 7, saboteurBoardState.getTurnPlayer());
                }
            }
        }
        return mapMove;
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

    public static SaboteurMove removeUnnecessaryCardFromDeck(ArrayList<SaboteurCard> deck, SaboteurBoardState saboteurBoardState) {
        SaboteurMove removedMove = null;
        
        if (removeSaboteurTileCardFromDeck_TopPriority(deck, saboteurBoardState) != null) {
            return removeSaboteurTileCardFromDeck_TopPriority(deck, saboteurBoardState);
        }
        // check if we have a Destroy card
        if (containsCard(deck, "Destroy")) {
            for(int i = 0; i < deck.size(); i++) {
                String cname = deck.get(i).getName();
                if (cname.contains("Destroy")) {
                    removedMove = new SaboteurMove(new SaboteurDrop(), i, 0, saboteurBoardState.getTurnPlayer());
                    return removedMove;
                }
            }
        }
        removedMove = removeSaboteurTileCardFromDeck_NextPriority(deck, saboteurBoardState);
        return removedMove;
    }

    public static SaboteurMove removeSaboteurTileCardFromDeck_TopPriority(ArrayList<SaboteurCard> deck, SaboteurBoardState saboteurBoardState) {
        SaboteurMove removedMove = null;
        // search in the deck, find card: tile 13
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            String[] splits = card.getName().split(":");
            if (card instanceof SaboteurTile) {
                if (splits[1].equals("13")) {
                    removedMove = new SaboteurMove(new SaboteurDrop(), i, 0, saboteurBoardState.getTurnPlayer());
                    return removedMove;
                }
            }
        }
        // search in the deck, find card: tile 2 or tile 11
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            String[] splits = card.getName().split(":");
            if (card instanceof SaboteurTile) {
                if (splits[1].equals("2") || splits[1].equals("11")) {
                    removedMove = new SaboteurMove(new SaboteurDrop(), i, 0, saboteurBoardState.getTurnPlayer());
                    return removedMove;
                }
            }
        }
        // search in the deck, find card: tile 1, or tile 3, or tile 14, or tile 15
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            String[] splits = card.getName().split(":");
            if (card instanceof SaboteurTile) {
                if (splits[1].equals("1") || splits[1].equals("3") || splits[1].equals("14") || splits[1].equals("15")) {
                    removedMove = new SaboteurMove(new SaboteurDrop(), i, 0, saboteurBoardState.getTurnPlayer());
                    return removedMove;
                }
            }
        }
        // search in the deck, find card: tile 4 or tile 12
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            String[] splits = card.getName().split(":");
            if (card instanceof SaboteurTile) {
                if (splits[1].equals("4") || splits[1].equals("12")) {
                    removedMove = new SaboteurMove(new SaboteurDrop(), i, 0, saboteurBoardState.getTurnPlayer());
                    return removedMove;
                }
            }
        }
        return removedMove;
    }

    public static SaboteurMove playTileCardByPriority(ArrayList<SaboteurMove> sMoves) {
        // Order 1. Tile 8
        for (SaboteurMove move : sMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            if (tileNum.equals("8")) {
                return move;
            }
        }
        
        // Order 2. Tile 6, Tile 6_flip, Tile 9, Tile 9_flip
        for (SaboteurMove move : sMoves) {
            String num = move.getCardPlayed().getName().split(":")[1];
            if (num.equals("6") || num.equals("6_flip")
             || num.equals("9") || num.equals("9_flip")) {
                return move;
            }
        }
        
        // Order 3. Tile 0, Tile 10
        for (SaboteurMove move : sMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            if (tileNum.equals("0") || tileNum.equals("10")) {
                return move;
            }
        }
        
        // Order 4. Tile 5, Tile 5_flip, Tile 7, Tile 7_flip
        for (SaboteurMove move : sMoves) {
            String num = move.getCardPlayed().getName().split(":")[1];
            if (num.equals("5") || num.equals("5_flip")
             || num.equals("7") || num.equals("7_flip")) {
                return move;
            }
        }

        return null;
    }

    public static SaboteurMove removeSaboteurTileCardFromDeck_NextPriority(ArrayList<SaboteurCard> deck, SaboteurBoardState saboteurBoardState) {
        SaboteurMove removedMove = null;
        // search in the deck, find card: tile 5 or tile 7
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            String[] splits = card.getName().split(":");
            if (card instanceof SaboteurTile) {
                if (splits[1].equals("5") || splits[1].equals("7")) {
                    removedMove = new SaboteurMove(new SaboteurDrop(), i, 0, saboteurBoardState.getTurnPlayer());
                    return removedMove;
                }
            }
        }
        // search in the deck, find card: tile 10 or tile 0
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            String[] splits = card.getName().split(":");
            if (card instanceof SaboteurTile) {
                if (splits[1].equals("10") || splits[1].equals("0")) {
                    removedMove = new SaboteurMove(new SaboteurDrop(), i, 0, saboteurBoardState.getTurnPlayer());
                    return removedMove;
                }
            }
        }
        // search in the deck, find card: tile 6, or tile 9
        for (int i = 0; i < deck.size(); i++) {
            SaboteurCard card = deck.get(i);
            String[] splits = card.getName().split(":");
            if (card instanceof SaboteurTile) {
                if (splits[1].equals("6") || splits[1].equals("9")) {
                    removedMove = new SaboteurMove(new SaboteurDrop(), i, 0, saboteurBoardState.getTurnPlayer());
                    return removedMove;
                }
            }
        }
        return removedMove;
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

    public static int findBestX(int[][] board, ArrayList<SaboteurMove> saboteurMoves) {
        int max = Integer.MIN_VALUE;
        int[][] boardCopy = boardCopy(board);
        for (SaboteurMove move : saboteurMoves) {
            SaboteurCard card = move.getCardPlayed();
            int xMove = move.getPosPlayed()[0];
            
            if (card instanceof SaboteurTile) {
                if (isReachableToStartPoint(boardCopy, move)) {
                    if (xMove > max) {
                        max = xMove; // update the best
                    }
                }
            }
        }
        return max;
    }

    public static boolean isReachableToStartPoint(int[][] board, SaboteurMove sMove) {
        boolean isReachable = false;
        SaboteurTile tileCard = (SaboteurTile)sMove.getCardPlayed();
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                int xPos = i + 3 * sMove.getPosPlayed()[0];
                int yPos = j + 3 * sMove.getPosPlayed()[1];
                int[][] tilePath = tileCard.getPath();
                board[xPos][yPos] = tilePath[j][2-i];
            }
        }
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                int xPos = i + 3 * sMove.getPosPlayed()[0];
                int yPos = j + 3 * sMove.getPosPlayed()[1];
                if(board[xPos][yPos] == 1) {
                    int[] pos = new int[]{xPos, yPos};
                    ArrayList<int[]> previousMoves = new ArrayList<>();
                    int counter = searchInBoardHelper(board, pos, previousMoves);
                    if(counter != 0) {
                        isReachable = true;
                    }
                }
            }
        }
        return isReachable;
    }

    // recursive helper method
    public static int searchInBoardHelper(int[][] board, int[] pos, ArrayList<int[]> previousMoves) {
        if (pos[0] == 16 && pos[1] == 16) {
            return 1;
        }

        int count = 0;
        boolean upAvailable = true;
        boolean downAvailable = true;
        boolean leftAvailable = true;
        boolean rightAvailable = true;

        for (int[] preMove : previousMoves) {
            if (pos[0] == preMove[0]) {
                if ((pos[1] + 1) == preMove[1]) {
                    rightAvailable = false;
                }
            }
            if (pos[0] == preMove[0]) {
                if ((pos[1] - 1) == preMove[1]) {
                    leftAvailable = false;
                }
            }
            if ((pos[0] + 1) == preMove[0]) {
                if (pos[1] == preMove[1]) {
                    downAvailable = false;
                }
            }
            if ((pos[0] - 1) == preMove[0]) {
                if (pos[1] == preMove[1]) {
                    upAvailable = false;
                }
            }
        }

        // Right
        if (pos[1] < 41 && rightAvailable) {
            if (board[pos[0]][pos[1] + 1] == 1) {
                ArrayList<int[]> preMovesClone = (ArrayList<int[]>)previousMoves.clone();
                preMovesClone.add(pos);
                int[] newPos = new int[2];
                newPos[0] = pos[0];
                newPos[1] = pos[1] + 1;
                count += searchInBoardHelper(board, newPos, preMovesClone);
            }
        }

        // Left
        if (pos[1] > 0 && leftAvailable) {
            if (board[pos[0]][pos[1] - 1] == 1) {
                ArrayList<int[]> preMovesClone = (ArrayList<int[]>)previousMoves.clone();
                preMovesClone.add(pos);
                int[] newPos = new int[2];
                newPos[0] = pos[0];
                newPos[1] = pos[1] - 1;
                count += searchInBoardHelper(board, newPos, preMovesClone);
            }
        }

        // Up
        if (pos[0] > 0 && upAvailable) {
            if (board[pos[0] - 1][pos[1]] == 1) {
                ArrayList<int[]> preMovesClone = (ArrayList<int[]>)previousMoves.clone();
                preMovesClone.add(pos);
                int[] newPos = new int[2];
                newPos[0] = pos[0] - 1;
                newPos[1] = pos[1];
                count += searchInBoardHelper(board, newPos, preMovesClone);
            }
        }

        // Down
        if (pos[0] < 41 && downAvailable) {
            if (board[pos[0] + 1][pos[1]] == 1) {
                ArrayList<int[]> preMovesClone = (ArrayList<int[]>)previousMoves.clone();
                preMovesClone.add(pos);
                int[] newPos = new int[2];
                newPos[0] = pos[0] - 1;
                newPos[1] = pos[1];
                count += searchInBoardHelper(board, newPos, preMovesClone);
            }
        }

        return count;
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
		int distance = xDiff + yDiff;
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

    public static SaboteurMove tileCardAsNextMove(int[][] board, ArrayList<SaboteurMove> legalMoves, int xPos) {
        ArrayList<SaboteurMove> chosenMoves = getChosenMoves(board, legalMoves, xPos);
        // play card according to our ranking/consideration under different conditions
        return playTileCardByPriority(chosenMoves);
    }

    
    public static int getGoalNuggetY(SaboteurTile[][] board) {
        int y = 0;
        if (board[12][3].getIdx().equals("nugget")) {
            y = 3;
        }
        if (board[12][5].getIdx().equals("nugget")) {
            y = 5;
        }
        if (board[12][7].getIdx().equals("nugget")) {
            y = 7;
        }
        return y;
    }

    public static ArrayList<SaboteurMove> getChosenMoves(int[][] board, ArrayList<SaboteurMove> legalMoves, int xPos) {
        ArrayList<SaboteurMove> chosenMoves = new ArrayList<>();
        for (SaboteurMove move: legalMoves) {
            SaboteurCard card = move.getCardPlayed();
            int x = move.getPosPlayed()[0];
            int[][] copy = boardCopy(board);
            if (card instanceof SaboteurTile) {
                if (xPos == x && isReachableToStartPoint(copy, move)) {
                    chosenMoves.add(move);
                }
            }	
        }
        return chosenMoves;
    }

    // special case dealing with when x at 11
    public static SaboteurMove tileCardAsNextMoveSpecialCaseOne(int[][] board, ArrayList<SaboteurMove> legalMoves, int posX, int nugPosY) {
        ArrayList<SaboteurMove> chosenMoves = getChosenMoves(board, legalMoves, posX);

        // if the map card has been used
        if (nugPosY != 0) {
            // 1. choose tile 8
            for (SaboteurMove move : chosenMoves) {
                String tileNum = move.getCardPlayed().getName().split(":")[1];
                int y = move.getPosPlayed()[1];
                if (nugPosY == y) {
                    if (tileNum.equals("8")) {
                        return move;
                    }
                }
            }
            // 2. choose tile 6, 6_flip, 9
            for (SaboteurMove move : chosenMoves) {
                String tileNum = move.getCardPlayed().getName().split(":")[1];
                int y = move.getPosPlayed()[1];
                if (nugPosY == y) {
                    if (tileNum.equals("6") || tileNum.equals("6_flip") || tileNum.equals("9")) {
                        return move;
                    }
                }
            }
            for (SaboteurMove move : chosenMoves) {
                String tileNum = move.getCardPlayed().getName().split(":")[1];
                int y = move.getPosPlayed()[1];
                if (nugPosY == y) {
                    if (tileNum.equals("0")) {
                        return move;
                    }
                }
            }
            for (SaboteurMove move : chosenMoves) {
                String tileNum = move.getCardPlayed().getName().split(":")[1];
                int y = move.getPosPlayed()[1];
                if (nugPosY == y) {
                    if (tileNum.equals("5") || tileNum.equals("7_flip")) {
                        return move;
                    }
                }
            }
        }

        // iterate thorugh the chosenMoves again to find desired card
        // make move for conditions of y at 3 || y at 5 || y at 7
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            int y = move.getPosPlayed()[1];
            if (y == 3 || y == 5 || y == 7) {
                if (tileNum.equals("8")) {
                    return move;
                }
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            int y = move.getPosPlayed()[1];
            if (y == 3 || y == 5 || y == 7) {
                if (tileNum.equals("6") || tileNum.equals("6_flip") || tileNum.equals("9")) {
                    return move;
                }
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            int y = move.getPosPlayed()[1];
            if (y == 3 || y == 5 || y == 7) {
                if (tileNum.equals("0")) {
                    return move;
                }
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            int y = move.getPosPlayed()[1];
            if (y == 3 || y == 5 || y == 7) {
                if (tileNum.equals("5") || tileNum.equals("7_flip")) {
                    return move;
                }
            }
        }

        // iterate one more time to see if there is an optimal move can be made
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            if (tileNum.equals("8")) {
                return move;
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            if (tileNum.equals("6") || tileNum.equals("6_flip") || tileNum.equals("9")) {
                return move;
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            if (tileNum.equals("0")) {
                return move;
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            if (tileNum.equals("5") || tileNum.equals("7_flip")) {
                return move;
            }  
        }

        return null;
    }

    // special case dealing with when x at 12
    public static SaboteurMove tileCardAsNextMoveSpecialCaseTwo(int[][] board, ArrayList<SaboteurMove> legalMoves, int posX, int nugPosY) {
        ArrayList<SaboteurMove> chosenMoves = getChosenMoves(board, legalMoves, posX);

        // if the map card has been used
        if (nugPosY != 0) {
            // 1. choose tile 8
            for (SaboteurMove move : chosenMoves) {
                String tileNum = move.getCardPlayed().getName().split(":")[1];
                int y = move.getPosPlayed()[1];
                if ((nugPosY-1) == y || (nugPosY+1) == y) {
                    if (tileNum.equals("8")) {
                        return move;
                    }
                }
            }
            for (SaboteurMove move : chosenMoves) {
                String tileNum = move.getCardPlayed().getName().split(":")[1];
                int y = move.getPosPlayed()[1];
                if ((nugPosY-1) == y || (nugPosY+1) == y) {
                    if (tileNum.equals("9") || tileNum.equals("9_flip")) {
                        return move;
                    }
                }
            }
            for (SaboteurMove move : chosenMoves) {
                String tileNum = move.getCardPlayed().getName().split(":")[1];
                int y = move.getPosPlayed()[1];
                if ((nugPosY-1) == y || (nugPosY+1) == y) {
                    if (tileNum.equals("10")) {
                        return move;
                    }
                }
            }
            for (SaboteurMove move : chosenMoves) {
                String tileNum = move.getCardPlayed().getName().split(":")[1];
                int y = move.getPosPlayed()[1];
                if ((nugPosY+1) == y) {
                    if (tileNum.equals("6") || tileNum.equals("5_flip")) {
                        return move;
                    }
                }
            }
            for (SaboteurMove move : chosenMoves) {
                String tileNum = move.getCardPlayed().getName().split(":")[1];
                int y = move.getPosPlayed()[1];
                if ((nugPosY-1) == y) {
                    if (tileNum.equals("7") || tileNum.equals("6_flip")) {
                        return move;
                    }
                }
            }
        }

        // the nugget card hasn't been revealed
        // iterate thorugh the chosenMoves again to find desired card
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            int y = move.getPosPlayed()[1];
            if (y == 4 || y == 6) {
                if (tileNum.equals("8")) {
                    return move;
                }
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            int y = move.getPosPlayed()[1];
            if (y == 4 || y == 6) {
                if (tileNum.equals("9") || tileNum.equals("9_flip")) {
                    return move;
                }
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            int y = move.getPosPlayed()[1];
            if (y == 4 || y == 6) {
                if (tileNum.equals("10")) {
                    return move;
                }
            }
        }

        // iterate one more time to see if there is a optimal move can be made
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            if (tileNum.equals("8")) {
                return move;
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            if (tileNum.equals("9") || tileNum.equals("9_flip")) {
                return move;
            }
        }
        for (SaboteurMove move : chosenMoves) {
            String tileNum = move.getCardPlayed().getName().split(":")[1];
            if (tileNum.equals("10")) {
                return move;
            }
        }

        return null;
    }

    public static int[][] boardCopy(int[][] board) {
        if (board == null) {
            return board;
        }
        int[][] copy = new int[board.length][board[0].length];
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }

    public static boolean containsSaboteurTileWithDeadEnd(String saboteurTileIdx) {
        boolean contains = withDeadEndSaboteurTiles.contains(saboteurTileIdx);
    	return contains;
    }

}