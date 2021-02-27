package de.tuberlin.sese.swtpp.gameserver.model;

import java.io.Serializable;

public class Rook extends Chesspiece implements Serializable {
	
	private static final long serialVersionUID = -696923383557195905L;

	public Rook(String pos,char fenChar) {
		super(pos,fenChar);
	}

	@Override
	public boolean tryMove(Move move) {
		
		//if(!this.getPos().equals(move.getPosition()) ||
				//move.getPosition()==move.getTarget()) {return false;}
		//else {
			boolean isValidDirection = (move.xOffset==0&&move.yOffset!=0) || (move.yOffset==0&&move.xOffset!=0);
			if (!isValidDirection) {return false;}
			Chesspiece targetPosContent = Gamestate.getGamestate().getPieceFromPos(move.getTarget());
			boolean wayFree = move.isWayFree();
			if(!wayFree) {return false;} else {
				boolean isValidMove = targetPosContent == null;
				if(isValidMove) {return true;} else {
					return targetPosContent.isWhite()!=this.isWhite();
				}
			}
		}
	//}
	
}
