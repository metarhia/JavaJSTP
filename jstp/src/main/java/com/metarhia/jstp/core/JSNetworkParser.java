package com.metarhia.jstp.core;

import com.metarhia.jstp.core.JSTypes.JSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lundibundi on 2/19/17.
 */
public class JSNetworkParser {

    private static final byte TERMINATOR = '\0';

    public static List<JSObject> parse(byte[] msg, int[] length) throws JSParsingException {
        List<JSObject> packets = new ArrayList<>();
        JSParser parser = new JSParser();
        int chunkStart = 0;
        int chunkLength = bytesUntil(msg, chunkStart, length[0], TERMINATOR);
        while (chunkLength != -1 && chunkStart < length[0]) {
            String packetData = new String(msg, chunkStart, chunkLength);
            parser.setInput(packetData);
            packets.add(parser.parseObject());
            chunkStart += chunkLength;
            chunkLength = bytesUntil(msg, chunkStart, length[0], TERMINATOR);
        }
        int restLength = length[0] - chunkStart;
        if (restLength > 0) {
            System.arraycopy(msg, chunkStart, msg, 0, restLength);
            length[0] = restLength;
        } else {
            length[0] = 0;
        }
        return packets;
    }

    private static int bytesUntil(byte[] bytes, int offset, int length, byte until) {
        int counter = 0;
        for (int i = offset; i < length; i++) {
            if (bytes[counter++] == until) return counter;
        }
        return -1;
    }
}
