package org.nicksanft;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpServer;

public class IsoServer {

    public void startServer(String host, int port, ISOPackager packager) {
        System.out.println("Starting IsoServer");
        Thread thread = new Thread(() -> {
            TcpServer.create()
                    .host(host)
                    .port(port)
                    .handle((inbound, outbound) -> {
                        // Log the received message from the client
                        return inbound.receive()
                                .asByteArray() // Ensure that the received message is in byte array form
                                .doOnNext(msg -> {
                                    System.out.println("Server received: " + ISOUtil.byte2hex(msg)); // Log the received message in hexadecimal
                                })
                                .flatMap(msg -> {
                                    try {
                                        System.out.println("Processing received message");
                                        var request = new ISOMsg();
                                        request.setPackager(packager);
                                        request.unpack(msg); // Unpack the received message using the packager
                                        request.dump(System.out, "Request in"); // Debug the unpacked message

                                        // Prepare response
                                        var response = (ISOMsg) request.clone();
                                        response.setResponseMTI(); // Set response MTI (Message Type Indicator)

                                        var requestMti = request.getMTI();

                                        // 800 for Network Statuses, 000 and an approval code for everyone else.
                                        if (requestMti.startsWith("08") || requestMti.startsWith("18")) {
                                            response.set(39, "800");
                                        } else {
                                            response.set(38, IsoMsgTemplates.rand6digitString());
                                            response.set(39, "000");
                                        }
                                        response.dump(System.out, "Response out");

                                        // Send back the response
                                        byte[] responseBytes = response.pack();
                                        System.out.println("Server sending response: " + ISOUtil.byte2hex(responseBytes));

                                        return outbound.sendByteArray(Mono.just(responseBytes)); // Send the response back to the client
                                    } catch (ISOException e) {
                                        e.printStackTrace();
                                        return outbound.sendByteArray(Mono.just("ERROR".getBytes())); // Send an error if unpacking fails
                                    }
                                })
                                .then(); // Complete the flow
                    })
                    .bindNow()
                    .onDispose() // Ensure the thread blocks until server shuts down
                    .block(); // Block the main thread to keep the server alive
        });
        thread.start();
        System.out.println("Started IsoServer");
    }
}