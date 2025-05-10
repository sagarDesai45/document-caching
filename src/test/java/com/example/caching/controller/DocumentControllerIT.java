package com.example.caching.controller;

import com.example.caching.dto.DocumentDTO;
import com.example.caching.util.DocumentUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.hamcrest.CoreMatchers.is;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class DocumentControllerIT {

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

        given()
                .contentType(ContentType.JSON)
                .body(inputDto)
                .when()
                .post("/cache")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(ContentType.JSON)
                .body("title", is("Test Title"))
                .body("id", is(inputDto.getId()));

    }

    @Test
    public void testCreateDocument_InvalidInput(){

        DocumentDTO inputDto=new DocumentDTO();
        inputDto.setId(docId.toString());
        inputDto.setTitle("Test Title");
        inputDto.setContent("Test Content");

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

        String docId= DocumentUtils.createDocumentWithTenant(tenantId.toString());


        given()
                .queryParam("documentId", docId)
                .queryParam("tenantId", tenantId)
                .contentType(ContentType.JSON)
                .when()
                .get("/cache")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

    }

    @Test
    public void getDocument_NotFound(){

        String docId=UUID.randomUUID().toString();


        given()
                .queryParam("documentId", docId)
                .queryParam("tenantId", tenantId)
                .contentType(ContentType.JSON)
                .when()
                .get("/cache")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

    }

    @Test
    public void getDocument_AccessDenied(){

        String docId= DocumentUtils.createDocumentWithTenant(UUID.randomUUID().toString());


        given()
                .queryParam("documentId", docId)
                .queryParam("tenantId", tenantId)
                .contentType(ContentType.JSON)
                .when()
                .get("/cache")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

    }
}
