package de.tuberlin.sese.swtpp.gameserver.model;

import java.io.Serializable;

public class King extends Chesspiece implements Serializable{
	
	private static final long serialVersionUID = 73233873088043830L;

	public King(String pos,char fenChar) {
		super(pos,fenChar);
	}

	@Override
	public boolean tryMove(Move move) {
		
		boolean isValidPattern = Math.abs(move.yOffset)<=1 &&
				Math.abs(move.xOffset)<=1 &&
				Math.abs(move.yOffset)+Math.abs(move.xOffset)!=0;

		if(isValidPattern) {
			Chesspiece cp = Gamestate.getGamestate().getPieceFromPos(move.getTarget());
			boolean movePossible = cp==null;
			if(!movePossible) {
				boolean capturePossible = cp.isWhite()!=this.isWhite();
				return capturePossible;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}
