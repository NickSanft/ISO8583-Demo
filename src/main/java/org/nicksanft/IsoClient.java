package org.nicksanft;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

public class IsoClient {

    public ISOMsg startClientAndSend(String host, int port, ISOPackager packager, ISOMsg request) {
        System.out.println("Starting client");

        // Create a Mono to capture the response
        Mono<ISOMsg> responseMono = Mono.create(sink -> TcpClient.create()
                .host(host)
                .port(port)
                .handle((inbound, outbound) -> {
                    try {
                        // Pack the request
                        byte[] requestBytes = request.pack();
                        request.dump(System.out, "Request out");

                        // Send the request and handle the response
                        return outbound.sendByteArray(Mono.just(requestBytes))
                                .then(inbound.receive()
                                        .asByteArray()
                                        .next() // Get the first response as Mono<byte[]>
                                        .flatMap(msg -> {
                                            try {
                                                // Unpack the response
                                                ISOMsg response = new ISOMsg();
                                                response.setPackager(packager);
                                                response.unpack(msg);
                                                response.dump(System.out, "Response in");
                                                sink.success(response); // Emit the response to the sink
                                                return Mono.empty(); // Complete the pipeline
                                            } catch (ISOException e) {
                                                sink.error(e); // Propagate the error
                                                return Mono.error(e);
                                            }
                                        }).then()); // Return Mono<Void>
                    } catch (ISOException e) {
                        sink.error(e); // Handle packing errors
                        return Mono.error(e);
                    }
                })
                .connect()
                .block()); // Ensure the connection completes

        // Block to retrieve the response from the Mono
        return responseMono.block();
    }
}