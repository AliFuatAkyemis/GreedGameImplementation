package players;

import game.*;
import java.util.List;

public class PlayerNew extends Player {
	public PlayerNew(Board board) {
		super(board);
	}

	@Override
	public Move nextMove() {
		List<Move> possibleMoves = board.getPossibleMoves();
		if (possibleMoves.isEmpty()) return null;

		int maxScore = Integer.MIN_VALUE;
		Move bestMove = possibleMoves.get(0);

		for (Move move : possibleMoves) {
			Board temp = new Board(super.board);
			temp.applyMove(move);
			for (int i = 0; i < 300; i++) {
				if (temp.isGameOver()) break;
				Move tempMove = choose(temp);
				temp.applyMove(tempMove);
			}
			if (temp.getScore() > maxScore) {
				maxScore = temp.getScore();
				bestMove = move;
			}
		}

		return bestMove;
	}

	private Move choose(Board gameBoard) {
		Move[] directions = {
			new Move(0, -1), new Move(1, -1), new Move(1, 0), new Move(1, 1), new Move(0, 1), new Move(-1, 1), new Move(-1, 0), new Move(-1, -1)
		};

		int segment = segment(gameBoard.getPlayerRow(), gameBoard.getPlayerCol());
		List<Move> moves = gameBoard.getPossibleMoves();

		for (int i = 0; i < directions.length; i++) {
			Move dir = directions[(i+segment)%directions.length];
			for (Move move : moves) {
				if (move.getDRow() == dir.getDRow() && move.getDCol() == dir.getDCol()) return move;
			}
		}

		return moves.get(0);
	}

	private int segment(int row, int col) {
		int size = super.board.getSize()/2;
		if (row < size && col <= size) return 0;
		else if (row >= size && col <= size) return 1;
		else if (row >= size && col > size) return 2;
		else return 3;
	}
}
