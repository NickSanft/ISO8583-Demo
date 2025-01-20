package org.nicksanft;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

public class IsoClient {

    public void startClientAndSend(String host, int port, ISOPackager packager, ISOMsg request) {
        // Start the client
        System.out.println("Starting client");
        TcpClient.create()
                .host(host)
                .port(port)
                .handle((inbound, outbound) -> {
                    // Send a message to the server
                    request.dump(System.out, "Request out");
                    try {
                        return outbound.sendByteArray(Mono.just(request.pack())) // Send the request message
                                .then() // Wait for the message to be fully sent
                                .then(inbound.receive() // Now wait for the response from the server
                                        .asByteArray()
                                        .doOnNext(msg -> {
                                            try {
                                                var response = new ISOMsg();
                                                response.setPackager(packager);
                                                response.unpack(msg);
                                                response.dump(System.out, "Response in");
                                            } catch (ISOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        })
                                        .then()); // Complete the flow after receiving the message
                    } catch (ISOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .connect()
                .block(); // Block until the client completes the communication

        // This line will be printed after the communication completes
        System.out.println("Client communication completed.");
    }

}