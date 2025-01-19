package org.nicksanft;

import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

public class Main {

    private static String host = "localhost";
    private static int port = 8080;

    // Method to start a simple TCP server using Reactor
    private static void startServer() {
        TcpServer.create()
                .host(host)
                .port(port)
                .handle((inbound, outbound) -> {
                    // Log the received message from the client
                    inbound.receive()
                            .asString()
                            .doOnNext(msg -> System.out.println("Server received: " + msg)) // Log the received message
                            .subscribe(); // Ensure subscription to process the incoming message

                    // Send a response back to the client
                    return outbound.sendString(Mono.just("goodbye\n")).then();
                })
                .bindNow()
                .onDispose() // Ensure we block the thread until server shuts down
                .block(); // Block the main thread to keep the server alive
    }

    public static void main(String[] args) throws InterruptedException {

        // Start the server in a separate thread
        new Thread(Main::startServer).start();

        // Allow the server time to start up
        Thread.sleep(1000);

        // Start the client
        System.out.println("Starting client");
        TcpClient.create()
                .host(host)
                .port(port)
                .handle((inbound, outbound) -> {
                    // Send a message to the server
                    outbound.sendString(Mono.just("hello"));

                    // Receive the response from the server
                    return inbound.receive()
                            .asString()
                            .doOnNext(msg -> {
                                // Ensure we print the received message
                                System.out.println("Client received: " + msg);
                            })
                            .then(); // Ensure the flow completes after receiving the message
                })
                .connect()
                .block(); // Block until the client completes the communication

        // This line will be printed after the communication completes
        System.out.println("Client communication completed.");
    }
}