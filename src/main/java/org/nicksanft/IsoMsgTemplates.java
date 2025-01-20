package org.nicksanft;

import org.jpos.iso.ISOMsg;

import java.util.Random;

public class IsoMsgTemplates {

    public static ISOMsg buildNetworkStatus(){
        ISOMsg msg = new ISOMsg("1800");
        msg.set(11, rand6digitString());

        return msg;
    }

    public static ISOMsg buildPreauth(){
        ISOMsg msg = new ISOMsg("1100");
        msg.set(4, "10000");
        msg.set(11, rand6digitString());
        msg.set(41, "Terminal");
        msg.set(42, "Merchant");

        return msg;
    }

    public static ISOMsg buildCompletion(){
        ISOMsg msg = new ISOMsg("1220");
        msg.set(2, "4400123456781234");
        msg.set(4, "5000");
        msg.set(11, rand6digitString());
        msg.set(38, rand6digitString());
        msg.set(41, "Merchant");
        msg.set(42, "Merchant");

        return msg;
    }


    public static String rand6digitString(){
        Integer stan = new Random().nextInt(1,999999);
        return String.format("%06d", stan);
    }
}
