package com.example.caching.controller;

import com.example.caching.dto.DocumentDTO;
import com.example.caching.service.DocumentService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class DocumentControllerTest {

    @InjectMock
    DocumentService documentService;

    @Inject
    DocumentController documentController;

    private UUID docId;

    private UUID tenantId;

    @BeforeEach
    public  void setUp(){
        docId=UUID.randomUUID();
        tenantId=UUID.randomUUID();
    }

    @Test
    public void testCreateDocument_Success(){

        DocumentDTO inputDto=new DocumentDTO();
        inputDto.setId(docId.toString());
        inputDto.setTenantId(tenantId.toString());
        inputDto.setTitle("Test Title");
        inputDto.setContent("Test Content");

        Mockito.when(documentService.createDocument(inputDto)).thenReturn(inputDto);

    }

    @Test
    public void testCreateDocument_InvalidInput(){

        DocumentDTO inputDto=new DocumentDTO();
        inputDto.setId(docId.toString());
        inputDto.setTitle("Test Title");
        inputDto.setContent("Test Content");

        Mockito.when(documentService.createDocument(inputDto)).thenReturn(inputDto);


        given()
                .contentType(ContentType.JSON)
                .body(inputDto)
                .when()
                .post("/cache")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void getDocument_Success(){

        DocumentDTO inputDto=new DocumentDTO();
        inputDto.setId(docId.toString());
        inputDto.setTenantId(tenantId.toString());
        inputDto.setTitle("Test Title");
        inputDto.setContent("Test Content");

        Mockito.when(documentService.createDocument(inputDto)).thenReturn(inputDto);

        Response response=documentController.getDocument(docId,tenantId);

        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

}
