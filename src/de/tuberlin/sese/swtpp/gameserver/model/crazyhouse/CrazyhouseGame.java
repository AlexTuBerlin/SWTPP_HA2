package de.tuberlin.sese.swtpp.gameserver.model.crazyhouse;

import java.io.Serializable;

import de.tuberlin.sese.swtpp.gameserver.model.Chesspiece;
import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Gamestate;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

public class CrazyhouseGame extends Game implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5424778147226994452L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign white and black player
	private Player blackPlayer;
	private Player whitePlayer;
	private Gamestate board;

	// internal representation of the game state
	// TODO: insert additional game data here

	/************************
	 * constructors
	 ***********************/

	public CrazyhouseGame() {
		super();
		board = Gamestate.getGamestate(); //new Board

		// TODO: initialize internal model if necessary 
	}

	public String getType() {
		return "crazyhouse";
	}

	/*******************************************
	 * Game class functions already implemented
	 ******************************************/

	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);

			// game starts with two players
			if (players.size() == 2) {
				started = true;
				this.whitePlayer = players.get(0);
				this.blackPlayer= players.get(1);
				nextPlayer = whitePlayer;
			}
			return true;
		}

		return false;
	}

	@Override
	public String getStatus() {
		if (error)
			return "Error";
		if (!started)
			return "Wait";
		if (!finished)
			return "Started";
		if (surrendered)
			return "Surrendered";
		if (draw)
			return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if (started) {
			if (blackGaveUp())
				gameInfo = "black gave up";
			else if (whiteGaveUp())
				gameInfo = "white gave up";
			else if (didWhiteDraw() && !didBlackDraw())
				gameInfo = "white called draw";
			else if (!didWhiteDraw() && didBlackDraw())
				gameInfo = "black called draw";
			else if (draw)
				gameInfo = "draw game";
			else if (finished)
				gameInfo = blackPlayer.isWinner() ? "black won" : "white won";
		}

		return gameInfo;
	}

	@Override
	public String nextPlayerString() {
		return isWhiteNext() ? "w" : "b";
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}

	@Override
	public boolean callDraw(Player player) {

		// save to status: player wants to call draw
		if (this.started && !this.finished) {
			player.requestDraw();
		} else {
			return false;
		}

		// if both agreed on draw:
		// game is over
		if (players.stream().allMatch(Player::requestedDraw)) {
			this.draw = true;
			finish();
		}
		return true;
	}

	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.whitePlayer == player) {
				whitePlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				whitePlayer.setWinner();
			}
			surrendered = true;
			finish();

			return true;
		}

		return false;
	}

	/* ******************************************
	 * Helpful stuff
	 ***************************************** */

	/**
	 *
	 * @return True if it's white player's turn
	 */
	public boolean isWhiteNext() {
		return nextPlayer == whitePlayer;
	}

	/**
	 * Ends game after regular move (save winner, finish up game state,
	 * histories...)
	 *
	 * @param winner player who won the game
	 * @return true if game was indeed finished
	 */
	public boolean regularGameEnd(Player winner) {
		// public for tests
		if (finish()) {
			winner.setWinner();
			winner.getUser().updateStatistics();
			return true;
		}
		return false;
	}

	public boolean didWhiteDraw() {
		return whitePlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean whiteGaveUp() {
		return whitePlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/

	@Override
	public void setBoard(String state) {
		this.board.setBoardState(state);
	}
	
	@Override
	public String getBoard() {
		return this.board.getBoardState();
	}

	@Override
	public boolean tryMove(String moveString, Player player) {
		String boardPre = board.getBoardState();
		Move move = new Move(moveString,boardPre,player);
		
		Boolean isMovePossible = false;
		boolean moveSuccess = false;
		boolean isAddToBoard = move.isAddToBoardMove();
		Boolean isPositionWhite;
		if(!isAddToBoard) {
			isPositionWhite=board.isPosWhite(move.getPosition());
		} else {
			isPositionWhite=move.isAddToBoardWhite();
		}
		
		if(isPositionWhite!=null&&isWhiteNext()==isPositionWhite) {
			
			//ADD TO BOARD MOVE
			if(isAddToBoard) {
				isMovePossible = board.getPieceFromPos(move.getTarget())==null;
				if(isMovePossible) {
					moveSuccess = board.pullFromReserveToPos(move.getToBoardFenChar(), move.getTarget(),isWhiteNext(),true);
				}
			} 
			
			//ORDENARY MOVE
			if(move.isOrdenaryMove()) {
				Chesspiece cp = board.getPieceFromPos(move.getPosition());
				if(cp!=null) {
					isMovePossible = cp.tryMove(move);
					if(isMovePossible==true) {
						moveSuccess = board.doMove(move,isWhiteNext());
					}
				}
			}
			if(moveSuccess) {
				doPostMoveCheck(move);
				return true;
			}
		}
		return false;
	}
	
	public void switchTurn() {
		
		if(isWhiteNext()) {
			setNextPlayer(blackPlayer);
		} else {
			setNextPlayer(whitePlayer);
		}
	}
	
	public void doPostMoveCheck(Move move) {
		this.history.add(move);
		switchTurn();
		
		//check if moves are available for opponent
		if(board.getAllMovesPossible(isWhiteNext()).isEmpty()) {
			if(board.isKingInDanger(isWhiteNext())){
				giveUp(getNextPlayer());
			} else {
				callDraw(getNextPlayer());
			}
		}
	}
}
