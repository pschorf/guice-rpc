package com.pschorf.rpc;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import com.pschorf.messages.Rpc.ProtobufType;
import com.pschorf.rpc.MimeTypes;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ClientProxyFactoryTest {

  private static final URI BASE_URI = URI.create("http://example.com/rpc/test/");

  @Mock private Client client;
  @Mock private WebResource resource;
  @Mock private WebResource.Builder resourceBuilder;
  private ClientProxyFactory factory;

  @Before
  public void createFactory() {
    MockitoAnnotations.initMocks(this);
    factory = new ClientProxyFactory(client);
  }


  @Test
  public void testPostString() {
    String arg = "argument";
    String result = "result";
    TestService service = factory.createProxy(TestService.class, BASE_URI);
    URI methodUri = BASE_URI.resolve("a");
    when(client.resource(methodUri)).thenReturn(resource);
    when(resource.accept(MimeTypes.PROTOBUF)).thenReturn(resourceBuilder);
    when(resourceBuilder.header("Content-Type", "text/plain")).thenReturn(resourceBuilder);
    when(resourceBuilder.post(String.class, arg)).thenReturn(result);

    assertEquals(result, service.a(arg));
  }

  @Test
  public void testPostProtobuf() {
    ProtobufType argument = ProtobufType.getDefaultInstance();
    ProtobufType result = ProtobufType.newBuilder()
      .setTypeName("result")
      .build();
    TestService service = factory.createProxy(TestService.class, BASE_URI);
    URI methodUri = BASE_URI.resolve("b");
    when(client.resource(methodUri)).thenReturn(resource);
    when(resource.accept(MimeTypes.PROTOBUF)).thenReturn(resourceBuilder);
    when(resourceBuilder.header("Content-Type", MimeTypes.PROTOBUF)).thenReturn(resourceBuilder);
    when(resourceBuilder.post(ProtobufType.class, argument)).thenReturn(result);

    assertEquals(result, service.b(argument));
  }

  private static interface TestService {
    String a(String argument);
    ProtobufType b(ProtobufType req);
  }
}
