package com.noveltyplant.rest.server.util;

import java.util.Collections;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;
import com.noveltyplant.rest.server.RestServerModule;
import com.noveltyplant.rest.server.RestServletConfig;

public class RestServerRunner {
  public static void main(String[] args) throws Exception {
    Server server = new Server(10000);
    ServletContextHandler sch = new ServletContextHandler(server, "/");
    sch.setSessionHandler(new SessionHandler(new HashSessionManager()));
    sch.addEventListener(new RestServletConfig(
      Collections.singleton(new RestServerModule())));
    sch.addFilter(GuiceFilter.class,  "/*", null);
    sch.addServlet(DefaultServlet.class, "/");
    server.start();
    server.join();
  }
}
