package drozdi.net;

import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;
import drozdi.net.connection.TCP;
import drozdi.net.connection.UDP;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class Packet {
    static private final ConsoleLogger log = new ConsoleLogger(ConsoleColors.BLACK_UNDERLINED);

    @Getter
    FirstPacketID firstId;
    @Getter
    SecondPacketID secondId;
    @Getter
    private byte[] data;

    public Packet(byte[] receivedData) throws IOException {
        if (receivedData.length >= NetConst.sizeID) {

            this.firstId = FirstPacketID.valueOf(getIntId(receivedData[0], receivedData[1]));
            this.secondId = SecondPacketID.valueOf(getIntId(receivedData[2], receivedData[3]));

            int restOfDataLength = receivedData.length - NetConst.getSizeID();
            this.data = new byte[restOfDataLength];
            System.arraycopy(receivedData, NetConst.getSizeID(), data, 0, restOfDataLength);

        }else {
            log.error("Packet shorter than expected");
            throw new IOException("Packet shorter than expected");
        }
    }

    int getIntId(byte one, byte two){
        return (Character.getNumericValue(one)*10) + Character.getNumericValue(two);
    }

    public Packet(FirstPacketID firstPacketID, SecondPacketID secondPacketID) {
        this.firstId = firstPacketID;
        this.secondId = secondPacketID;
    }
    public Packet(FirstPacketID firstPacketID, SecondPacketID secondPacketID, byte[] data) {
        this(firstPacketID, secondPacketID);
        this.data = data;
    }

    public void sendTCP(Socket clientSocket){
        TCP.sendData(clientSocket, buildPacket(getData()));
    }
    public void sendUDP(DatagramSocket senderSocket, InetAddress serverAddress, int serverPort){ //TODO dat tyhle 3 veci do tridy?
        UDP.sendData(senderSocket,serverAddress, serverPort, buildPacket(getData()));
    }

    public byte[] buildPacket(byte[] data) {
        if (data == null){
            data = "".getBytes();
        }
        byte[] packetData;

        String header = String.format("%02d%02d", firstId.getId(),secondId.getId());
        byte[] headerBytes = header.getBytes();

        packetData = new byte[headerBytes.length + data.length];
        System.arraycopy(headerBytes, 0, packetData, 0, headerBytes.length);
        System.arraycopy(data, 0, packetData, headerBytes.length, data.length);


        return packetData;
    }


    /*public void writeDataList(List<ServerPlayer> list) {
        for(ServerPlayer player : list){
            writeDataTCP(player);
        }
    }*/

    @Override
    public String toString(){
        return String.format("%s%s[%s]",firstId, secondId, data);
    }

    @Getter @RequiredArgsConstructor
    public enum FirstPacketID {
        INVALID(-1),PING(0),LOGIN(1),MAP(2), PLAYER(3),CHAT(4);
        private final int firstId;

        public static FirstPacketID valueOf(int packetId) {
            for (FirstPacketID enumValue : values()) {
                if (enumValue.firstId == packetId) {
                    return enumValue;
                }
            }
            return INVALID;
        }
        public int getId(){
            return firstId;
        }
    }
    @Getter @RequiredArgsConstructor
    public enum SecondPacketID {
        INVALID(-1),NO(0),CONNECT(1),DISCONNECT(2),RECONNECT(3),START(4),DATA(5),STOP(6),MOVE(7), POSITION(8), NAME(9);
        private final int secondId;

        public static SecondPacketID valueOf(int secondId) {
            for (SecondPacketID enumValue : values()) {
                if (enumValue.secondId == secondId) {
                    return enumValue;
                }
            }
            return INVALID;
        }
        public int getId(){
            return secondId;
        }
    }
    public static boolean isValid(FirstPacketID firstPacketID,SecondPacketID secondPacketID) {
        Set<PacketConnection> conn = new HashSet<>();
        conn.add(new PacketConnection(FirstPacketID.PING, SecondPacketID.NO));
        conn.add(new PacketConnection(FirstPacketID.LOGIN, SecondPacketID.CONNECT));
        conn.add(new PacketConnection(FirstPacketID.LOGIN, SecondPacketID.DISCONNECT));
        conn.add(new PacketConnection(FirstPacketID.LOGIN, SecondPacketID.NAME));
        conn.add(new PacketConnection(FirstPacketID.LOGIN, SecondPacketID.RECONNECT));
        conn.add(new PacketConnection(FirstPacketID.MAP, SecondPacketID.START));
        conn.add(new PacketConnection(FirstPacketID.MAP, SecondPacketID.DATA));
        conn.add(new PacketConnection(FirstPacketID.MAP, SecondPacketID.STOP));
        conn.add(new PacketConnection(FirstPacketID.PLAYER, SecondPacketID.MOVE));
        conn.add(new PacketConnection(FirstPacketID.PLAYER, SecondPacketID.POSITION));

        PacketConnection InputConn = new PacketConnection(firstPacketID, secondPacketID);
        for (PacketConnection connection : conn) {
            if ((connection.secondPacketID == InputConn.secondPacketID) && (connection.firstPacketID == InputConn.firstPacketID)) {
                return true;
            }
        }
        return false;
    }

    @RequiredArgsConstructor
    public static class PacketConnection{
        final FirstPacketID firstPacketID;
        final SecondPacketID secondPacketID;
    }

}
