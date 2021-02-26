package de.tuberlin.sese.swtpp.gameserver.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Gamestate implements Serializable {

	private static final long serialVersionUID = 1007695680187123877L;
	private static Gamestate INSTANCE;
	  private String boardState;
	    
	    private Gamestate() {    
	    	boardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/"; //Start-Zustand Board
	    }
	    
		public static Gamestate getGamestate() {
	        if(INSTANCE == null) {
	            INSTANCE = new Gamestate();
	        }
	        return INSTANCE;
	    }
		
		public static Chesspiece createChesspiece(String pos, char fenChar) {
			switch(Character.toLowerCase(fenChar)) {
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
			default: 
				return new Rook(pos,fenChar);
			}
		}
		
		public static String getFenfromBoard(Chesspiece[][] board) {
			int x = 0;
			int y = 0;
			int acc = 0;
			String fen = "";
			boolean lastCol = false;
			while (y <8) {
				while (x < 8) {
					while (board[7-y][x]==null 
							&& (!lastCol)) {
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
				fen=fen+"/";
				x=0;
				lastCol = false;
				y++;
			}
			return fen;
		}
		
		public static char changeCase(char c) {
			if(Character.toUpperCase(c)==c) {
				return Character.toLowerCase(c);
			} else {
				return Character.toUpperCase(c);
			}
		}
		
		//Fen String -> 2d-Char-Array
		public Chesspiece[][] getBoardAsArray(){
			String[] rows = getBoardState().split("/");
			Chesspiece[][] boardA = new Chesspiece[8][8];
			int emptyCount = 0;
			int rowN = 7; //Reihe
			int i = 0; //Spalte
        	int j = 0; //Position inerhalb einer Fen-String Reihe
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
		
		public Chesspiece getPieceFromPos(String pos) {
			if(pos!=null&&pos.length()==2) {
				Chesspiece[][] boardA = getBoardAsArray();
				int y = Move.getCoordFromPos(pos,1);
				int x = Move.getCoordFromPos(pos,0);
				return boardA[y][x];
			}
			return null;
		}
		
		public boolean addPieceToBoard(Chesspiece cp,boolean isWhite,boolean commit) {
			String boardPre = getBoardState();
			Chesspiece[][] boardA = getBoardAsArray();
			boardA[Move.getCoordFromPos(cp.getPos(),1)]
					[Move.getCoordFromPos(cp.getPos(),0)]=cp;
			setBoardOnly(getFenfromBoard(boardA));
			if(!isKingInDanger(!isWhite)) {
				if(!commit) {
					setBoardState(boardPre);
				}
				return true;
			}
			setBoardState(boardPre);
			return false;
		}

		public boolean doMove(Move move,boolean isWhite) {
			String boardFenPre = getBoardState();

			Chesspiece targetCP = getPieceFromPos(move.getTarget());
			clearPosition(move.getTarget());

			Chesspiece currentCP = getPieceFromPos(move.getPosition());
			clearPosition(move.getPosition());

			currentCP.setPos(move.getTarget());
			moveCP(currentCP);
			
			if(targetCP!=null) {
				targetCP.setPos(null);
				addToReserve(targetCP);
			}
			
			//check if Pawn has to be transformed
			if(!isKingInDanger(isWhite)) {
				return true;
			} else {
				setBoardState(boardFenPre);
				return false;
			}
		}
		
		@SuppressWarnings("deprecation")
		public List<Character> getReserveAsList() {
			String[] rows = getBoardState().split("/");
			List<Character> reserveList =  new LinkedList<>();
			if(rows.length==9) {
				char[] charArr = rows[8].toCharArray();
				for(int i=0;i<charArr.length;i++) {
					reserveList.add(new Character(charArr[i]));
				}
			}
			return reserveList;
		}
		
		public boolean addToReserve(Chesspiece cp) {
			String reserveOld=getReserve();
			String reserveNew = reserveOld+changeCase(cp.getFenChar());
			cp.setPos("");
			setReserve(sortString(reserveNew));
			return true;
		}
		
		public boolean pullFromReserveToPos(Character fenChar,String pos,boolean isWhiteTurn,boolean commit) {	
			String boardPre= getBoardState();
			//check if position is Free and in case its a pawn also the position
			if(pullReservePossible(fenChar,pos,isWhiteTurn)) {
				List<Character> cpL =getReserveAsList();
				for(Character cp:cpL) {
					if(cp.equals(fenChar)
							&& Character.isUpperCase(cp)==isWhiteTurn) {
						Chesspiece c = createChesspiece(pos,cp);
						if(commit) {
							removePieceFromList(cpL,cp);
						}
						setReserve(sortString(getReserveListAsString(cpL)));
						if(addPieceToBoard(c,isWhiteTurn,commit)){
							return true;
						} 
					}
				}
			}
			setBoardState(boardPre);
			return false;
		}
		
		public void removePieceFromList(List<Character> res,Character c) {
			for (Iterator<Character> iterator = res.iterator(); iterator.hasNext();) {
				Character ch = iterator.next();
			    if(ch.equals(c)) {
			        iterator.remove();
			        break;
			    }
			}
		}
		
		public boolean pullReservePossible(Character fenChar,String pos,boolean isWhiteTurn) {
			if (getPieceFromPos(pos)!=null ||
					(Integer.parseInt(pos.substring(pos.length() - 1))==1||
					Integer.parseInt(pos.substring(pos.length() - 1))==8)&&
					'p'==Character.toLowerCase(fenChar)) {
				return false;
			}
			return true;
		}
		
		public static String getReserveListAsString(List<Character> reserve) {
			String resString="";
			for(Character c:reserve) {
				resString=resString+c;
			}
			return sortString(resString);
		}
		
		public String getReserve() {
			String[] rows = getBoardState().split("/");
			if(rows.length==9) {
				return rows[8];
			}
			return "";
		}

		private String getBoardOnly() {
			String boardOnly = "";
			String[] rows = getBoardState().split("/");
			int i=0;
			for(String s:rows) {
				boardOnly=boardOnly+s+"/";
				if(i++==7) {
					 break;
				}
			}
			return boardOnly;
		}
		
		public boolean isKingInDanger(boolean isWhiteTurn) {
			Chesspiece king;
			for(Chesspiece cpR[]:getBoardAsArray()) {
				for(Chesspiece cp:cpR) {
					if(cp instanceof King && cp.isWhite()==isWhiteTurn) {
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
		
		public List<String> getAllMovesPossible(boolean isWhite){
			List<String> al = new ArrayList<>();
			ArrayList<Move> alm = new ArrayList<>();
			for(Chesspiece cpR[]:getBoardAsArray()) {
				for(Chesspiece cp:cpR) {
					if(cp!=null&&cp.isWhite()==isWhite) {
						List<Move> lm=getPossibleMoves(cp);
						if(lm!=null) {
							alm.addAll(lm);
						}
					}
				}
			}
			al.addAll(getAllAddToBoardMoves(isWhite));
			al.addAll(alm.stream().map(n -> n.getMove())
					.distinct().sorted()
					.collect(Collectors.toList()));
			return al;
		}
		
		public LinkedList<String> getAllAddToBoardMoves(boolean isWhite){
			String boardPre = getBoardState();
			List<Character> rl = getReserveAsList();
			LinkedList<String> addToBoardMoves = new LinkedList<>();
			if (!rl.isEmpty()) {
				for(Character fenC:rl) {
					if(Character.isUpperCase(fenC)==isWhite) {
						for(String targetPos : allPosOnBoard()) {
							Move move = new Move(fenC+"-"+targetPos, getBoardState(), null);
							if(pullFromReserveToPos(fenC,targetPos,isWhite,false)) {
								addToBoardMoves.add(move.getMove());
								setBoardState(boardPre);
							}
						}
					}
				}
			}
			return addToBoardMoves;
		}
		
		public List<Move> getPossibleMoves(Chesspiece cp){
			String boardPre = getBoardState();
			ArrayList<Move> al = new ArrayList<>();
			for(String pos:allPosOnBoard()) {
				Move move = new Move(cp.getPos()+"-"+pos, getBoardState(), null);
				if(cp.tryMove(move)&&doMove(move,cp.isWhite())) {
					al.add(move);
					setBoardState(boardPre);
				}
			}
			return al;
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
		
	    public static String sortString(String inputString) { 
	        char tempArray[] = inputString.toCharArray(); 
	        Arrays.sort(tempArray); 
	        return new String(tempArray); 
	    }
	    
		public Boolean isPosWhite(String pos) { //null wenn Feld leer
			if (getPieceFromPos(pos) == null) {
				return null;
			}
			return getPieceFromPos(pos).isWhite();
		}
		
		public Chesspiece transformToQueen(Chesspiece cp) {
			Chesspiece queen;
			if(cp.isWhite()) {
				queen = createChesspiece(cp.getPos(),'Q');
			} else {
				queen = createChesspiece(cp.getPos(),'q');
			}
			cp.setPos(null);
			return queen;
		}
		
		public void moveCP(Chesspiece cp) {
			Chesspiece[][] boardA = getBoardAsArray();
			int y = Move.getCoordFromPos(cp.getPos(),1);
			int x = Move.getCoordFromPos(cp.getPos(),0);
			if((y==0||y==7)&&'p'==Character.toLowerCase(cp.getFenChar())) {
				cp=transformToQueen(cp);
			}
			boardA[y][x] = cp;
			setBoardOnly(getFenfromBoard(boardA));
		}
		
		public void clearPosition(String pos) {
			if(getPieceFromPos(pos)!=null) {
				Chesspiece boardA[][] = getBoardAsArray();
				int y = Move.getCoordFromPos(pos,1);
				int x = Move.getCoordFromPos(pos,0);
				boardA[y][x] = null;
				setBoardOnly(getFenfromBoard(boardA));
			}
		}
		
		public void setReserve(String reserve) {
			setBoardState(getBoardOnly()+reserve);
		}
		
		public void setBoardOnly(String boardState) {
			setBoardState(boardState+getReserve());
		}
	    
		public String getBoardState() {
			return boardState;
		}

		public void setBoardState(String boardState) {
			this.boardState = boardState;
		}
	
}

