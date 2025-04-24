package com.example.caching.config;

import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.consul.ConsulClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ServiceRegistry {
    @ConfigProperty(name = "consul.host",defaultValue = "localhost") String host;
    @ConfigProperty(name = "consul.port",defaultValue = "8500") int port;

    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8082") int documentPort;
    @ConfigProperty(name = "quarkus.application.name", defaultValue = "document-caching") String documentCaching;

    public void init(@Observes StartupEvent ev, Vertx vertx) {

        ConsulClient client = ConsulClient.create(Vertx.vertx(),
                new ConsulClientOptions().setHost(host).setPort(port));

        client.registerServiceAndAwait(
                new ServiceOptions().setPort(documentPort).setAddress(host)
                        .setName(documentCaching).setId(documentCaching));

    }

}
