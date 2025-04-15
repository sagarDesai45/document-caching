package com.example.caching.service.impl;

import com.example.caching.dto.DocumentDTO;
import com.example.caching.exception.CustomException;
import com.example.caching.service.DocumentService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class RedisDocService implements DocumentService {

    private ValueCommands<String, DocumentDTO> valueCommands;

    public RedisDocService(RedisDataSource dataSource)
    {
        valueCommands=dataSource.value(DocumentDTO.class);
    }

    @Override
    public DocumentDTO createDocument(DocumentDTO document) {
        String id= document.getId();
        if(StringUtil.isNullOrEmpty(id))
        {
            id=UUID.randomUUID().toString();
        }
        document.setId(id);
        valueCommands.set(id,document);
        return document;
    }

    @Override
    public DocumentDTO getDocument(String documentId,String tenantId) {

        DocumentDTO document=valueCommands.get(documentId);
        if(Objects.isNull(document))
        {
            throw new CustomException("No Document Found", HttpResponseStatus.BAD_REQUEST.code());
        }

        if(!tenantId.equals(document.getTenantId()))
        {
            throw new CustomException("You don't have access", HttpResponseStatus.FORBIDDEN.code());
        }

        return document;
    }
}
