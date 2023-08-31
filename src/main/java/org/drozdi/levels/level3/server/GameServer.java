package org.drozdi.levels.level3.server;

import lombok.Data;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.net.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@Data
public class GameServer extends Thread{
    private static DatagramSocket socket;
    private String mapPath;
    private HitBoxHelper hitBoxHelper;
    private int keys;

    public GameServer(){
        mapPath = "/server_data/maps/map2.bmp";
        hitBoxHelper = new HitBoxHelper(this);
        try {
            socket = new DatagramSocket(4250);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private byte[] readMapFile() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(mapPath);
            if (inputStream != null) {
                return inputStream.readAllBytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendMapToClient(InetAddress clientAddress, int clientPort) {
        byte[] mapData = readMapFile();

        //System.out.println("All map: "+ new String(mapData));
        new Packet03Map(Packet03Map.MapPacketType.START).writeData(this, clientAddress, clientPort);

        int chunkSize = NetSettings.getMapChunkSize();
        int totalChunks = (int) Math.ceil((double) mapData.length / chunkSize);

        for (int chunkNumber = 0; chunkNumber < totalChunks; chunkNumber++) {

            int offset = chunkNumber * chunkSize;
            int length = Math.min(chunkSize, mapData.length - offset);

            byte[] chunk = new byte[length];
            System.arraycopy(mapData, offset, chunk, 0, length);

            new Packet03Map(Packet03Map.MapPacketType.DATA).writeData(this, clientAddress, clientPort, chunk);

        }
        Packet03Map stop = new Packet03Map(Packet03Map.MapPacketType.STOP);
        sendData(stop.buildPacket(null), clientAddress, clientPort);
    }

    private void updateTick() {
        while (true){
            long startTime = System.currentTimeMillis();

            hitBoxHelper.update();
            hitBoxHelper.sendAllData();

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            long sleepDuration = Math.max(50 - elapsedTime, 0); // 2 TPS max

            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                e.printStackTrace(); 
            }
        }
    }

    @Override
    public void run() {
        System.out.println("server is up");

        Thread updateThread = new Thread(this::updateTick);
        updateThread.start();


        while (true){

            byte[] data = new byte[NetSettings.getMapChunkSize()];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            byte[] receivedData = packet.getData();
            String id = new String(receivedData).substring(0,4);

            Packet.PacketType packetType = Packet.lookupPacket(Integer.parseInt(id.substring(0,2)));


            switch (packetType){
                case PING -> {
                    //sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
                    System.out.println("ping from " +packet.getAddress()+";"+ packet.getPort());
                }
                case LOGIN -> {
                    Packet01Login.LoginPacketType loginPacketType = Packet01Login.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                    //System.out.println(new String(data));
                    switch (loginPacketType){
                        case CONNECT -> {
                            String name = new String(receivedData).trim().substring(4);

                            for (PlayerMP player : hitBoxHelper.getMapHelper().getPlayerList()) {
                                if (player.getName().equals(name)) {
                                    System.out.println("Player " + player.getName() + " is already connected");
                                }
                            }
                            PlayerMP playerMP = new PlayerMP(name, packet.getAddress(), packet.getPort());
                            new Packet01Login(Packet01Login.LoginPacketType.CONNECT).sendToPlayer(this, playerMP);
                            hitBoxHelper.getMapHelper().addPlayer(playerMP);

                            System.out.print("Players {");
                            for (PlayerMP player : getHitBoxHelper().getMapHelper().getPlayerList()) {
                                System.out.print("" + player.getName());
                            }
                            System.out.print("}");
                           }
                        case DISCONNECT -> {
                            //TODO diconnect
                        }
                    }
                }
                case MAP -> {
                    Packet03Map.MapPacketType mapPacketType = Packet03Map.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                    switch (mapPacketType){
                        case START -> {
                            sendMapToClient(packet.getAddress(), packet.getPort());
                        }
                        case STOP -> {
                            //TODO if player want stop downloading
                        }
                    }
                }
                case PLAYER -> {
                    Packet04Player.PlayerPacketType playerPacketType = Packet04Player.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                    switch (playerPacketType){
                        case MOVE -> {
                            try {
                                int idLengthInBytes = id.getBytes().length;
                                int restOfDataLength = receivedData.length - id.getBytes().length;

                                byte[] restOfData = new byte[restOfDataLength];
                                System.arraycopy(receivedData, idLengthInBytes, restOfData, 0, restOfDataLength);

                                ByteArrayInputStream byteStream = new ByteArrayInputStream(restOfData);
                                ObjectInputStream objectStream = new ObjectInputStream(byteStream);

                                PlayerMP player = hitBoxHelper.getPlayerByIpAndPort(packet.getAddress(),packet.getPort());

                                if (player == null){
                                    break;
                                }
                                player.setUp((boolean) objectStream.readObject());
                                player.setLeft((boolean) objectStream.readObject());
                                player.setDown((boolean) objectStream.readObject());
                                player.setRight((boolean) objectStream.readObject());

                                objectStream.close();
                                byteStream.close();

                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
    public void sendData(byte[] data, InetAddress ipAddress, int port ){
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
