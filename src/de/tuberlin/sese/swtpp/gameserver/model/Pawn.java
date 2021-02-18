package de.tuberlin.sese.swtpp.gameserver.model;

public class Pawn extends Chesspiece {
	
	public Pawn(String pos,char fenChar) {
		super(pos,fenChar);
	}

	@Override
	public boolean tryMove(Move move) {
		String a = this.getPos();
		String b = move.getPosition();
		
		if(!a.equals(b) || move.getPosition()==move.getTarget()) {return false;}
		if(this.isWhite()) {return tryMoveWhite(move);}
		if(!this.isWhite()) {return tryMoveBlack(move);}
		
		return false;
	}
	
	private boolean tryMoveBlack(Move move) {	
		boolean isValidAttack = (move.xOffset==1||move.xOffset==-1) && move.yOffset==1 && capturePossibleForBlack(move.getTarget()) ;
		boolean isValidMove = (move.xOffset==0 && move.yOffset ==-1) && moveSpotFree(move.getTarget());
		boolean isValidStart = this.isOnStartPosition()&&(move.xOffset==0 && move.yOffset ==-2) && moveSpotFree(move.getTarget());
		return isValidAttack || isValidMove || isValidStart;
	}
	
	private boolean tryMoveWhite(Move move) {	
		boolean isValidAttack = (move.xOffset==1||move.xOffset==-1) && move.yOffset==-1&& capturePossibleForWhite(move.getTarget());
		boolean isValidMove = (move.xOffset==0 && move.yOffset ==1) && moveSpotFree(move.getTarget());
		boolean isValidStart = this.isOnStartPosition()&&(move.xOffset==0 && move.yOffset ==2) && moveSpotFree(move.getTarget());
		return isValidAttack || isValidMove || isValidStart;
	}
	
	private boolean moveSpotFree(String pos) {
		return Gamestate.getGamestate().getPieceFromPos(pos)==null;
	}
	
	private boolean capturePossibleForWhite(String pos) {
		Chesspiece cp = Gamestate.getGamestate().getPieceFromPos(pos);
		if (cp==null) {
			return false;
		}
		return !cp.isWhite();
	}
	
	private boolean capturePossibleForBlack(String pos) {
		Chesspiece cp = Gamestate.getGamestate().getPieceFromPos(pos);
		if (cp==null) {
			return false;
		}
		return cp.isWhite();
	}
	
	private boolean isOnStartPosition() {
		if(this.isWhite()) {
			return Move.getCoordFromPos(this.getPos(), 1) == 1;
		}
		if(!this.isWhite()) {
			return Move.getCoordFromPos(this.getPos(), 1) == 6;
		}
		return false;
	}
}
