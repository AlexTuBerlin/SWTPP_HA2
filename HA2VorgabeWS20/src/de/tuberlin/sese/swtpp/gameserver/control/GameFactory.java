package de.tuberlin.sese.swtpp.gameserver.control;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.HaskellBot;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.crazyhouse.CrazyhouseGame;

public class GameFactory {

    //TODO: change path to bot executable if desired   
    public static final String CRAZYHOUSE_BOT_PATH = "D:\\tmp\\crazyhouse\\";
    public static final String CRAZYHOUSE_BOT_COMMAND = "Bot";

    private GameFactory() {
    }

    public static Game createGame(String gameType) throws Exception {
        try {
            switch (gameType) {
            	// reduced to current game here
                case "crazyhouse":
                    return new CrazyhouseGame();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new Exception("Illegal game type encountered");
    }

    public static User createBot(String type, Game game) {
        if (type.equals("haskell")) {
            switch (game.getClass().getName().substring(game.getClass().getName().lastIndexOf(".") + 1)) {
                case "CrazyhouseGame":
                    return new HaskellBot(game, CRAZYHOUSE_BOT_PATH, CRAZYHOUSE_BOT_COMMAND);
                default:
                    return null;
            }
        }
        return null;
    }

}
