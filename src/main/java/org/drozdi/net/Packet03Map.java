package org.drozdi.net;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level3.client.GameClient;
import org.drozdi.levels.level3.server.GameServer;

import java.net.InetAddress;

public class Packet03Map extends Packet{

    @Getter @Setter
    MapPacketType mapPacketType;

    public Packet03Map(MapPacketType mapPacketType) {
        super(03);
        this.mapPacketType = mapPacketType;
    }

    @Override
    public void writeData(GameClient client) {
        switch (mapPacketType) {
            case START -> {
                client.sendData(buildPacket("".getBytes()));
            }
        }
    }

    @Override
    public void writeData(GameServer server, InetAddress clientAddress, int clientPort) {
        switch (mapPacketType) {
            case START -> {
                server.sendData(buildPacket(null), clientAddress, clientPort);
            }
            default -> {
                System.out.println("INVALID mapPacketType");
            }
        }
    }
    public void writeData(GameServer server, InetAddress clientAddress, int clientPort, byte[] data) {
        switch (mapPacketType) {
            case DATA -> {
                server.sendData(buildPacket(data), clientAddress, clientPort);
            }
            default -> {
                System.out.println("INVALID mapPacketType");
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
        public String getMapPacketTypeString() {
            return String.valueOf(mapPacketId);
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
