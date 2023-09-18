package org.drozdi.net;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level3.client.GameClient;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.server.GameServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Packet04Player extends Packet{

    @Getter @Setter
    PlayerPacketType playerPacketType;

    public Packet04Player(PlayerPacketType playerPacketType) {
        super(04);
        this.playerPacketType = playerPacketType;
    }

    @Override
    public void writeDataTCP(GameServer server, Socket clientSocket) {
    }

    @Override
    public void writeDataTCP(GameClient client) {
    }

    @Override
    public void writeDataUDP(GameClient client) {
        switch (playerPacketType) {
            case MOVE -> {
                PlayerMP player = client.getPanelLevel3().getPlayer();
                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

                    objectStream.writeObject(player.isUp());
                    objectStream.writeObject(player.isRight());
                    objectStream.writeObject(player.isDown());
                    objectStream.writeObject(player.isLeft());
                    objectStream.writeObject(player.isDown());
                    objectStream.writeObject(player.isShooting());

                    objectStream.flush();
                    byte[] data = byteStream.toByteArray();

                    objectStream.close();
                    byteStream.close();
                    client.sendDataUDP(buildPacket(data));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void writeData(GameServer server, InetAddress clientAddress, int clientPort) {
    }

    public void writeData(GameServer server, InetAddress clientAddress, int clientPort, PlayerMP player) {
        switch (playerPacketType) {
            case NEXT_PLAYER, MOVE -> {
                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

                    objectStream.writeObject(player.getName());
                    objectStream.writeObject(player.getPosition());
                    objectStream.writeObject(player.getDirection());
                    objectStream.writeObject(player.isOnGround());

                    objectStream.flush();
                    byte[] data = byteStream.toByteArray();
                    server.sendDataUDP(buildPacket(data), clientAddress, clientPort);

                    objectStream.close();
                    byteStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeAllClients(GameServer server, PlayerMP player) {
        for (PlayerMP playerMP : server.getHitBoxHelper().getMapHelper().getPlayerList()){
            if (player.getIpAddress().equals(playerMP.getIpAddress()) && player.getPort() == playerMP.getPort()) {
                playerPacketType = PlayerPacketType.MOVE;
            }else{
                playerPacketType = PlayerPacketType.NEXT_PLAYER;
            }
            writeData(server, playerMP.getIpAddress(), playerMP.getPort(), player);
        }
    }

    public byte[] buildPacket(byte[] data) {
        byte[] packetData;
        String header = String.format("%02d%02d", super.packetId, playerPacketType.getPlayerPacketId());
        byte[] headerBytes = header.getBytes();

        packetData = new byte[headerBytes.length + data.length];
        System.arraycopy(headerBytes, 0, packetData, 0, headerBytes.length);
        System.arraycopy(data, 0, packetData, headerBytes.length, data.length);

        return packetData;
    }

    public enum PlayerPacketType {
        INVALID(00),MOVE(01), NEXT_PLAYER(02);

        @Getter @Setter
        private int playerPacketId;

        PlayerPacketType(int playerPacketId) {
            this.playerPacketId = playerPacketId;
        };

        private int getPlayerPacketType() {
            return playerPacketId;
        };

    }

    public static PlayerPacketType lookupPlayerPacket(int id){
        for (PlayerPacketType playerPacketType : PlayerPacketType.values()) {
            if (playerPacketType.getPlayerPacketType() == id){
                return playerPacketType;
            }
        }
        return PlayerPacketType.INVALID;
    }
}
