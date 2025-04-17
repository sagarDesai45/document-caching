package com.example.caching.service;

import com.example.caching.dto.DocumentDTO;
import com.example.caching.exception.CustomException;
import com.example.caching.service.impl.RedisDocService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@QuarkusTest
public class RedisDocServiceTest {

    @InjectMock
    RedisDataSource redisDataSource;

    private ValueCommands<String, DocumentDTO> valueCommands;
    @Inject
    RedisDocService redisDocService;

    private UUID tenantId;

    @BeforeEach
    void setUp()
    {

        MockitoAnnotations.openMocks(this);
        valueCommands = Mockito.mock(ValueCommands.class);

        when(redisDataSource.value(DocumentDTO.class)).thenReturn(valueCommands);

        redisDocService = new RedisDocService(redisDataSource);

        tenantId = UUID.randomUUID();

    }

    @Test
    public void testCreateDocument_success() {
        DocumentDTO doc = new DocumentDTO();
        doc.setTitle("Test Title");
        doc.setContent("Test Content");
        doc.setTenantId(tenantId.toString());
        doNothing().when(valueCommands).set(UUID.randomUUID().toString(),doc);
        DocumentDTO documentDTO=redisDocService.createDocument(doc);
        assertNotNull(documentDTO.getId());
    }


    @Test
    public void testGetDocument_successful() {
        String docId=UUID.randomUUID().toString();
        DocumentDTO doc = new DocumentDTO();
        doc.setId(docId);
        doc.setTenantId(tenantId.toString());

        when(valueCommands.get(docId)).thenReturn(doc);

        DocumentDTO result = redisDocService.getDocument(docId, tenantId.toString());

        assertEquals(doc.getId(), result.getId());
    }

    @Test
    public void testGetDocument_notFound() {
        when(valueCommands.get("missing-id")).thenReturn(null);

        CustomException ex = assertThrows(CustomException.class, () -> {
            redisDocService.getDocument("missing-id", "tenant-xyz");
        });

        assertEquals(400, ex.getHttpStatus());
    }

    @Test
    public void testGetDocument_accessDenied() {
        String docId=UUID.randomUUID().toString();
        DocumentDTO doc = new DocumentDTO();
        doc.setId(docId);
        doc.setTenantId(tenantId.toString());

        when(valueCommands.get(docId)).thenReturn(doc);

        CustomException ex = assertThrows(CustomException.class, () -> {
            redisDocService.getDocument(docId, UUID.randomUUID().toString());
        });

        assertEquals(403, ex.getHttpStatus());
    }
}
