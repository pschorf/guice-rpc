package com.pschorf.rpc.server.rpc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.pschorf.messages.Rpc.ArgumentType.Type;
import com.pschorf.messages.Rpc.ProtobufType;
import com.pschorf.messages.Rpc.RpcMethodDescriptor;

public class MethodDescriptorTest {


  private MethodDescriptor descriptor;

  @Before
  public void initDescriptor() {
    descriptor = new MethodDescriptor();
  }
  
  @Test
  public void testMessageArgument() throws Exception {
    RpcMethodDescriptor result = descriptor.getDescriptor(TestService.class.getMethod("update", ProtobufType.class));
    assertEquals(Type.PROTOBUF, result.getArgumentType().getType());
    assertEquals("update", result.getMethodName());
    ProtobufType type = result.getArgumentType().getProtoType();
    assertEquals("ProtobufType", type.getTypeName());
    assertEquals("rpc_pb2.ProtobufType", type.getPythonType());
  }

  @Test
  public void testReturnType() throws Exception {
    RpcMethodDescriptor result = descriptor.getDescriptor(TestService.class.getMethod("update", ProtobufType.class));
    ProtobufType type = result.getReturnType();
    assertEquals("ProtobufType", type.getTypeName());
    assertEquals("rpc_pb2.ProtobufType", type.getPythonType());
  }

  @Test
  public void testNoArgument() throws Exception {
    RpcMethodDescriptor result = descriptor.getDescriptor(TestService.class.getMethod("list"));
    assertEquals(Type.NONE, result.getArgumentType().getType());
  }

  private interface TestService {
    ProtobufType update(ProtobufType i);
    ProtobufType list();
  }
}
