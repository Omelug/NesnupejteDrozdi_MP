package drozdi.net;

import lombok.Data;
import lombok.Getter;

@Data
public class NetConst {
    @Getter
    public static final int packetSizeNoID = 1024;

    //@Getter
   // public static final int mapChunkSize = 1024; //chunkSize for download map
    @Getter
    public static final int sizeID = 4;

    @Getter
    public static final int packetSizeWithID = packetSizeNoID+sizeID;
    @Getter
    public static final char tcpPackerSeparator = '\n';

    @Getter
    public static final int defaultClientListeningUDPPort = 4249;
    @Getter
    public static final int defaultServerUDPPort = 4250;
    @Getter
    public static final int defaultServerTCPPort = 4251;
    @Getter
    public static final String defaultServerIp = "localhost";
}
