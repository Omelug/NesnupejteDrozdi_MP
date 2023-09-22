package org.drozdi.net;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level3.client.GameClient;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.server.GameServer;

import java.net.Socket;
import java.util.List;

public abstract class Packet {
    public enum PacketType {
        INVALID(-1),PING(00),LOGIN(01), CHAT(02),MAP(03), PLAYER(04);

        @Getter
        private final int packetId;

        PacketType(int packetId) {
            this.packetId = packetId;
        };
    }
    public byte  packetId;
    public Packet(int packetId) {
        this.packetId = (byte) packetId;
    }

    public abstract void writeDataTCP(GameClient client);
    public abstract void writeDataUDP(GameClient client);
    public abstract void writeDataTCP(GameServer server, Socket clientSocket);
    public void writeDataTCP(GameServer server, PlayerMP playerMP){
        writeDataTCP(server, playerMP.getClientSocket());
    }
    public void writeDataList(GameServer server, List<PlayerMP> list) {
        for(PlayerMP playerMP : list){
            writeDataTCP(server, playerMP);
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
