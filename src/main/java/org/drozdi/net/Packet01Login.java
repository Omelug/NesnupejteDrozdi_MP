package org.drozdi.net;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.server.GameServer;
import org.drozdi.levels.level3.client.GameClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.Set;

public class Packet01Login extends Packet {

    @Getter @Setter
    LoginPacketType loginPacketType;

    public Packet01Login(LoginPacketType loginPacketType) {
        super(01);
        this.loginPacketType = loginPacketType;
    }

    @Override
    public void writeData(GameClient client) {
        switch (loginPacketType) {
            case CONNECT -> {
                client.sendData(buildPacket(client.getPanelLevel3().getPlayer().getName().getBytes()));
            }
        }
    }
    @Override
    public void writeData(GameServer server, InetAddress clientAddress, int clientPort) {
    }

    public void sendToPlayer(GameServer server, PlayerMP player) {
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
            server.sendData(buildPacket(data), player.getIpAddress(), player.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public byte[] buildPacket(byte[] data) {
        byte[] packetData = new byte[0];
        String header = String.format("%02d%02d", super.packetId, loginPacketType.getLoginPackedId());
        byte[] headerBytes = header.getBytes();

        switch (loginPacketType){
            case CONNECT -> {
                packetData = new byte[headerBytes.length + data.length];
                System.arraycopy(headerBytes, 0, packetData, 0, headerBytes.length);
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
