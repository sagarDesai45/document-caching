package com.example.caching.service;

import com.example.caching.model.Document;

public interface DocumentService {

    Document createDocument(Document document);

    Document getDocument(String id);
}
