package com.pschorf.rpc.server.rpc;

import java.lang.reflect.Method;

interface ArgumentDeserializerFactory {
  ArgumentDeserializerImpl create(Method method);
}
