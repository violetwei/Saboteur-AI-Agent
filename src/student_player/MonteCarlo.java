package student_player;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;

import boardgame.Board;
import boardgame.Move;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;

public class MonteCarlo {

	private static Node winningNode;

	// define Data
	static class Data {
		int win;
		int total;
	}

	// define Node
	static class Node {
		private SaboteurBoardState state; // state of the Saboteur board
		private SaboteurMove move; // the move to take parent to this state

		private Node parent;
		private List<Node> children; 

		int visited;
		int win;

		public Node(SaboteurBoardState state, SaboteurMove move, Node parent) {
			super();
			this.state = state;
			this.move = move;
			this.parent = parent;
			this.children = new ArrayList<Node>();
		}

		public SaboteurBoardState getState() {
			return state;
		}

		public void setSate(SaboteurBoardState state) {
			this.state = state;
		}

		public SaboteurMove getMove() {
			return move;
		}

		public void setMove(SaboteurMove move) {
			this.move = move;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public List<Node> getChildren() {
			return children;
		}

		public void setChildren(List<Node> children) {
			this.children = children;
		}

		public Node selectRandomChild() {
			return children.get((int) (Math.random() * children.size()));
		}

	}

	public static Move getNextMove(SaboteurBoardState boardState, int player_id) {
		winningNode = null;
		Node root = new Node(boardState, null, null);
		expandNode(root);
		
		long time = System.currentTimeMillis();
		take(root.getChildren(), player_id);
		if (winningNode != null) {
			return winningNode.move;
		}
		long timeTake = System.currentTimeMillis() - time;
		long cur = System.currentTimeMillis();
		long end = cur + 1000 - timeTake;
		
		while (System.currentTimeMillis() < end) {
			Node node = decentWithUCT(root);
			playout(node, player_id);
		}

		Node optimalNode = Collections.max(root.getChildren(), Comparator.comparing(n -> (double) n.win / n.visited));
	
		if (winningNode != null) {
			optimalNode = winningNode;
		}
		
		return optimalNode.getMove();
	}

	// expand the given Node
	private static void expandNode(Node node) {
		ArrayList<SaboteurMove> allLegalMoves = node.getState().getAllLegalMoves();
		allLegalMoves.forEach(m -> {
			SaboteurBoardState boardState = (SaboteurBoardState) node.getState().clone();
			boardState.processMove(m);
			Node child = new Node(boardState, m, node);
			node.getChildren().add(child);
		});
	}
	
	// UCT - Upper Confidence Trees
	private static double calculateUCT(Node node) {
		if (node.visited == 0) {
			return Integer.MAX_VALUE;
		}
		int num = node.win / node.visited;
		return num + Math.sqrt(2 * Math.log(node.getParent().visited) / node.visited);
	}

	private static Node decentWithUCT(Node node) {
		List<Node> children = node.getChildren();
		while (children.size() > 0) {
			node = Collections.max(children, Comparator.comparing(n -> calculateUCT(n)));
		}		
		return node;
	}

	private static void playout(Node node, int player_id) {
		SaboteurBoardState state = (SaboteurBoardState) node.getState().clone();

		int i = 0;
		while (state.getWinner() == Board.NOBODY) {
			Move move = state.getAllLegalMoves().get(0);
			state.processMove((SaboteurMove)move);
			i++;
		}

		int winner = state.getWinner();
		int reward = -1;

		if (winner == player_id) {
			reward = 56 - 2*node.state.getTurnNumber() - i;
		} else if (winner == Board.DRAW) {
			reward = 0;
		} else {
			reward = 0;
			if(i <= 1 && node.parent.children.size() >= 2) {
				node.parent.children.remove(node);
				node.parent = null;
			}
		}
		Node temp = node;
		while (temp != null) {
			temp.visited++;
			temp.win += reward;
			temp = temp.parent;
		}
	}

	private static void take(List<Node> nodes, int player_id) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).state.getWinner() == player_id /*|| nodes.get(i).state.getWinner() == Board.DRAW*/) {
				winningNode = nodes.get(i);
			};
			List<SaboteurMove> moves = nodes.get(i).state.getAllLegalMoves();
			for (SaboteurMove move : moves) {
                SaboteurBoardState state = (SaboteurBoardState)nodes.get(i).getState().clone();
				state.processMove(move);
				if (state.getWinner()!= Board.NOBODY && state.getWinner() != player_id && state.getWinner()!= Board.DRAW) {
					if(nodes.size() > 1) {
						nodes.remove(nodes.get(i));
					}else {
						return;
					}
					i--;
					break;
				}
			}
		}
	}
	
}