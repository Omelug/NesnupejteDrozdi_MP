package org.drozdi.net;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level3.client.GameClient;
import org.drozdi.levels.level3.server.GameServer;
import org.drozdi.levels.level3.client.PlayerMP;

import java.net.InetAddress;
import java.util.List;

public abstract class Packet {
    public static enum PacketType {
        INVALID(-1),PING(00),LOGIN(01), CHAT(02),MAP(03), PLAYER(04);

        @Getter @Setter
        private int packetId;

        private PacketType(int packetId) {
            this.packetId = packetId;
        };
    }
    public byte  packetId;
    public Packet(int packetId) {
        this.packetId = (byte) packetId;
    }
    public abstract void writeData(GameClient client);
    public abstract void writeData(GameServer server, InetAddress clientAddress, int clientPort);
    public void writeData(GameServer server, PlayerMP playerMP){
        writeData(server, playerMP.getIpAddress(), playerMP.getPort());
    }
    public void writeDataList(GameServer server, List<PlayerMP> list) {
        for(PlayerMP playerMP : list){
            writeData(server, playerMP);
        }
    }

    public static String readDataFirst(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2);
    }
    public static String readIdSecond(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2,4);
    }

    public static PacketType lookupPacket(int id){
        for (PacketType packetType : PacketType.values()) {
            if (packetType.getPacketId() == id){
                return packetType;
            }
        }
        return PacketType.INVALID;
    }
}
