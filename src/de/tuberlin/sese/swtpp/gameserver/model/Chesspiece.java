package de.tuberlin.sese.swtpp.gameserver.model;

public abstract class Chesspiece {

    private boolean isWhite;
	private String pos;
	private char fenChar;

	public Chesspiece(String pos,char fenChar) {
		this.pos = pos;
		this.fenChar = fenChar;
		this.isWhite = Character.isUpperCase(fenChar);
	}

	public abstract boolean tryMove(Move move);
	
	public String addOffset(int xOff, int yOff) {
		return Move.addOffsetToPostition(pos, xOff, yOff);
	}

	public boolean isWhite() {
		return isWhite;
	}

	public String getPos() {
		return pos;
	}
	
	public void setPos(String pos) {
		this.pos = pos;
	}

	public char getFenChar() {
		return fenChar;
	}
	
	public boolean isInReserve() {
		return this.pos == null;
	}

}
