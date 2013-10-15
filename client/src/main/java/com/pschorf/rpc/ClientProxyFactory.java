package com.pschorf.rpc;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.protobuf.Message;
import com.sun.jersey.api.client.Client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;

public class ClientProxyFactory {

  private final Client client;

  @Inject
  ClientProxyFactory(Client client) {
    this.client = client;
  }

  public <T> T createProxy(Class<T> klass, URI uri) {
    InvocationHandler handler = new ClientInvocationHandler(uri);
    return (T) Proxy.newProxyInstance(klass.getClassLoader(), new Class[] { klass },
        handler);
  }

  private class ClientInvocationHandler implements InvocationHandler {
    private final URI uri;
    ClientInvocationHandler(URI uri) {
      this.uri = uri;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      URI toInvoke = uri.resolve(method.getName());
      return client.resource(toInvoke)
        .accept(MimeTypes.PROTOBUF)
        .header("Content-Type", getContentType(args[0].getClass()))
        .post(method.getReturnType(), args[0]);
    }

    private String getContentType(Class<?> argType) {
      if (Message.class.isAssignableFrom(argType)) {
        return MimeTypes.PROTOBUF;
      } else {
        return "text/plain";
      }
    }
  }
}
