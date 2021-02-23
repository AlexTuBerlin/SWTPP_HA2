package de.tuberlin.sese.swtpp.gameserver.model;

import java.io.Serializable;

public class Bishop extends Chesspiece implements Serializable	 {

	private static final long serialVersionUID = -4584146611030180756L;

	public Bishop(String pos,char fenChar) {
		super(pos,fenChar);
	}

	@Override
	public boolean tryMove(Move move) {
		boolean isValidDirection = move.xOffset!=0 && move.yOffset!=0 && Math.abs(move.xOffset)==Math.abs(move.yOffset);
		if (!isValidDirection) {return false;}
		Chesspiece targetPosContent = Gamestate.getGamestate().getPieceFromPos(move.getTarget());
		boolean wayFree = move.isWayFree();
		if(!wayFree) {return false;} else {
			boolean isValidMove = wayFree && targetPosContent == null;
			if(isValidMove) {return true;} else {
				return targetPosContent.isWhite()!=this.isWhite();
			}
		}
	}
}
