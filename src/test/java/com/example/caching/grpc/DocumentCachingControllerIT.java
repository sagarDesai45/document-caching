package com.example.caching.grpc;

import com.example.caching.dto.DocumentDTO;
import com.example.caching.exception.CustomException;
import com.example.caching.service.DocumentService;
import com.example.caching.util.DocumentUtils;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.grpc.document.caching.Document;
import org.acme.grpc.document.caching.DocumentCaching;
import org.acme.grpc.document.caching.GetDocumentRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@QuarkusTest
public class DocumentCachingControllerIT{

    @GrpcClient
    DocumentCaching documentCaching;

    @Inject
    DocumentService documentService;


    @Test
    void testCreateDocument() {

        DocumentDTO createdDTO = new DocumentDTO();
        createdDTO.setTenantId("testTenant");
        createdDTO.setTitle("Test Document");
        createdDTO.setContent("This is the content.");

        Document request = Document.newBuilder()
                .setTenantId("testTenant")
                .setTitle("Test Document")
                .setContent("This is the content.")
                .build();

        Document response = documentCaching.create(request).await().indefinitely();

        assertEquals("testTenant", response.getTenantId());
        assertEquals("Test Document", response.getTitle());
        assertEquals("This is the content.", response.getContent());

    }

    @Test
    void testGetDocument() {
        String tenantId= UUID.randomUUID().toString();
        String documentId= DocumentUtils.createGrpcDocumentWithTenant(documentService,tenantId);

        GetDocumentRequest request = GetDocumentRequest.newBuilder()
                .setDocumentId(documentId)
                .setTenantId(tenantId)
                .build();


        Document response = documentCaching.getDocument(request).await().indefinitely();


        assertEquals(documentId, response.getId());
        assertEquals(tenantId, response.getTenantId());

    }

    @Test
    void testGetDocument_NotFound() {
        String tenantId= UUID.randomUUID().toString();
        String documentId=UUID.randomUUID().toString();
        GetDocumentRequest request = GetDocumentRequest.newBuilder()
                .setDocumentId(documentId)
                .setTenantId(tenantId)
                .build();

        StatusRuntimeException thrown = assertThrows(StatusRuntimeException.class,
                () -> documentCaching.getDocument(request).await().indefinitely());

    }

    @Test
    void testGetDocument_AccessDenied() {
        String tenantId= UUID.randomUUID().toString();
        String documentId=DocumentUtils.createGrpcDocumentWithTenant(documentService,tenantId);
        GetDocumentRequest request = GetDocumentRequest.newBuilder()
                .setDocumentId(documentId)
                .setTenantId(UUID.randomUUID().toString())
                .build();

        StatusRuntimeException thrown = assertThrows(StatusRuntimeException.class,
                () -> documentCaching.getDocument(request).await().indefinitely());

    }
}
