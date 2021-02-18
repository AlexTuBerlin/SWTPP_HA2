package de.tuberlin.sese.swtpp.gameserver.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Gamestate {
	  private static Gamestate INSTANCE;
	  private String boardState;
	  private List<Chesspiece> reserveW;
	  private List<Chesspiece> reserveB;
	  private boolean whiteTurn;
	    
	    private Gamestate() {    
	    	boardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"; //Start-Zustand Board
	    	reserveW = new LinkedList<Chesspiece>();
	    	reserveB = new LinkedList<Chesspiece>();
	    	whiteTurn=true;
	    }
	    
		public static Gamestate getGamestate() {
	        if(INSTANCE == null) {
	            INSTANCE = new Gamestate();
	        }
	        return INSTANCE;
	    }
		
		public static Chesspiece createChesspiece(String pos, char fenChar) {
			
			switch (Character.toLowerCase(fenChar)) {
			case 'p': 
				return new Pawn(pos,fenChar);
			case 'k': 
				return new King(pos,fenChar);
			case 'q': 
				return new Queen(pos,fenChar);
			case 'n': 
				return new Knight(pos,fenChar);
			case 'b': 
				return new Bishop(pos,fenChar);
			case 'r': 
				return new Rook(pos,fenChar);
			}
			return null;
		}
		
		public static String getFenfromBoard(Chesspiece[][] board) {
			int x = 0;
			int y = 0;
			int acc = 0;
			String fen = "";
			boolean lastCol = false;
			while (y <8) {
				while (x < 8) {
					while (board[7-y][x]==null && (x<8) && (!lastCol)) {
						acc++;
						if (x==7) {
							lastCol = true;
						} else {
							x++;
						}
					}
					if (acc>0) {
						fen=fen+acc;
						acc = 0;
					} else {
						if (board[7-y][x]!=null) {
							fen = fen + board[7-y][x].getFenChar();
						}
						x++;
					}
				}
				if(y!=7) {
					fen=fen+"/";
				}
				x=0;
				lastCol = false;
				y++;
			}
			return fen;
		}
		
		//Fen String -> 2d-Char-Array
		public Chesspiece[][] getBoardAsArray(){
			if(getBoardState()!=null) {
				String[] rows = getBoardState().split("/");
				Chesspiece[][] boardA = new Chesspiece[8][8];
				int emptyCount = 0;
				int rowN = 7; //Reihe
				int i = 0; //Spalte
	        	int j = 0; //Position in einer Fen-String Reihe
				String pos = "";
		        while (rowN >= 0) { 
		        	i=0;
		        	j=0;
		        	while(i<8) {
		        		pos=Move.getPosfromCoord(i, 7-rowN);
			        	if(Character.isDigit(rows[rowN].charAt(j))) {
			        		emptyCount = Character.getNumericValue(rows[rowN].charAt(j));
			        	} else {
			        		boardA[7-rowN][i] = createChesspiece(pos, rows[rowN].charAt(j));
			        		i++;
			        	}
			        	while (emptyCount != 0) { //null für leere Felder
				        	i++;
				        	emptyCount--;
				        	}
			        	j++;
				    }	        	
				    rowN--;
		        }
		        return boardA;
			}
			return null;
		}
		
		public Chesspiece getPieceFromPos(String pos) {
			Chesspiece[][] boardA = getBoardAsArray();
			int y = Move.getCoordFromPos(pos,1);
			int x = Move.getCoordFromPos(pos,0);
			return boardA[y][x];
		}

		public boolean doMove(Move move) {
			String boardFenPre = getBoardState();
			Chesspiece[][] boardA = getBoardAsArray();
			int yS = Move.getCoordFromPos(move.getPosition(),1);
			int xS = Move.getCoordFromPos(move.getPosition(),0);
			int yT = Move.getCoordFromPos(move.getTarget(),1);
			int xT = Move.getCoordFromPos(move.getTarget(),0);
			Chesspiece currentCp = getPieceFromPos(move.getPosition());
			Chesspiece targetCP = getPieceFromPos(move.getTarget());
			if(targetCP!=null) {
				addToReserve(targetCP);
			}
			boardA[yS][xS] = null;
			boardA[yT][xT] = currentCp;
			setBoardState(getFenfromBoard(boardA));
			
			if(boardFenPre.equals(getBoardState())) {
				return false;
			} else {
				if(!isKingInDanger()) {
					return true;
				} else {
					setBoardState(boardFenPre);
					return false;
				}
			}
		}
		
		private void addToReserve(Chesspiece targetCP) {
			if(whiteTurn) {
				reserveW.add(createChesspiece(null, Character.toUpperCase(targetCP.getFenChar())));
			} else {
				reserveB.add(createChesspiece(null, Character.toLowerCase(targetCP.getFenChar())));
			}
			targetCP = null;
		}
		
		public boolean pullFromReserveToPos(char fenChar,String pos) {	
			if (getPieceFromPos(pos)!=null) {
				return false;
			}
			List<Chesspiece> cpL;
			if(isWhiteTurn()) {
				cpL=reserveW;
			} else {
				cpL=reserveB;
			}
			for(Chesspiece cp:cpL) {
				if(Character.toLowerCase(cp.getFenChar())==fenChar) {
					createChesspiece(pos,cp.getFenChar());
					cpL.remove(cp);
					if(getPieceFromPos(pos).getFenChar()==fenChar) {
						return true;
					} else {
						return false;
					}
				}
			}
			return false;
		}
		
		public boolean isKingInDanger() {
			Chesspiece king;
			for(Chesspiece cpR[]:getBoardAsArray()) {
				for(Chesspiece cp:cpR) {
					if(cp instanceof King && cp.isWhite()==isWhiteTurn()) {
						king = cp;
						if(isPosInDanger(king.getPos())){
							return true;
						} else {
							return false;
						}
					}
				}
			}
			return false;
		}
		
		public boolean isPosInDanger(String pos) {
			for(Chesspiece cpR[]:getBoardAsArray()) {
				for(Chesspiece cp:cpR) {
					if(cp!=null) {
						if(cp.isWhite()!=getPieceFromPos(pos).isWhite()) {
							Move move = new Move(cp.getPos()+"-"+pos,getBoardState(),null);
							if(cp.tryMove(move)) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}
		
		public List<String> getAllMovesPossible(){
			List<String> al = new ArrayList<>();
			ArrayList<Move> alm = new ArrayList<>();
			for(Chesspiece cpR[]:getBoardAsArray()) {
				for(Chesspiece cp:cpR) {
					if(cp!=null) {
						List<Move> lm=getPossibleMoves(cp);
						if(lm!=null) {
							alm.addAll(lm);
						}
					}
				}
			}
			al = alm.stream().map(n -> n.getMove())
					.distinct().sorted()
					.collect(Collectors.toList());
			return al;
		}
		
		public List<Move> getPossibleMoves(Chesspiece cp){
			if(cp.isWhite()==isWhiteTurn()) {
				ArrayList<Move> al = new ArrayList<>();
				for(String pos:allPosOnBoard()) {
					Move move = new Move(cp.getPos()+"-"+pos, getBoardState(), null);
					if(cp.tryMove(move)) {
						al.add(move);
					}
				}
				return al;
			}
			return null;
		}
		
		private List<String> allPosOnBoard(){
			ArrayList<String> al = new ArrayList<>();
			for(int i=0;i<8;i++) {
				for(int j=0;j<8;j++) {
					al.add(Move.getPosfromCoord(i, j));
				}
			}
			return al;
		}

		public boolean isWhiteTurn() {
			return whiteTurn;
		}
		
		public void setTurn(boolean whiteTurn) {
			this.whiteTurn=whiteTurn;
		}

		public Boolean isWhite(String pos) { //null wenn Feld leer
			if (getPieceFromPos(pos) == null) {
				return null;
			}
			return getPieceFromPos(pos).isWhite();
		}
	    
		public String getBoardState() {
			return boardState;
		}

		public void setBoardState(String boardState) {
			this.boardState = boardState;
		}

		public List<Chesspiece> getReserveW() {
			return reserveW;
		}

		public void setReserveW(List<Chesspiece> reserveW) {
			this.reserveW = reserveW;
		}

		public List<Chesspiece> getReserveB() {
			return reserveB;
		}

		public void setReserveB(List<Chesspiece> reserveB) {
			this.reserveB = reserveB;
		}		
}

