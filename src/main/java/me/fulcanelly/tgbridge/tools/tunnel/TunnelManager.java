package me.fulcanelly.tgbridge.tools.tunnel;

import java.net.URI;
import java.util.Optional;

import org.bukkit.Server;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import com.google.inject.Inject;

import lombok.SneakyThrows;

public class TunnelManager {

    @Inject
    NgrokClient ngrokClient;

    @Inject
    Server server;

    Optional<Tunnel> tunnel = Optional.empty();

    @SneakyThrows
    public String normalizedUrl() {
        var uri = new URI(obtainTunnel().getPublicUrl());
        return uri.getHost() + ':' + uri.getPort();
    }

    public synchronized Tunnel obtainTunnel() {
        // TODO: remove
        System.out.println(ngrokClient.getTunnels());

        if (tunnel.isEmpty()) {
            var tunnelCreationReq = new CreateTunnel.Builder()
                        .withProto(Proto.TCP)
                        .withAddr(server.getPort())
                        .build();

            tunnel = Optional.of(ngrokClient.connect(tunnelCreationReq));

        }

        return tunnel.get();
    }

}
