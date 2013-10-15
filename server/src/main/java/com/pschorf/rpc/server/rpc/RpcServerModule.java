package com.pschorf.rpc.server.rpc;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class RpcServerModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder()
        .implement(ArgumentDeserializer.class, ArgumentDeserializerImpl.class)
        .build(ArgumentDeserializerFactory.class));
  }
}
