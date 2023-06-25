package it.unimib.finalproject.server.exceptions;

import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


// TODO: Mappaggio di eccezioni esistenti a una Response,
// posso anche creare di nuove che estendono WebApplicationException e throwarle nel metodo del controller
// SRC: https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/representations.html#d0e6674

@Provider
public class RESPErrorMapper implements ExceptionMapper<RESPError> {
  public Response toResponse(RESPError ex) {
    return Response.serverError().entity(ex).type(MediaType.APPLICATION_JSON).build();
  }
}
