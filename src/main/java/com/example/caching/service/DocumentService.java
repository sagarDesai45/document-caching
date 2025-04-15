package com.example.caching.service;

import com.example.caching.dto.DocumentDTO;

public interface DocumentService {

    DocumentDTO createDocument(DocumentDTO document);

    DocumentDTO getDocument(String documentId,String tenantId);
}
