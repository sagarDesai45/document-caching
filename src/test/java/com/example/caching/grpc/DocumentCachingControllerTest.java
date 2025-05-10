package com.example.caching.grpc;

import com.example.caching.dto.DocumentDTO;
import com.example.caching.exception.CustomException;
import com.example.caching.service.DocumentService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.grpc.document.caching.Document;
import org.acme.grpc.document.caching.GetDocumentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
public class DocumentCachingControllerTest {

    DocumentService documentService;

    DocumentCachingController documentController;

    @BeforeEach
    void setUp() {
        documentService = mock(DocumentService.class);
        documentController = new DocumentCachingController();
        documentController.documentService = documentService;
    }

    @Test
    public void testCreateSuccess()
    {
        Document docRequest=Document.newBuilder().setContent("Content")
                .setTitle("Title").setTenantId(UUID.randomUUID().toString()).build();

        DocumentDTO documentDTO=new DocumentDTO();
        documentDTO.setTitle("Title");
        documentDTO.setId(UUID.randomUUID().toString());
        documentDTO.setTenantId(UUID.randomUUID().toString());
        documentDTO.setContent("Content");

        when(documentService.createDocument(any(DocumentDTO.class))).thenReturn(documentDTO);

        Document result=documentController.create(docRequest).await().indefinitely();

        assertNotNull(result);
        assertNotNull(result.getId());
    }

    @Test
    public void testGetDocument_success()
    {
        UUID documentId=UUID.randomUUID();
        UUID tenantId=UUID.randomUUID();

        GetDocumentRequest documentRequest=GetDocumentRequest.newBuilder().setDocumentId(documentId.toString())
                .setTenantId(tenantId.toString()).build();

        DocumentDTO documentDTO=new DocumentDTO();
        documentDTO.setTitle("Title");
        documentDTO.setId(documentId.toString());
        documentDTO.setTenantId(tenantId.toString());
        documentDTO.setContent("Content");

        when(documentService.getDocument(documentId.toString(),tenantId.toString())).thenReturn(documentDTO);

        Document result=documentController.getDocument(documentRequest).await().indefinitely();

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(documentId.toString(),result.getId());
    }

    @Test
    public void testGetDocument_NotFound()
    {
        UUID documentId=UUID.randomUUID();
        UUID tenantId=UUID.randomUUID();

        GetDocumentRequest documentRequest=GetDocumentRequest.newBuilder().setDocumentId(documentId.toString())
                .setTenantId(tenantId.toString()).build();

        CustomException exception=new CustomException("Document not found", HttpResponseStatus.BAD_REQUEST.code());

        when(documentService.getDocument(documentId.toString(),tenantId.toString())).thenThrow(exception);

        CustomException thrown = assertThrows(CustomException.class, () -> documentController.getDocument(documentRequest).await().indefinitely());

        assertEquals(400, thrown.getHttpStatus());

    }

    @Test
    public void testGetDocument_AccessDenied()
    {
        UUID documentId=UUID.randomUUID();
        UUID tenantId=UUID.randomUUID();

        GetDocumentRequest documentRequest=GetDocumentRequest.newBuilder().setDocumentId(documentId.toString())
                .setTenantId(tenantId.toString()).build();

        CustomException exception=new CustomException("You don't have access", HttpResponseStatus.FORBIDDEN.code());

        when(documentService.getDocument(documentId.toString(),tenantId.toString())).thenThrow(exception);

        CustomException thrown = assertThrows(CustomException.class, () -> documentController.getDocument(documentRequest).await().indefinitely());

        assertEquals(403, thrown.getHttpStatus());

    }
}
