package com.example.appl;

import java.util.logging.Logger;

import com.example.model.GuessGame;

/**
 * The object to coordinate the state of the Web Application and keep sitewide statistics.
 *
 * This class is an example of the Pure Fabrication principle.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author <a href='mailto:jrv@se.rit.edu'>Jim Vallino</a>
 */
public class GameCenter {
  private static final Logger LOG = Logger.getLogger(GameCenter.class.getName());

  //
  // Constants
  //

  // Output strings made public for unit test access
  public final static String PRINT_GAMES = "Players have won %.0f%% of the games played this session";
  public final static String NO_GAMES_MESSAGE = "No games have been played so far.";
  public final static String ONE_GAME_MESSAGE = "One game has been played so far.";
  public final static String GAMES_PLAYED_FORMAT = "There have been %d games played.";
  public final static String GAMES_WON_FORMAT = "You have won %d of the games played.";
  //
  // Attributes
  //

  private int totalGames = 0;
  private int gamesWon = 0;
  private PlayerServices player = null;
  //
  // Constructors
  //

  //
  // Public methods
  //

  /**
   *
   * @return return the current player
   */
  public PlayerServices getPlayer(){
    return player;
}

  /**
   * Get a new {@Linkplain PlayerServices} object to provide client-specific services to
   * the client who just connected to this application.
   *
   * @return
   *   A new {@Link PlayerServices}
   */
  public PlayerServices newPlayerServices() {
    LOG.fine("New player services instance created.");
    this.player = new PlayerServices(this);
    return player;
  }

  /**
   * Create a new {@Linkplain GuessGame} game.
   *
   * @return
   *   A new {@link GuessGame}
   */
  public GuessGame getGame() {return new GuessGame();}

  /**
   * Collect sitewide statistics when a game is finished.
   */
  public void gameFinished(boolean result) {
    // do some application-wide book-keeping
    synchronized (this) {  // protect the critical code
      if(result) {
        gamesWon++;
      }
      totalGames++;
      player.updateGames(result);
    }
  }

  /**
   * Get a user message about the sitewide statistics.
   *
   * @return
   *   The message to the user about global game statistics.
   */
  public synchronized String getGameStatsMessage() {
    if(totalGames == 0) {
      return NO_GAMES_MESSAGE;
    }
    float percent = ((float)gamesWon/(float)totalGames)*100;
    return String.format(PRINT_GAMES,  percent);
  }
}
