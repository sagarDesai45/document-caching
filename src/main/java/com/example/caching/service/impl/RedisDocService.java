package com.example.caching.service.impl;

import com.example.caching.model.Document;
import com.example.caching.service.DocumentService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class RedisDocService implements DocumentService {

    private ValueCommands<String,Document> valueCommands;

    public RedisDocService(RedisDataSource dataSource)
    {
        valueCommands=dataSource.value(Document.class);
    }

    @Override
    public Document createDocument(Document document) {
        String id= UUID.randomUUID().toString();
        document.setId(id);

        valueCommands.set(id,document);
        return document;
    }

    @Override
    public Document getDocument(String id) {

        Document document=valueCommands.get(id);
        if(!Objects.nonNull(document))
        {
            throw new RuntimeException("No Document Found");
        }
        return document;
    }
}
