package de.tuberlin.sese.swtpp.gameserver.test.crazyhouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Chesspiece;
import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Gamestate;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.Rook;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.Move;

public class TryMoveIntegrationTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player whitePlayer = null;
	Player blackPlayer = null;
	Game game = null;
	GameController controller;
	List<Move> history = new LinkedList<Move>();
	Gamestate gamestate = Gamestate.getGamestate();
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "", "crazyhouse");
		
		game =  controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);
	}
	
	public void startGame() {
		controller.joinGame(user2, "crazyhouse");		
		blackPlayer = game.getPlayer(user2);
	}
	
	public void startGame(String initialBoard, boolean whiteNext) {
		startGame();
		
		game.setBoard(initialBoard);
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(expectedResult, game.tryMove(move, whitePlayer));
		else 
			assertEquals(expectedResult, game.tryMove(move, blackPlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		String board = game.getBoard().replaceAll("e", "");
		
		assertEquals(expectedBoard,board);
		assertEquals(finished, game.isFinished());

		if (!game.isFinished()) {
			assertEquals(whiteNext, game.getNextPlayer() == whitePlayer);
		} else {
			assertEquals(whiteWon, whitePlayer.isWinner());
			assertEquals(!whiteWon, blackPlayer.isWinner());
		}
	}
	
	
	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/

	@Test
	public void testBoardAsArray() {
		String boardFen = "rnbqkbnr/pppp1ppp/1p6/8/8/8/PPPPPPPP/RNBQKBNR";
		startGame(boardFen,true);
		game.setBoard("rnbqkbnr/ppppp1pp/8/5p2/8/8/PPPPPPPP/RNBQKBNR");
		Chesspiece[][] matrix = gamestate.getBoardAsArray();
		Chesspiece ch5 = matrix[4][5];
		assertEquals(ch5.getFenChar(),'p');
		
	}
	
	@Test
	public void testCreateChesspiece() {
		String boardFen = "rnbqkbnr/pppp1ppp/1p6/8/8/8/PPPPPPPP/RNBQKBNR";
		startGame(boardFen,true);
		String pos = "d6";
		char fe ='r';
		Chesspiece cp = Gamestate.createChesspiece(pos,fe);
		
		assertTrue(cp instanceof Rook);
		assertTrue(cp.getFenChar() == fe);
		assertTrue(cp.getPos() == pos);
	}
	
	@Test
	public void testGetChesspieceFromPos() {
		String boardFen = "rnbqkbnr/pppp1ppp/1p6/8/8/8/PPPPPPPP/RNBQKBNR";
		startGame(boardFen,true);
		String pos = "a7";
		Chesspiece cp = gamestate.getPieceFromPos(pos);
		assertEquals(cp.getPos() ,pos);
		assertTrue(cp.getFenChar() == 'p');
	}
	
	@Test
	public void testGetAsFen() {
		String fen = "rnbqkbnr/pppp1ppp/1p6/8/8/8/PPPPPPPP/RNBQKBNR";
		String fen2 ="rnbqkbnr/pppppppp/1P6/8/8/8/PPPPP1PP/RNBQKBNR";
		startGame(fen,true);
		Chesspiece[][] matrix = gamestate.getBoardAsArray();
		String transFen = Gamestate.getFenfromBoard(matrix);
		assertTrue(fen.equals(transFen));
		game.setBoard(fen2);
		matrix = gamestate.getBoardAsArray();
		transFen = Gamestate.getFenfromBoard(matrix);
		assertTrue(fen2.equals(transFen));
		assertFalse(fen.equals(transFen));
	}
	
	@Test
	public void testGetCoordinatesOfPos() {
		String fen = "rnbqkbnr/pppp1ppp/1p6/8/8/8/PPPPPPPP/RNBQKBNR/";
		startGame(fen,true);
		Integer x1 = Move.getCoordFromPos("b4", 0);
		Integer y1 = Move.getCoordFromPos("b4", 1);
		Integer x2 = Move.getCoordFromPos("c7", 0);
		Integer y2 = Move.getCoordFromPos("c7", 1);
	 	assertEquals(x1,Integer.valueOf(1));
	 	assertEquals(y1,Integer.valueOf(3));
	 	assertEquals(x2,Integer.valueOf(2));
	 	assertEquals(y2,Integer.valueOf(6));
	}
	
	@Test
	public void testGetPosFromCoord() {
		String pos = "d6";
		assertTrue(pos.compareTo(Move.getPosfromCoord(3, 5))==0);
	}

	@Test
	public void testTryMove() {
		String fen = "rnbqkbnr/pppp1ppp/1p6/8/8/8/PPPPPPPP/RNBQKBNR";
		String move;
		startGame(fen,false);
		
	//PAWN
		move ="c7-c6";
		assertTrue(game.tryMove(move, blackPlayer));
		
		startGame(fen,false);
		move ="c7-c6";
		assertFalse(game.tryMove(move, whitePlayer));
		
		startGame(fen,false);
		gamestate.setTurn(false);
		move ="c7-c5";
		assertTrue(game.tryMove(move, blackPlayer));
		
		startGame(fen,false);
		gamestate.setTurn(false);
		game.setBoard(fen);
		move ="c7-b6";
		assertFalse(game.tryMove(move, blackPlayer));
		
	//ROOK
		fen = "rnbqkbnr/pppp1ppp/8/8/8/1P6/1PPPPPPP/RNBQKBNR";
		startGame(fen,true);
		
		move ="a1-a4";
		assertTrue(game.tryMove(move, whitePlayer));
		
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="a1-a8";
		assertFalse(game.tryMove(move, whitePlayer));
		
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="a1-a7";
		assertTrue(game.tryMove(move, whitePlayer));
		
		fen = "r2q1bnr/1ppp1ppp/1p6/8/1P6/1NB5/1PPPPPPP/R2QKBNR";
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="a1-c1";
		assertTrue(game.tryMove(move, whitePlayer));
		
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="a1-e1";
		assertFalse(game.tryMove(move, whitePlayer));
		
	//BISHOP
		fen = "rnbqkbnr/1ppp1ppp/1p6/8/8/PP6/1PPP1PPP/RNBQKBNR";
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="f1-e2";
		assertTrue(game.tryMove(move, whitePlayer));
		
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="f1-a6";
		assertTrue(game.tryMove(move, whitePlayer));
		
		fen = "rnbqkbnr/2pp1ppp/p7/1p6/1P6/P3P3/2PP1PPP/RNBQKBNR";
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		assertFalse(game.tryMove(move, whitePlayer));
		
	//QUEEN
		fen = "rnb1kbnr/1ppqpppp/3p4/p7/P7/3P4/1PPQPPPP/RNB1KBNR";
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="d2-a5";
		assertTrue(game.tryMove(move, whitePlayer));
		
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="d2-f4";
		assertTrue(game.tryMove(move, whitePlayer));
		
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="d2-d4";
		assertFalse(game.tryMove(move, whitePlayer));
		
//Knight
		fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
		startGame(fen,true);
		game.setBoard(fen);
		gamestate.setTurn(true);
		move ="g1-h3";
		assertTrue(game.tryMove(move, whitePlayer));
	}
	
	@Test
	public void exampleTest() {
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",true);
		assertMove("b2-b7",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",true,false,false);
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",true);
		gamestate.setTurn(true);
		assertMove("b2-b4",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/1P6/8/P1PPPPPP/RNBQKBNR",false,false,false);
	}
	
	@Test
	public void testCantPutKingInDanger() {
		String fen = "rnbqkbnr/p1pppppp/8/1p5Q/8/4P3/PPPP1PPP/RNB1KBNR";
		startGame(fen,false);
		game.setBoard(fen);
		gamestate.setTurn(false);
		assertMove("f7-f6",false,false);
		assertGameState(fen,false,false,false);
		
		startGame(fen,false);
		game.setBoard(fen);
		gamestate.setTurn(false);
		assertMove("e7-e6",false,true);
		fen = gamestate.getBoardState();
		assertGameState(fen,true,false,false);
		
		fen = "rnb1k2r/pppqpppp/3p4/7b/8/P3Pn1B/RP1PKPNP/QNBP1P1R";
		startGame(fen,false);
		game.setBoard(fen);
		gamestate.setTurn(false);
		assertMove("d7-b5",false,true);
	}
	
	@Test
	public void testGetAllMovesPossible() {
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
		startGame(fen,false);
		game.setBoard(fen);
		gamestate.setTurn(false);
		List<String> moves = gamestate.getAllMovesPossible();
		assertTrue(moves.size()==20);
	}
	
	@Test
	public void testGameFinish() {
		String fen = "rnb1k2r/pppqpppp/3p4/7b/8/P3Pn1B/RP1PKPNP/QNBP1P1R";
		startGame(fen,false);
		game.setBoard(fen);
		gamestate.setTurn(false);
		assertMove("d7-b5",false,true);
		assertMove("b2-b3",true,false);
		assertGameState("rnb1k2r/ppp1pppp/3p4/1q5b/8/P3Pn1B/RP1PKPNP/QNBP1P1R",true,true,false);
		
	}
	@Test
	public void testGameFinish2() {
		String fen = "8/8/8/8/8/5q2/8/7K";
		startGame(fen,false);
		game.setBoard(fen);
		gamestate.setTurn(false);
		assertMove("f3-f2",false,true);
		assertGameState("8/8/8/8/8/8/5q2/7K",true,true,false);
	}

	//TODO: implement test cases of same kind as example here
}
