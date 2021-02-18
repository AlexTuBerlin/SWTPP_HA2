package de.tuberlin.sese.swtpp.gameserver.model;

public class King extends Chesspiece{
	
	public King(String pos,char fenChar) {
		super(pos,fenChar);
	}

	@Override
	public boolean tryMove(Move move) {
		
		if(!this.getPos().equals(move.getPosition()) || move.getPosition()==move.getTarget()){
			return false;
			}
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
				return false;
			}
			
		} else {
			return false;
		}
	}
}
