package com.pschorf.rpc.server.rpc;

import com.google.inject.Inject;
import com.google.protobuf.AbstractMessage;
import com.pschorf.messages.Rpc.RpcMethodDescriptor;
import com.pschorf.rpc.MimeTypes;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

@Path("/rpc")
public class RpcServlet {

  private final RpcHelper helper;

  @Inject
  RpcServlet(RpcHelper helper) {
    this.helper = helper;
  }

  @POST
  @Path("/{service}/{method}")
  @Produces({ "application/json", "application/x-protobuf" })
  @Consumes({"text/plain", "application/x-protobuf"})
  public AbstractMessage post(@PathParam("service") String service, @PathParam("method") String method, byte[] body) throws WebApplicationException {
    return helper.invoke(service, method, body);
  }

  @GET
  @Path("/{service}/{method}")
  @Produces({MimeTypes.JSON, MimeTypes.PROTOBUF})
  public RpcMethodDescriptor get(@PathParam("service") String service, @PathParam("method") String method) {
    return helper.getMethodDescriptor(service, method);
  }
}
