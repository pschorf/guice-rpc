package com.noveltyplant.rest.server;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.MDC;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.noveltyplant.rest.server.rpc.RpcServerModule;
import com.sun.jersey.api.core.HttpContext;

public class RestServerModule extends AbstractModule {

	@Override
	protected void configure() {
                install(new RpcServerModule());
	}

        @Provides
        @RequestScoped
        MultivaluedMap<String, String> provideHeaders(HttpContext context) {
          return context.getRequest().getRequestHeaders();
        }

        @Provides
        @RequestScoped
        MediaType provideMediaType(HttpContext context) {
          String contentType = context.getRequest().getRequestHeader("content-type").get(0);
          com.google.common.net.MediaType type = com.google.common.net.MediaType.parse(contentType);
          return new MediaType(type.type(), type.subtype());
        }

}
