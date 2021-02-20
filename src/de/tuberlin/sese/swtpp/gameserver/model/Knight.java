package de.tuberlin.sese.swtpp.gameserver.model;

import java.io.Serializable;

public class Knight extends Chesspiece implements Serializable {
	
	private static final long serialVersionUID = -925421469433394680L;

	public Knight(String pos,char fenChar) {
		super(pos,fenChar);
	}

	@Override
	public boolean tryMove(Move move) {
		if(!this.getPos().equals(move.getPosition()) || move.getPosition()==move.getTarget()){
			return false;
			}
		boolean isValidPattern = (Math.abs(move.yOffset)==2&&Math.abs(move.xOffset)==1)||
				(Math.abs(move.yOffset)==1&&Math.abs(move.xOffset)==2);
		if(isValidPattern) {
			Chesspiece cp = Gamestate.getGamestate().getPieceFromPos(move.getTarget());
			boolean movePossible = cp==null;
			if(movePossible) {
				return true;
			} else {
				boolean capturePossible = cp.isWhite()!=this.isWhite();
				return capturePossible;
			}
		}
		return false;
	}
	
}
