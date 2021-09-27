package com.example.ui;

import java.util.Objects;
import java.util.logging.Logger;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.example.appl.PlayerServices;

/**
 * Whenever an instance of a class that implements {@linkplain HttpSessionBindingListener}
 * gets set as the value for a session attribute, the valueBound() method gets called.
 * Any time that the attribute is removed, set to another value, or the session is
 * invalidated, the valueUnbound() method gets called.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 */
public class SessionTimeoutWatchdog implements HttpSessionBindingListener {
  private static final Logger LOG = Logger.getLogger(SessionTimeoutWatchdog.class.getName());

  private final PlayerServices playerServices;

  public SessionTimeoutWatchdog(final PlayerServices playerServices) {
    LOG.fine("Watch dog created.");
    this.playerServices = Objects.requireNonNull(playerServices);
  }

  @Override
  public void valueBound(HttpSessionBindingEvent event) {
    // ignore this event
    LOG.fine("Player session started.");
  }

  @Override
  public void valueUnbound(HttpSessionBindingEvent event) {
    // the session is being terminated do some cleanup
    playerServices.endSession();
    //
    LOG.fine("Player session ended.");
  }
}
