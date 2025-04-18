package com.example.caching.util;

import com.example.caching.dto.DocumentDTO;
import com.example.caching.service.DocumentService;
import io.quarkus.grpc.GrpcClient;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.acme.grpc.document.caching.Document;
import org.acme.grpc.document.caching.DocumentCaching;

import static io.restassured.RestAssured.given;

public class DocumentUtils {

    public static String createDocumentWithTenant(String tenantId) {
        DocumentDTO doc = new DocumentDTO();
        doc.setTitle("Auto-generated Test Document");
        doc.setContent("Some content here");
        doc.setTenantId(tenantId);

        return given()
                .contentType(ContentType.JSON)
                .body(doc)
                .when()
                .post("/cache")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    public static String createGrpcDocumentWithTenant(DocumentService documentService,String tenantId)
    {
        DocumentDTO request = new DocumentDTO();
        request.setTenantId(tenantId);
        request.setTitle("Test Document");
        request.setContent("This is the content.");


        DocumentDTO documentDTO=documentService.createDocument(request);
        return documentDTO.getId();
    }
}
