package com.noveltyplant.rpc;

import java.net.URI;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;

public class ClientModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ResourceConfig.class).to(DefaultResourceConfig.class);
    // TODO(paul): Make configurable
    bind(URI.class).annotatedWith(BaseUri.class)
      .toInstance(URI.create("http://rpc.noveltyplant.com/rpc/"));
  }

  @Provides
  GuiceComponentProviderFactory provideProviderFactory(Injector injector, ResourceConfig config) {
    return new GuiceComponentProviderFactory(config, injector);
  }

  @Provides
  Client provideClient(GuiceComponentProviderFactory factory) {
    ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(ProtobufSerialization.ProtobufMessageBodyReader.class);
    config.getClasses().add(ProtobufSerialization.ProtobufMessageBodyWriter.class);
    return Client.create(config, factory);
  }
}
