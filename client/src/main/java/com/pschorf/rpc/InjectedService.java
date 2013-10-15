package com.pschorf.rpc;

import com.google.inject.Binder;
import com.google.inject.Provider;

import java.lang.Class;

public class InjectedService<T, U extends T> {
  private final Class<T> iface;
  private final Provider<U> provider;

  static <T , U extends T> InjectedService<T, U> create(Class<T> iface, Class<U> klass, Binder binder) {
    return new InjectedService<T, U>(iface, binder.getProvider(klass));
  }

  public U getInstance() {
    return provider.get();
  }

  public Class<T> getServiceClass() {
    return iface;
  }

  private InjectedService(Class<T> iface, Provider<U> provider) {
    this.provider = provider;
    this.iface = iface;
  }
}
