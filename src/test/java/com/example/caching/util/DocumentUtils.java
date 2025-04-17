package com.example.caching.util;

import com.example.caching.dto.DocumentDTO;
import io.restassured.http.ContentType;

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
}
