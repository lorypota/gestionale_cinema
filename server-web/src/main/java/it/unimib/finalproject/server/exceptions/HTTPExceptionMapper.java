package it.unimib.finalproject.server.exceptions;

import it.unimib.finalproject.server.model.exceptions.ErrorModel;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class HTTPExceptionMapper implements ExceptionMapper<WebApplicationException>{
    @Override
    public Response toResponse(WebApplicationException exception) {
        int statusCode = exception.getResponse().getStatus();
        String message = exception.getMessage();

        ErrorModel errorModel = new ErrorModel();
        errorModel.setMessage(message);
        errorModel.setStatusCode(statusCode);

        return Response.status(statusCode)
                .entity(errorModel)
                .build();
    }
}
 