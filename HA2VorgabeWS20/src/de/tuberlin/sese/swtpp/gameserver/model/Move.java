package de.tuberlin.sese.swtpp.gameserver.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Represents one move of a player in a certain stage of the game.
 * <p>
 * May be specialized further to represent game-specific move information.
 */
public class Move implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8030012939073138731L;

    // attributes
    protected String move;
    protected String board;
	protected int xOffset;
	protected int yOffset;
	protected boolean validStructure;
	protected boolean isAddToBoard;

    // associations
    protected Player player;

    /************************************
     * constructors
     ************************************/

    public Move(String move, String boardBefore, Player player) {
        this.move = move;
        this.board = boardBefore;
        this.player = player;
        this.xOffset = calcOffset(0,move);
        this.yOffset = calcOffset(1,move);
        this.validStructure = checkString();
        this.isAddToBoard = isAddToBoardMove();
    }

    public boolean isOrdenaryMove() {
    	String[] arr = this.move.split("-");
    	if(arr.length ==2) {
	    	int x1,x2,y1,y2;
	    	x1=getCoordFromPos(arr[0],0);
	    	y1=getCoordFromPos(arr[0],1);
	    	x2=getCoordFromPos(arr[1],0);
	    	y2=getCoordFromPos(arr[1],1);
			return coordOnBoard(x1,y1)&&coordOnBoard(x2,y2);
    	}
    	return false;
	}
    
    public boolean isAddToBoardMove() {
    	String[] arr = this.move.split("-");
    	if(arr.length ==2) {
	    	List<String> validChars = Arrays.asList(new String[]{"p","k","q","n","r","b"});
	    	int x,y;
	    	x=getCoordFromPos(arr[1],0);
	    	y=getCoordFromPos(arr[1],1);
			return coordOnBoard(x,y)&&validChars.contains(arr[0]);
    	}
    	return false;
    }
    
    private boolean checkString() {
    	return isOrdenaryMove() || isAddToBoardMove();
    }
    
    private boolean coordOnBoard(int x,int y) {
    	return x<=7&&x>=0&&y<=7&&y>=0;
    }

	private int calcOffset(int axis, String move) {
    	String[] arr = this.move.split("-");
    	return getCoordFromPos(arr[1], axis)-getCoordFromPos(arr[0], axis);
	}

	// returns either x or y Coordinates of Position
	// dim: 0 for X, 1 for Y 
	public static Integer getCoordFromPos(String pos,int dim) {
		if (dim < 2) {
	        char[] ch = new char[pos.length()]; 
	        for (int i = 0; i < pos.length(); i++) { 
	            ch[i] = pos.charAt(i); 
	        } 
	        if(Character.isDigit(ch[dim])) {
	        	return Integer.parseInt(String.valueOf(ch[dim])) - 1;
	        } else {
	        	return (int) ch[dim] -96 - 1;
	        }
		}
		return null;
	}

	public static String addOffsetToPostition(String pos,int xOff, int yOff) {
		int startX = getCoordFromPos(pos,0);
		int startY = getCoordFromPos(pos,1);
		
		if (startX+xOff<9 && startY+yOff<9 && startX+xOff>=0 && startY+yOff>=0) {
			return getPosfromCoord(startX+xOff,startY+yOff);
		}
		return null;
	}
	
	public static String getPosfromCoord(int x,int y) {
		return (char)(x+96+1) + String.valueOf(y+1);
	}
	
	public String getPosition() {
		String[] arr = this.move.split("-");
		if(arr.length ==2) {
			return arr[0];
		}
		return null;
	}
	
	public String getTarget() {
		String[] arr = this.move.split("-");
		if(arr.length ==2) {
			return arr[1];
		}
		return null;
	}
	
	public boolean isWayFree() {
		int cX=getDirection(0);
		int cY=getDirection(1);
		String nextPosition = this.getPosition();
		while (!nextPosition.equals(this.getTarget())) {
			nextPosition = Move.addOffsetToPostition(nextPosition, cX, cY);
			if(Gamestate.getGamestate().getPieceFromPos(nextPosition)!=null) {
				if (nextPosition.equals(this.getTarget())) {return true;} else {
				return false;
				}
			}
		}
		return true;
	}
	
	//axis: 0 for x, 1 for y
	private int getDirection(int axis) {
		if(axis==0) {
			if(this.xOffset>0) {return 1;} if(this.xOffset<0) {return -1;} else {return 0;}
		} else {
			if(this.yOffset>0) {return 1;} if(this.yOffset<0) {return -1;} else {return 0;}
		}
	}
	
	public Character getToBoardFenChar() {
		char[] charArray =this.getPosition().toCharArray();
		if(charArray.length==1) {
			return charArray[0];
		} else {
			return null;
		}
	}
	
	/************************************
     * getters/setters
     ************************************/

	
    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getState() {
        return board;
    }

    public void setBoard(String state) {
        this.board = state;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public boolean isValid() {
    	return this.validStructure;
    }
}
