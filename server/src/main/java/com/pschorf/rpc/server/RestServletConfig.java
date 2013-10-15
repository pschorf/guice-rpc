package com.pschorf.rpc.server;

import java.util.Collections;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;
import com.pschorf.rpc.server.rpc.RpcServlet;
import com.pschorf.rpc.ProtobufSerialization;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class RestServletConfig extends GuiceServletContextListener {
	private final ImmutableList<Module> modules;

	public RestServletConfig() {
		this(Collections.singleton(new RestServerModule()));
	}

	public RestServletConfig(Iterable<? extends Module> modules) {
		this.modules = ImmutableList.<Module>builder().addAll(modules)
				.add(getServletModule())
				.build();
	}

	private Module getServletModule() {
		return new JerseyServletModule() {
			@Override
			protected void configureServlets() {
                                bind(RpcServlet.class);
				bind(ProtobufSerialization.ProtobufMessageBodyWriter.class).in(Singleton.class);
				bind(ProtobufSerialization.ProtobufMessageBodyReader.class).in(Singleton.class);
				bind(IterableJsonSerializer.class).in(Singleton.class);
				bind(ProtobufJsonSerializer.class).in(Singleton.class);
				serve("/*").with(GuiceContainer.class);
			}

		};
	}

	@Override
	protected Injector getInjector() {
	  return Guice.createInjector(Stage.PRODUCTION, modules);
	}
}
