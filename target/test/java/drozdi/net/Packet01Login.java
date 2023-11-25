package drozdi.net;

import drozdi.levels.level3.server.GameServer;
import lombok.Getter;
import lombok.Setter;
import Panel;
import drozdi.levels.level3.client.GameClient;
import drozdi.levels.level3.client.PlayerMP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Packet01Login extends Packet {

    @Getter @Setter
    LoginPacketType loginPacketType;

    public Packet01Login(LoginPacketType loginPacketType) {
        super(01);
        this.loginPacketType = loginPacketType;
    }

    @Override
    public void writeDataTCP(GameServer server, Socket clientSocket) {
        switch (loginPacketType) {
            case DISCONNECT -> {
                server.sendDataTCP(buildPacket(null), clientSocket);
            }
        }
    }

    @Override
    public void writeDataTCP(GameClient client) {
        switch (loginPacketType) {
            case CONNECT -> {
                String loginMessage = GamePanel.getPlayer().getName() + ":" + GameClient.getListeningUDPPort();
                client.sendDataTCP(buildPacket(loginMessage.getBytes()));
            }
            case DISCONNECT -> {
                GameClient.getLogger().TCPInfoSend("Disconnect request");
                client.sendDataTCP(buildPacket(null));
            }
        }
    }

    @Override
    public void writeDataUDP(GameClient client) {}

    public void sendToPlayer(GameServer server, PlayerMP player, Socket clientSocket) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

            objectStream.writeObject(player.getPosition());
            objectStream.writeObject(player.getSize());

            objectStream.flush();
            byte[] data = byteStream.toByteArray();

            objectStream.close();
            byteStream.close();
            //System.out.println(new String(buildPacket(data)));
            server.sendDataTCP(buildPacket(data), clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public byte[] buildPacket(byte[] data) {
        if (data == null){
            data = "".getBytes();
        }
        byte[] packetData;
        String header = String.format("%02d%02d", super.packetId, loginPacketType.getLoginPackedId());
        byte[] headerBytes = header.getBytes();

        packetData = new byte[headerBytes.length + data.length];
        System.arraycopy(headerBytes, 0, packetData, 0, headerBytes.length);

        switch (loginPacketType){
            case CONNECT, DISCONNECT -> {
                System.arraycopy(data, 0, packetData, headerBytes.length, data.length);
            }
        }

        return packetData;
    }

    public enum LoginPacketType {
        INVALID(00),CONNECT(01),DISCONNECT(02),RECONNECT(03);

        @Getter @Setter
        private int loginPackedId;

        LoginPacketType(int loginPackedId) {
            this.loginPackedId = loginPackedId;
        }
    }

    public static LoginPacketType lookupMapPacket(int id){
        for (LoginPacketType loginPacketType : LoginPacketType.values()) {
            if (loginPacketType.getLoginPackedId() == id){
                return loginPacketType;
            }
        }
        return LoginPacketType.INVALID;
    }
}
