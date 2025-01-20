package org.nicksanft;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.ISO93APackager;

public class Main {

    private static final ISOPackager packager = new ISO93APackager();

    public static void main(String[] args) throws InterruptedException, ISOException {
        var server = new IsoServer();
        String host = "localhost";
        int port = 8080;
        server.startServer(host, port, packager);
        // Allow the server time to start up
        Thread.sleep(2000);
        // Create the request message
        var request = IsoMsgTemplates.buildNetworkStatus(packager);
        var client = new IsoClient();
        var response = client.startClientAndSend(host, port, packager, request);
        response.dump(System.out, "Final Response");
    }
}