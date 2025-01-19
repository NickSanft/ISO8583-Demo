package org.nicksanft;

import org.jpos.iso.ISOMsg;

public class Main {
    public static void main(String[] args) {
        ISOMsg msg = new ISOMsg("1800");
        msg.dump(System.out, "");
    }
}