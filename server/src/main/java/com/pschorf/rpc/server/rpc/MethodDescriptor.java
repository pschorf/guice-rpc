package com.pschorf.rpc.server.rpc;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import com.pschorf.messages.Rpc.ArgumentType;
import com.pschorf.messages.Rpc.ProtobufType;
import com.pschorf.messages.Rpc.RpcMethodDescriptor;

class MethodDescriptor {

  RpcMethodDescriptor getDescriptor(Method m) {
    ArgumentType typeProto;
    if (m.getGenericParameterTypes().length > 0) {
      Type argType = m.getGenericParameterTypes()[0];
      if (isMessage(argType)) {
        typeProto = ArgumentType.newBuilder()
            .setType(ArgumentType.Type.PROTOBUF)
            .setProtoType(getProtobufSubtype((Class<?>) argType))
            .build();
      } else {
        throw new IllegalArgumentException("Unsupported argument type");
      }
    } else { 
      typeProto = ArgumentType.newBuilder()
        .setType(ArgumentType.Type.NONE)
        .build();
    }

    return RpcMethodDescriptor.newBuilder()
      .setArgumentType(typeProto)
      .setMethodName(m.getName())
      .setReturnType(getProtobufSubtype(m.getReturnType()))
      .build();
  }

  private boolean isMessage(Type t) {
    return t instanceof Class<?> && Message.class.isAssignableFrom((Class<?>) t);
  }

  private ProtobufType getProtobufSubtype(Class<?> klass) {
    Message message;
    try {
      Method getMessage = klass.getMethod("getDefaultInstance");
      message = (AbstractMessage) getMessage.invoke(null);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    Descriptor d = message.getDescriptorForType();
    String pythonName = d.getFile().getName();
    pythonName = pythonName.replace('/', '.');
    pythonName = pythonName.replace(".proto", "_pb2.");
    pythonName = pythonName.concat(d.getName());
    return ProtobufType.newBuilder()
      .setPythonType(pythonName)
      .setTypeName(d.getFullName())
      .build();
  }
}
