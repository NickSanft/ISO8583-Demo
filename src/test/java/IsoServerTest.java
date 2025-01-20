import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.ISO93APackager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nicksanft.IsoClient;
import org.nicksanft.IsoMsgTemplates;
import org.nicksanft.IsoServer;

public class IsoServerTest {

    private static String host = "localhost";
    private static int port = 8080;
    private static ISOPackager packager = new ISO93APackager();

    static IsoServer server;

    @BeforeAll
    public static void setUp() {
        server = new IsoServer();
        server.startServer(host, port, packager);
    }

    @Test
    public void networkStatus() {
        var request = IsoMsgTemplates.buildNetworkStatus(packager);
        var client = new IsoClient();
        client.startClientAndSend(host, port, packager, request);
    }
}
