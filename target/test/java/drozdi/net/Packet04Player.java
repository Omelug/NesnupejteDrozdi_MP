package drozdi.net;

import drozdi.levels.level3.server.GameServer;
import lombok.Getter;
import lombok.Setter;
import drozdi.levels.level3.client.GameClient;
import drozdi.levels.level3.client.PlayerMP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

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
        if (Objects.requireNonNull(playerPacketType) == PlayerPacketType.MOVE) {
            PlayerMP player = GameClient.getPlayer();
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
                e.printStackTrace();
            }
        }
    }

    public void writeDataUDP(GameServer server, PlayerMP player) {
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
                    server.sendDataUDP(buildPacket(data), player.getIp(), player.getUDPPort());

                    objectStream.close();
                    byteStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeAllClientsUDP(GameServer server, PlayerMP player) {
        for (PlayerMP playerMP : server.getHitBoxHelper().getMapHelper().getPlayerList()){
            if (player.getIp().equals(playerMP.getIp()) && player.getUDPPort() == playerMP.getUDPPort()) {
                playerPacketType = PlayerPacketType.MOVE;
            }else{
                GameServer.logger.TCPInfoSend("another player move");
                playerPacketType = PlayerPacketType.NEXT_PLAYER;
            }
            writeDataUDP(server, player);
        }
    }

    public byte[] buildPacket(byte[] data) {
        byte[] packetData;
        String header = String.format("%02d%02d", super.packetId, playerPacketType.getPlayerPacketId());
        byte[] headerBytes = header.getBytes();
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
        if (Objects.requireNonNull(playerPacketType) == PlayerPacketType.MOVE) {
            PlayerMP player = GameClient.getPlayer();
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
                e.printStackTrace();
            }
        }
    }

    public void writeDataUDP(GameServer server, PlayerMP player) {
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
                    server.sendDataUDP(buildPacket(data), player.getIp(), player.getUDPPort());

                    objectStream.close();
                    byteStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeAllClientsUDP(GameServer server, PlayerMP player) {
        for (PlayerMP playerMP : server.getHitBoxHelper().getMapHelper().getPlayerList()){
            if (player.getIp().equals(playerMP.getIp()) && player.getUDPPort() == playerMP.getUDPPort()) {
                playerPacketType = PlayerPacketType.MOVE;
            }else{
                GameServer.logger.TCPInfoSend("another player move");
                playerPacketType = PlayerPacketType.NEXT_PLAYER;
            }
            writeDataUDP(server, player);
        }
    }

    public byte[] buildPacket(byte[] data) {
        byte[] packetData;
        String header = String.format("%02d%02d", super.packetId, playerPacketType.getPlayerPacketId());
        byte[] headerBytes = header.getBytes();

        packetData
        packetData = new byte[headerBytes.length + data.length];
        System.arraycopy(headerBytes, 0, packetData, 0, headerBytes.length);
        System.arraycopy(data, 0, packetData, headerBytes.length, data.length);

        return packetData;
    }

    public enum PlayerPacketType {
        INVALID(00),MOVE(01), NEXT_PLAYER(02);
        @Getter
        private final int playerPacketId;
        PlayerPacketType(int playerPacketId) {
            this.playerPacketId = playerPacketId;
        }

        private int getPlayerPacketType() {
            return playerPacketId;
        }
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
