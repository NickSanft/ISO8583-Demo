package org.nicksanft;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.ISO93APackager;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

public class Main {

    private static String host = "localhost";
    private static int port = 8080;
    private static ISOPackager packager = new ISO93APackager();

    public static void main(String[] args) throws InterruptedException, ISOException {
        var server = new IsoServer();
        server.startServer(host, port, packager);

        // Allow the server time to start up
        Thread.sleep(2000);

        // Create the request message
        var request = new ISOMsg("1800");
        request.set(2, "599999999");
        request.setPackager(packager);
        var requestBytes = request.pack();

        // Start the client
        System.out.println("Starting client");
        TcpClient.create()
                .host(host)
                .port(port)
                .handle((inbound, outbound) -> {
                    // Send a message to the server
                    System.out.println("Sending: " + ISOUtil.byte2hex(requestBytes)); // Log the outgoing message
                    return outbound.sendByteArray(Mono.just(requestBytes)) // Send the request message
                            .then() // Wait for the message to be fully sent
                            .then(inbound.receive() // Now wait for the response from the server
                                    .asByteArray()
                                    .doOnNext(msg -> {
                                        // Ensure we print the received message
                                        System.out.println("Client received: " + ISOUtil.byte2hex(msg)); // Log the incoming response in hex
                                    })
                                    .then()); // Complete the flow after receiving the message
                })
                .connect()
                .block(); // Block until the client completes the communication

        // This line will be printed after the communication completes
        System.out.println("Client communication completed.");
    }


}