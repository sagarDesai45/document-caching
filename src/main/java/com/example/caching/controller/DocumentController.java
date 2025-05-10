package com.example.caching.controller;

import com.example.caching.dto.DocumentDTO;
import com.example.caching.service.DocumentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/cache")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentController {

    @Inject
    private DocumentService documentService;


    @POST
    public Response createDocument(@Valid DocumentDTO document)
    {
        DocumentDTO newDoc=documentService.createDocument(document);
        return Response.status(Response.Status.CREATED).entity(newDoc).build();
    }

    @GET
    public Response getDocument(@QueryParam("documentId") UUID documentId,@QueryParam("tenantId") UUID tenantId) {
        DocumentDTO doc=documentService.getDocument(documentId.toString(),tenantId.toString());
        return Response.status(Response.Status.OK).entity(doc).build();
    }
}
