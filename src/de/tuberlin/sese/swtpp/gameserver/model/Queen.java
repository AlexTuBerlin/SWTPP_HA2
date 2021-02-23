package de.tuberlin.sese.swtpp.gameserver.model;

import java.io.Serializable;

public class Queen extends Chesspiece implements Serializable {

	private static final long serialVersionUID = 8107729630606560030L;

	public Queen(String pos,char fenChar) {
		super(pos,fenChar);
	}

	@Override
	public boolean tryMove(Move move) {
		
		if(!this.getPos().equals(move.getPosition()) || move.getPosition()==move.getTarget()) {return false;}
		else {
			boolean likeRook=move.xOffset==0&&move.yOffset!=0 || move.yOffset==0&&move.xOffset!=0;
			boolean likeBishop= move.xOffset!=0 && move.yOffset!=0 && Math.abs(move.xOffset)==Math.abs(move.yOffset);
			boolean isValidDirection = likeRook || likeBishop ;
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
}
