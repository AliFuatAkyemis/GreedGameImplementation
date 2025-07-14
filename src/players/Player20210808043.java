package players;

import game.*;
import java.util.List;

public class Player20210808043 extends Player {
	public Player20210808043(Board board) {
		super(board);
	}

	@Override
	public Move nextMove() {
		List<Move> possibleMoves = board.getPossibleMoves();
		if (possibleMoves.isEmpty()) return null;

		//All possible directions
		Move[] directions = {
			new Move(0, -1), new Move(1, -1), new Move(1, 0), new Move(1, 1), new Move(0, 1), new Move(-1, 1), new Move(-1, 0), new Move(-1, -1)
		};

		Move bestMove = choose(directions, possibleMoves, super.board); //Choosing the move according to the segment priorities

		if (!simulator(directions, bestMove, super.board)) { //Simulating the chosen move
			Move temp = bestMove;
			bestMove = exChoose(directions, possibleMoves, super.board, new Move[] {bestMove}); //Choosing another move to compare
			if (!simulator(directions, bestMove, super.board)) bestMove = temp; //Updating the best move
		}

		return bestMove;
	}

	//Simulator method for several moves
	private boolean simulator(Move[] directions, Move currMove, Board currBoard) {
		Board simulatorBoard = new Board(currBoard); //Creating a new game board
		simulatorBoard.applyMove(currMove); //Applying current move
		int depth = 0;

		if (simulatorBoard.getSize() < 25) depth = 2; //Depth for 10x10 boards
		else if (simulatorBoard.getSize() < 50) depth = 4; //Depth for 25x25 boards
		else depth = 5; //Depth for 50x50 boards

		//Simulating phase
		for (int i = 0; i < depth; i++) { 
			if (simulatorBoard.isGameOver()) return false;
			Move move = choose(directions, simulatorBoard.getPossibleMoves(), simulatorBoard);
			simulatorBoard.applyMove(move);
		}

		return true;
	}

	//Checks for future score of board
	private int getFutureScore(Move[] directions, Move currMove, Board currBoard) {
		Board simulatorBoard = new Board(currBoard); //Creating a new game board
		simulatorBoard.applyMove(currMove); //Applying current move
		int depth = 0;

		if (simulatorBoard.getSize() < 25) depth = 2; //Depth for 10x10 boards
		else if (simulatorBoard.getSize() < 50) depth = 4; //Depth for 25x25 boards
		else depth = 4; //Depth for 50x50 boards

		//Simulating phase
		for (int i = 0; i < depth; i++) { 
			if (simulatorBoard.isGameOver()) return simulatorBoard.getScore();
			Move move = choose(directions, simulatorBoard.getPossibleMoves(), simulatorBoard);
			simulatorBoard.applyMove(move);
		}

		return simulatorBoard.getScore();
	}
	
	//Checks for futurePotential of next move
	private boolean futurePotential(Move[] directions, Move firstMove, Board currBoard) {
		Board simulation = new Board(currBoard);
		simulation.applyMove(firstMove);
		List<Move> moves = simulation.getPossibleMoves();

		int length = directions.length; //Directions array length
		int segment = determine(simulation.getPlayerRow(), simulation.getPlayerCol()); //Get segment of player
		
		//Check for getting stuck
		for (int i = 0; i < 3; i++) {
			Move direction = directions[(i+2*segment)%length];
			for (Move move : moves) {
				if (compare(direction, move)) return true;
			}
		}

		return false;
	}

	//Choose method that chooses move according to its restrictions
	private Move choose(Move[] directions, List<Move> possibleMoves, Board sampleBoard) {
		int length = directions.length; //Directions array length
		int segment = determine(sampleBoard.getPlayerRow(), sampleBoard.getPlayerCol()); //Get segment of player

		Move bestMove = possibleMoves.isEmpty() ? null : possibleMoves.get(0); //Initialize return value

		//Choosing phase
		for (int i = 0; i < length; i++) {
			Move direction = directions[(i+2*segment)%length]; //Getting the correct position int the list
			for (Move move : possibleMoves) {
				if (compare(direction, move)) bestMove = move;
			}
		}

		return bestMove;
	}

	//Choose method that excludes a specific move
	private Move exChoose(Move[] directions, List<Move> possibleMoves, Board sampleBoard, Move[] forbidden) {
		int length = directions.length; //Directions array length
		int segment = determine(sampleBoard.getPlayerRow(), sampleBoard.getPlayerCol()); //Get segment of player

		Move bestMove = possibleMoves.isEmpty() ? null : possibleMoves.get(0); //Initialize return value

		//Choosing phase
		for (int i = 0; i < length; i++) {
			Move direction = directions[(i+2*segment)%length];
			for (Move move : possibleMoves) {
				if (!isInclude(forbidden, move) && compare(direction, move)) bestMove = move; //Choosing by excluding the forbidden moves
			}
		}

		return bestMove;
	}

	//A method that checks including
	private boolean isInclude(Move[] moves, Move move) {
		for (Move temp : moves) if (compare(move, temp)) return true;
		return false;
	}

	//Comparator method to compare move objects
	private boolean compare(Move m1, Move m2) {
		if (m1 == null || m2 == null) return false; 
		return m1.getDCol() == m2.getDCol() && m1.getDRow() == m2.getDRow();
	}

	//Segment determination method(The region divided into 4 segment by a cross)
	private int determine(int row, int col) {
		int size = board.getSize()/2; //Semisize of an edge
		if (row < size && col <= size) return 0; //Segment 0
		else if (row >= size && col <= size) return 1; //Segment 1
		else if (row >= size && col > size) return 2; //Segment 2
		else return 3; //Segment 3
	}
}
