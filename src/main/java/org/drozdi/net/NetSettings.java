package org.drozdi.net;

import lombok.Data;
import lombok.Getter;

@Data
public class NetSettings {
    @Getter
    public static final int defaultPacketSize = 1028;
    @Getter
    public static final int mapChunkSize = 1024; //chunkSize for download map
    @Getter
    public static final int packetHeaderSize = 4;
}
