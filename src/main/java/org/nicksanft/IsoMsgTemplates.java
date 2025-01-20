package org.nicksanft;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;

import java.util.Random;

public class IsoMsgTemplates {


    public static ISOMsg buildNetworkStatus(ISOPackager packager) {
        ISOMsg msg = new ISOMsg("1800");
        msg.setPackager(packager);
        msg.set(11, rand6digitString());

        return msg;
    }

    public static ISOMsg buildPreauth(ISOPackager packager, String card, String amount, String rrn, String terminal, String merchant) {
        ISOMsg msg = new ISOMsg("1100");
        msg.setPackager(packager);
        msg.set(2, card);
        msg.set(4, amount);
        msg.set(11, rand6digitString());
        msg.set(37, rrn);
        msg.set(41, terminal);
        msg.set(42, merchant);

        return msg;
    }

    public static ISOMsg buildCompletion(ISOPackager packager, String card, String amount, String rrn, String approvalCode, String terminal, String merchant) {
        ISOMsg msg = new ISOMsg("1220");
        msg.setPackager(packager);
        msg.set(2, card);
        msg.set(4, amount);
        msg.set(11, rand6digitString());
        msg.set(37, rrn);
        msg.set(38, approvalCode);
        msg.set(41, terminal);
        msg.set(42, merchant);

        return msg;
    }


    public static String rand6digitString() {
        Integer stan = new Random().nextInt(1, 999999);
        return String.format("%06d", stan);
    }
}
