package com.example.ui;

import static spark.Spark.halt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.appl.GameCenter;
import com.example.appl.PlayerServices;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;
import spark.TemplateEngine;

/**
 * The {@code GET /} route handler; aka the Home page.
 * This is the page where the user starts (no Game yet)
 * but is also the landing page after a game ends.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author <a href='mailto:jrv@se.rit.edu'>Jim Vallino</a>
 */
public class GetHomeRoute implements Route {

  // Values used in the view-model map for rendering the home view.
  static final String TITLE_ATTR = "title";
  static final String GAME_STATS_MSG_ATTR = "gameStatsMessage";
  static final String NEW_PLAYER_ATTR = "newPlayer";
  static final String TITLE = "Welcome to the Guessing Game";
  static final String VIEW_NAME = "home.ftl";
  static final String INDIVIDUAL_GAME_STATS_MSG_ATTR = "individualGameStatsMessage";
  final static String NO_GAMES_PLAYED_MESSAGE = "You have not played a game this session";

  // Key in the session attribute map for the player who started the session
  static final String PLAYERSERVICES_KEY = "playerServices";
  static final String TIMEOUT_SESSION_KEY = "timeoutWatchdog";

  // The length of the session timeout in seconds
  static final int SESSION_TIMEOUT_PERIOD = 10;

  //
  // Attributes
  //

  private final GameCenter gameCenter;
  private final TemplateEngine templateEngine;

  //
  // Constructor
  //

  /**
   * The constructor for the {@code POST /guess} route handler.
   *
   * @param gameCenter
   *    The {@link GameCenter} for the application.
   * @param templateEngine
   *    The {@link TemplateEngine} for the application to use when rendering HTML responses.
   *
   * @throws NullPointerException
   *    when the {@code gameCenter} or {@code templateEngine} parameter is null
   */
  GetHomeRoute(final GameCenter gameCenter, final TemplateEngine templateEngine) {
    // validation
    Objects.requireNonNull(gameCenter, "gameCenter must not be null");
    Objects.requireNonNull(templateEngine, "templateEngine must not be null");
    //
    this.gameCenter = gameCenter;
    this.templateEngine = templateEngine;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String handle(Request request, Response response) {
    // retrieve the HTTP session
    final Session httpSession = request.session();

    // start building the View-Model
    final Map<String, Object> vm = new HashMap<>();
    vm.put(TITLE_ATTR, TITLE);

    // report application-wide game statistics
    vm.put(GAME_STATS_MSG_ATTR, gameCenter.getGameStatsMessage());

    //add personal player stats
    if(gameCenter.getPlayer() != null) {
      vm.put(INDIVIDUAL_GAME_STATS_MSG_ATTR, gameCenter.getPlayer().getPlayerStatsMessage());
    }else{
      vm.put(INDIVIDUAL_GAME_STATS_MSG_ATTR, NO_GAMES_PLAYED_MESSAGE);
    }

    // if this is a brand new browser session or a session that timed out
    if(httpSession.attribute(PLAYERSERVICES_KEY) == null) {
      // get the object that will provide client-specific services for this player
      final PlayerServices playerService = gameCenter.newPlayerServices();
      httpSession.attribute(PLAYERSERVICES_KEY, playerService);

      // setup session timeout. The valueUnbound() method in the SessionTimeoutWatchdog will
      // be called when the session is invalidated. The next invocation of this route will
      // have a new Session object with no attributes.
      httpSession.attribute(TIMEOUT_SESSION_KEY, new SessionTimeoutWatchdog(playerService));
      httpSession.maxInactiveInterval(SESSION_TIMEOUT_PERIOD);

      // render the Game Form view
      vm.put(NEW_PLAYER_ATTR, true);
      return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
    }
    else {
      // there is a game already being played so redirect the user to the Game view
      response.redirect(WebServer.GAME_URL);
      halt();
      return null;
    }
  }
}
