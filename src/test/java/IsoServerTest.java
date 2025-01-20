import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.ISO93APackager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nicksanft.IsoClient;
import org.nicksanft.IsoMsgTemplates;
import org.nicksanft.IsoServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IsoServerTest {

    private static final String host = "localhost";
    private static final int port = 8080;
    private static final ISOPackager packager = new ISO93APackager();

    private static final String CARD_NUMBER = "4400123456781234";
    private static final String MERCHANT_ID = "WaikikiResorts";
    private static final String TERMINAL_ID = "223344";


    static IsoServer server;

    @BeforeAll
    public static void setUp() throws InterruptedException {
        server = new IsoServer();
        server.startServer(host, port, packager);
    }

    @Test
    public void networkStatus() {
        var request = IsoMsgTemplates.buildNetworkStatus(packager);
        var response = buildAndSend(request);
        response.dump(System.out, "Response");

        assertEquals("1810", response.getString(0));
        assertEquals(request.getString(11), response.getString(11));
        assertEquals("800", response.getString(39));
    }

    @Test
    public void preAuth() {
        String rrn = "555666777888";
        var request = IsoMsgTemplates.buildPreauth(packager, CARD_NUMBER,"100000", rrn, TERMINAL_ID, MERCHANT_ID );
        var response = buildAndSend(request);
        response.dump(System.out, "Response");

        assertEquals("1110", response.getString(0));
        assertEquals(rrn, response.getString(37));
        assertTrue(response.hasField(38), "DE 38 (Approval Code) must be populated!");
        assertEquals("000", response.getString(39));
    }

    @Test
    public void completion() {
        String rrn = "333222111333";
        String approvalCode = "668877";
        var request = IsoMsgTemplates.buildCompletion(packager, CARD_NUMBER,"50000", rrn, approvalCode, TERMINAL_ID, MERCHANT_ID);
        var response = buildAndSend(request);
        response.dump(System.out, "Response");

        assertEquals("1230", response.getString(0));
        assertEquals(rrn, response.getString(37));
        assertTrue(response.hasField(38), "DE 38 (Approval Code) must be populated!");
        assertEquals("000", response.getString(39));
    }

    private ISOMsg buildAndSend(ISOMsg request) {
        var client = new IsoClient();
        return client.startClientAndSend(host, port, packager, request);
    }
}
