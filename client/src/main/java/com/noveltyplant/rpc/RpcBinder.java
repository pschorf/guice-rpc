package com.noveltyplant.rpc;

import java.net.URI;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;

public class RpcBinder {
  private final Binder binder;
  private final MapBinder<String, InjectedService> mapBinder;

  public RpcBinder(Binder binder) {
    this.binder = binder;
    mapBinder = MapBinder.newMapBinder(binder, String.class, InjectedService.class);
  }

  public <T> void bindService(Class<T> iface, Class<? extends T> impl, String name) {
    mapBinder.addBinding(name).toInstance(InjectedService.create(iface, impl, binder));
    binder.bind(iface).toProvider(buildProvider(iface, name));
  }

  private <T> Provider<T> buildProvider(final Class<T> iface, final String name) {
    final Provider<URI> baseUri = binder.getProvider(Key.get(URI.class, BaseUri.class));
    final Provider<ClientProxyFactory> factory = binder.getProvider(ClientProxyFactory.class);
    return new Provider<T>() {
      @Override
      public T get() {
        URI targetUri = baseUri.get().resolve(name + "/");
        return factory.get().createProxy(iface, targetUri);
      }
    };
  }
}
