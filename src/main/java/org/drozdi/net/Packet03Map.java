package org.drozdi.net;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level3.client.GameClient;
import org.drozdi.levels.level3.server.GameServer;
import java.net.Socket;

public class Packet03Map extends Packet{

    @Getter @Setter
    MapPacketType mapPacketType;

    public Packet03Map(MapPacketType mapPacketType) {
        super(03);
        this.mapPacketType = mapPacketType;
    }

    public void writeDataTCP(GameClient client) {
        switch (mapPacketType) {
            case START -> {
                GameClient.getLogger().TCPInfoSend("getMap START");
                client.sendDataTCP(buildPacket(null));
            }
        }
    }

    @Override
    public void writeDataUDP(GameClient client) {}

    @Override
    public void writeDataTCP(GameServer server,Socket clientSocket) {
        switch (mapPacketType) {
            case START, STOP -> {
                server.sendDataTCP(buildPacket(null), clientSocket);
            }
            default -> {
                System.out.println("INVALID mapPacketType");
            }
        }
    }
    public void writeDataTCP(GameServer server,Socket clientSocket, byte[] data) {
        switch (mapPacketType) {
            case DATA -> {
                server.sendDataTCP(buildPacket(data), clientSocket);
            }
        }
    }

    public byte[] buildPacket(byte[] data) {
        byte[] packetData = new byte[0];
        String header = String.format("%02d%02d", super.packetId, mapPacketType.getMapPacketId());
        byte[] headerBytes = header.getBytes();

        switch (mapPacketType){
            case START, STOP -> {
                packetData = new byte[headerBytes.length];
                System.arraycopy(headerBytes, 0, packetData, 0, headerBytes.length);
            }
            case DATA -> {
                packetData = new byte[headerBytes.length + data.length];
                System.arraycopy(headerBytes, 0, packetData, 0, headerBytes.length);
                System.arraycopy(data, 0, packetData, headerBytes.length, data.length);
            }
        }

        return packetData;
    }

    public enum MapPacketType {
        INVALID(00),START(01),DATA(02),STOP(03);

        @Getter @Setter
        private int mapPacketId;

        MapPacketType(int mapPacketId) {
            this.mapPacketId = mapPacketId;
        };
        private int getMapPacketType() {
            return mapPacketId;
        };

    }

    public static MapPacketType lookupMapPacket(int id){
        for (MapPacketType mapPacketType1 : MapPacketType.values()) {
            if (mapPacketType1.getMapPacketType() == id){
                return mapPacketType1;
            }
        }
        return MapPacketType.INVALID;
    }
}
