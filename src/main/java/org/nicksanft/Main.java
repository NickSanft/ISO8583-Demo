package org.nicksanft;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.ISO93APackager;

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
        var client = new IsoClient();
        client.startClient(host, port, packager, request);
    }
}