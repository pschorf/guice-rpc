package com.pschorf.rpc.server;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.WeakHashMap;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces({"application/json"})
public class IterableJsonSerializer implements MessageBodyWriter<Iterable<?>> {
	
	private Map<Object, byte[]> buffer = new WeakHashMap<Object, byte[]>();

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return Iterable.class.isAssignableFrom(type);
		
	}

	@Override
	public long getSize(Iterable<?> t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		StringBuilder sb = new StringBuilder("[");
		boolean first = true;
		for (Object obj : t) {
			if (!first) {
				sb.append(",");
			} else {
				first = false;
			}
			String serialized = obj.toString().replace('"', '\'');
			sb.append("\"").append(serialized).append("\"");
		}
		byte[] bytes = sb.append("]").toString().getBytes();
		buffer.put(t, bytes);
		return bytes.length;
	}

	@Override
	public void writeTo(Iterable<?> t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		entityStream.write(buffer.remove(t));

	}

}
