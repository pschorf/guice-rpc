package com.noveltyplant.rest.server.rpc;

import java.lang.reflect.Method;

interface ArgumentDeserializerFactory {
  ArgumentDeserializerImpl create(Method method);
}
