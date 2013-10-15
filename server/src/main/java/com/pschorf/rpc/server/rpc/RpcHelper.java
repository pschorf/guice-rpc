package com.pschorf.rpc.server.rpc;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.protobuf.AbstractMessage;
import com.pschorf.messages.Rpc.RpcMethodDescriptor;
import com.pschorf.rpc.InjectedService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

public class RpcHelper {

  private final Map<String, InjectedService> services;
  private final Map<String, Map<String, Method>> methods;
  private final ArgumentDeserializerFactory factory;
  private final MethodDescriptor descriptor;

  @Inject
  RpcHelper(Map<String, InjectedService> services,
      ArgumentDeserializerFactory factory,
      MethodDescriptor descriptor) {
    this.services = services;
    this.factory = factory;
    this.descriptor = descriptor;
    methods = buildMethodMap(services);
  }

  public AbstractMessage invoke(String serviceName, String methodName, byte[] data) throws WebApplicationException {
    Object service = services.get(serviceName).getInstance();
    Method m = methods.get(serviceName).get(methodName);
    try {
      if (m.getParameterTypes().length != 0) {
        Object argument = factory.create(m).deserializeArgument(data);
        return invoke(m, service, argument);
      } else {
        return invoke(m, service);
      }
    } catch (IOException ex) {
      throw new WebApplicationException(ex);
    }
  }

  public RpcMethodDescriptor getMethodDescriptor(String service, String method) throws WebApplicationException {
    if (!methods.containsKey(service)) {
      throw new WebApplicationException(HttpServletResponse.SC_NOT_FOUND);
    }
    Method m = methods.get(service).get(method);
    if (m == null) {
      throw new WebApplicationException(HttpServletResponse.SC_NOT_FOUND);
    }
    return descriptor.getDescriptor(m);
  }

  private Map<String, Map<String, Method>> buildMethodMap(Map<String, InjectedService> services) {
    ImmutableMap.Builder<String, Map<String, Method>> builder = ImmutableMap.builder();
    for (String key : services.keySet()) {
      Class<?> serviceClass = services.get(key).getServiceClass();
      ImmutableMap.Builder<String, Method> methodMap = ImmutableMap.builder();
      Method[] methods = serviceClass.getDeclaredMethods();
      for (int i = 0; i < methods.length; i++) {
        Method m  = methods[i];
        m.setAccessible(true);
        methodMap.put(m.getName(), m);
      }
      builder.put(key, methodMap.build());
    }
    return builder.build();
  }

  public AbstractMessage invoke(Method method, Object on, Object argument) throws WebApplicationException {
    try {
      return (AbstractMessage) method.invoke(on, argument);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      throw new WebApplicationException(ex, 500);
    }
  }

  public AbstractMessage invoke(Method method, Object on) throws WebApplicationException {
    try {
      return (AbstractMessage) method.invoke(on);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      throw new WebApplicationException(ex, 500);
    }
  }
}
