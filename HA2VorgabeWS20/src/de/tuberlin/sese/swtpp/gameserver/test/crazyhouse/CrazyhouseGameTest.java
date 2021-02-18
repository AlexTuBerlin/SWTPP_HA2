package de.tuberlin.sese.swtpp.gameserver.test.crazyhouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.crazyhouse.CrazyhouseGame;

public final class CrazyhouseGameTest {
	
	final User user1 = new User("Alice", "alice");
	final User user2 = new User("Bob", "bob");
	final User user3 = new User("Eve", "eve");
	
	Player whitePlayer = null;
	Player blackPlayer = null;
	CrazyhouseGame game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "", "crazyhouse");
		
		game = (CrazyhouseGame) controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);
	}
	
	public void startGame() {
		controller.joinGame(user2, "crazyhouse");
		blackPlayer = game.getPlayer(user2);
	}

	
	@Test
	public void testWaitingGame() {
		assertEquals("Wait", game.getStatus());
		assertEquals("", game.gameInfo());
	}
	
	@Test
	public void testGameStarted() {
		assertEquals(game.getGameID(), controller.joinGame(user2, "crazyhouse"));
		assertFalse(game.addPlayer(new Player(user3, game))); // no third player
		assertEquals("Started", game.getStatus());
		assertEquals("", game.gameInfo());
		assertTrue(game.isWhiteNext());
		assertFalse(game.didWhiteDraw());
		assertFalse(game.didBlackDraw());
		assertFalse(game.whiteGaveUp());
		assertFalse(game.blackGaveUp());
	}

	@Test
	public void testSetNextPlayer() {
		startGame();
		
		game.setNextPlayer(blackPlayer);
		
		assertFalse(game.isWhiteNext());
	}

	
	@Test
	public void testCallDrawBoth() {	
		// call draw before start
		assertFalse(game.callDraw(whitePlayer));

		startGame();
		
		controller.callDraw(user1, game.getGameID());
		assertTrue(game.didWhiteDraw());
		assertFalse(game.didBlackDraw());
		assertEquals("white called draw", game.gameInfo());
		
		controller.callDraw(user2, game.getGameID());
		assertTrue(game.didBlackDraw());

		assertEquals("Draw", game.getStatus());
		assertEquals("draw game", game.gameInfo());
		
		// call draw after finish
		assertFalse(game.callDraw(whitePlayer));
	}
	
	@Test
	public void testCallDrawBlack() {
		startGame();
		
		controller.callDraw(user2, game.getGameID());
		assertFalse(game.didWhiteDraw());
		assertTrue(game.didBlackDraw());
		assertEquals("black called draw", game.gameInfo());
	}

	@Test
	public void testGiveUpWhite() {
		// try before start 
		assertFalse(game.giveUp(whitePlayer));
		assertFalse(game.giveUp(blackPlayer));
		
		startGame();
		
		controller.giveUp(user1, game.getGameID());
		
		assertEquals("Surrendered", game.getStatus());
		assertEquals("white gave up", game.gameInfo());
		
		// try after finish
		assertFalse(game.giveUp(whitePlayer));
		assertFalse(game.giveUp(blackPlayer));

	}
	
	@Test
	public void testGiveUpBlack() {
		startGame();
		
		controller.giveUp(user2, game.getGameID());
		
		assertEquals("Surrendered", game.getStatus());
		assertEquals("black gave up", game.gameInfo());
	}

	@Test
	public void testGetMinPlayers() {
		assertEquals(2, game.getMinPlayers());
	}
	
	@Test
	public void testGetMaxPlayers() {
		assertEquals(2, game.getMaxPlayers());
	}
	
	@Test
	public void testNextPlayerString() {
		startGame();
		
		assertEquals("w", game.nextPlayerString());
		
		game.setNextPlayer(blackPlayer);
		
		assertEquals("b", game.nextPlayerString());
	}

	@Test
	public void testFinish() {
		startGame();
		
		assertTrue(game.regularGameEnd(whitePlayer));
		assertEquals("Finished", game.getStatus());
		assertEquals("white won", game.gameInfo());
		
		// test after finish
		assertFalse(game.regularGameEnd(whitePlayer));
	}
	
	@Test
	public void testFinishBlack() {
		startGame();
		
		assertTrue(game.regularGameEnd(blackPlayer));
		assertEquals("Finished", game.getStatus());
		assertEquals("black won", game.gameInfo());
	}

	@Test
	public void testError() {
		assertFalse(game.isError());
		game.setError(true);
		assertTrue(game.isError());
		assertEquals("Error", game.getStatus());
		game.setError(false);
		assertFalse(game.isError());
	}
}
