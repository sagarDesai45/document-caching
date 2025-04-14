package com.example.caching.controller;

import com.example.caching.model.Document;
import com.example.caching.service.DocumentService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/document")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentController {

    @Inject
    private DocumentService documentService;


    @POST
    public Response createDocument(Document document)
    {
        Document newDoc=documentService.createDocument(document);
        return Response.status(Response.Status.CREATED).entity(newDoc).build();
    }

    @GET
    @Path("/{id}")
    public Response getDocument(@PathParam("id") UUID id) {
        Document doc=documentService.getDocument(id.toString());
        return Response.status(Response.Status.OK).entity(doc).build();
    }
}
