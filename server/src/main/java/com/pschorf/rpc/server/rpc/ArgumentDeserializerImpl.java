package com.pschorf.rpc.server.rpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.sun.jersey.spi.MessageBodyWorkers;

class ArgumentDeserializerImpl implements ArgumentDeserializer {
  
  private final Provider<MediaType> mediaType;
  private final Provider<MultivaluedMap<String, String>> headers;
  private final Type genericType;
  private final Class rawType;
  private final MessageBodyWorkers workers;

  @Inject
  ArgumentDeserializerImpl(Provider<MediaType> mediaType,
      Provider<MultivaluedMap<String, String>> headers,
      MessageBodyWorkers workers,
      @Assisted Method method) {
    this.mediaType = mediaType;
    this.headers = headers;
    this.genericType = method.getGenericParameterTypes()[0];
    this.workers = workers;
    if (genericType instanceof ParameterizedType) {
      this.rawType = (Class<?>) ((ParameterizedType) genericType).getRawType();
    } else if (genericType instanceof Class<?>) {
      this.rawType = (Class<?>) genericType;
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public Object deserializeArgument(byte[] data) throws IOException {
    MediaType mime = mediaType.get();
    MessageBodyReader<?> reader = workers.getMessageBodyReader(rawType, genericType, new Annotation[] {}, mime);
    if (reader == null) {
      System.err.println(workers.getReaders(mime));
    }
    return reader.readFrom(rawType, genericType, new Annotation[] {}, mime, headers.get(), new ByteArrayInputStream(data));
  }

}
