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

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;

@Provider
@Produces("application/json")
public class ProtobufJsonSerializer implements MessageBodyWriter<Message> {

	@Override
	public long getSize(Message message, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		String str = JsonFormat.printToString(message);
		byte[] bytes = str.getBytes();
		buffer.put(message, bytes);
		return bytes.length;
	}

	private Map<Object, byte[]> buffer = new WeakHashMap<Object, byte[]>();

	@Override
	public boolean isWriteable(Class<?> type, Type arg1, Annotation[] arg2,
			MediaType arg3) {
		return Message.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(Message message, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4, MultivaluedMap<String, Object> arg5,
			OutputStream stream)
			throws IOException, WebApplicationException {
		stream.write(buffer.remove(message));
	}

}
