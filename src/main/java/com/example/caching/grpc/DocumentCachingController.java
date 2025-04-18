package com.example.caching.grpc;

import com.example.caching.dto.DocumentDTO;
import com.example.caching.service.DocumentService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import org.acme.grpc.document.caching.Document;
import org.acme.grpc.document.caching.DocumentCaching;
import org.acme.grpc.document.caching.GetDocumentRequest;

@GrpcService
public class DocumentCachingController implements DocumentCaching {

    @Inject
    DocumentService documentService;

    @Override
    public Uni<Document> create(Document request) {

        DocumentDTO documentDTO=new DocumentDTO();
        documentDTO.setTenantId(request.getTenantId());
        documentDTO.setContent(request.getContent());
        documentDTO.setTitle(request.getTitle());

        return Uni.createFrom().item(() -> request)
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .map(req -> {
                    DocumentDTO createdDoc=documentService.createDocument(documentDTO);

                    return Document.newBuilder().setId(createdDoc.getId())
                            .setContent(createdDoc.getContent())
                            .setTitle(createdDoc.getTitle())
                            .setTenantId(createdDoc.getTenantId())
                            .build();
                });

    }

    @Override
    public Uni<Document> getDocument(GetDocumentRequest request) {
        return Uni.createFrom().item(() -> request)
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .map(req -> {
                    DocumentDTO documentDTO=documentService.getDocument(request.getDocumentId(), request.getTenantId());

                    return Document.newBuilder().setId(documentDTO.getId())
                            .setTenantId(documentDTO.getTenantId())
                            .setTitle(documentDTO.getTitle())
                            .setContent(documentDTO.getContent())
                            .build();
                });
    }
}
