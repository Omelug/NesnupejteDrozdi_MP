package org.drozdi.levels.level3.client;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level0.Level0;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.net.*;

import java.awt.geom.Point2D;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GameClient extends Thread {

    @Getter @Setter
    private InetAddress ipAddress;
    @Getter @Setter
    private int portUDP;
    @Getter @Setter
    private int portTCP;
    @Getter @Setter
    private Panel_level3 panelLevel3;
    @Getter @Setter
    private DatagramSocket socketUDP;
    @Getter @Setter
    private ServerSocket socketTCP;
    int chunkSize = NetSettings.getMapChunkSize();

    boolean receivingMapData;
    ByteArrayOutputStream outputStream;

    public GameClient(String ipAddress, int portUDP, int portTCP){
        this.portUDP = portUDP;
        this.portTCP = portTCP;
        try {
            this.socketUDP = new DatagramSocket();
            this.socketTCP = new ServerSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
            receivingMapData = false;
            outputStream = null;
            receiveUDP();
            receiveTCP();
        }

    private void receiveTCP() {
        Socket clientSocket;
        try {
            clientSocket = socketTCP.accept();

            InputStream inStream = clientSocket.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream receivedDataBuffer = new ByteArrayOutputStream();
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                receivedDataBuffer.write(buffer, 0, bytesRead);
            }
            byte[] receivedData = receivedDataBuffer.toByteArray();

            String id = new String(receivedData, 0, 4, StandardCharsets.UTF_8);
            System.out.println("Received ID: " + id);
            Packet.PacketType packetType = Packet.lookupPacket(Integer.parseInt(id.substring(0,2)));

            switch (packetType){
                case LOGIN -> {
                    Packet01Login.LoginPacketType packet01Login = Packet01Login.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                    switch (packet01Login){
                        case CONNECT -> {
                            try {
                                int idLengthInBytes = id.getBytes().length;
                                int restOfDataLength = receivedData.length - idLengthInBytes;

                                byte[] restOfData = new byte[restOfDataLength];
                                System.arraycopy(receivedData, idLengthInBytes, restOfData, 0, restOfDataLength);

                                ByteArrayInputStream byteStream = new ByteArrayInputStream(restOfData);
                                ObjectInputStream objectStream = new ObjectInputStream(byteStream);

                                Point2D.Double position = (Point2D.Double) objectStream.readObject();
                                Point2D.Double size = (Point2D.Double) objectStream.readObject();

                                getPanelLevel3().getPlayer().setPosition(position);
                                getPanelLevel3().getPlayer().setSize(size);

                                getPanelLevel3().getMapHelper().getPlayerList().add(getPanelLevel3().getPlayer());

                                System.out.print("Players {");
                                for (PlayerMP player : getPanelLevel3().getMapHelper().getPlayerList()) {
                                    System.out.print("" + player.getName());
                                }
                                System.out.print("}");
                                objectStream.close();
                                byteStream.close();

                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                case MAP -> {
                    Packet03Map.MapPacketType mapPacketType = Packet03Map.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                    switch (mapPacketType){
                        case INVALID -> {
                            System.out.println("INVALID map request");
                        }
                        case START -> {
                            outputStream = new ByteArrayOutputStream();
                            receivingMapData = true;
                        }
                        case DATA -> {
                            if (receivingMapData) {
                                int idLengthInBytes = id.getBytes().length;
                                int restOfDataLength = receivedData.length - idLengthInBytes;

                                byte[] restOfData = new byte[restOfDataLength];
                                System.arraycopy(receivedData, idLengthInBytes, restOfData, 0, restOfDataLength);

                                //outputStream.write(restOfData, packet.getOffset(), restOfData.length);
                                //TODO doufam, ze packet.getOffset() je v tomto pripade 0
                                outputStream.write(restOfData, 0, restOfData.length);
                            }
                        }
                        case STOP -> {
                            receivingMapData = false;
                            byte[] mapData = outputStream.toByteArray();
                            System.out.println(" -> Map downloaded");

                            File outputFile = new File("src/main/resources/client_data/maps/server_map.bmp");
                            System.out.println( outputFile.exists()+ " " + outputFile.getAbsolutePath());
                            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                                fileOutputStream.write(mapData);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveUDP() {
        byte[] data = new byte[chunkSize +  NetSettings.getPacketHeaderSize()];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socketUDP.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] receivedData = packet.getData();
        String message = new String(receivedData).trim();
        String id = new String(receivedData).substring(0,4);

        Packet.PacketType packetType = Packet.lookupPacket(Integer.parseInt(id.substring(0,2)));

        switch (packetType){
            case INVALID -> {
                System.out.println("INVALID request");
            }
            case PLAYER -> {
                Packet04Player.PlayerPacketType playerPacketType = Packet04Player.lookupPlayerPacket(Integer.parseInt(id.substring(2, 4)));
                //System.out.println(new String(data));
                switch (playerPacketType){
                    case MOVE -> {
                        try {
                            int idLengthInBytes = id.getBytes().length;
                            int restOfDataLength = receivedData.length - id.getBytes().length;

                            byte[] restOfData = new byte[restOfDataLength];
                            System.arraycopy(receivedData, idLengthInBytes, restOfData, 0, restOfDataLength);

                            ByteArrayInputStream byteStream = new ByteArrayInputStream(restOfData);
                            ObjectInputStream objectStream = new ObjectInputStream(byteStream);

                            PlayerMP player = getPanelLevel3().getPlayer();

                            //System.out.println(new String(restOfData));
                            if (player != null){
                                player.setName((String) objectStream.readObject());
                                player.setPosition((Point2D.Double) objectStream.readObject());
                                player.setDirection((Direction) objectStream.readObject());
                                player.setOnGround((boolean) objectStream.readObject());

                                objectStream.close();
                                byteStream.close();
                            }else{
                                System.out.print(" player is null");

                            }

                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    case NEXT_PLAYER -> {
                        try {
                            int idLengthInBytes = id.getBytes().length;
                            int restOfDataLength = receivedData.length - id.getBytes().length;

                            byte[] restOfData = new byte[restOfDataLength];
                            System.arraycopy(receivedData, idLengthInBytes, restOfData, 0, restOfDataLength);

                            ByteArrayInputStream byteStream = new ByteArrayInputStream(restOfData);
                            ObjectInputStream objectStream = new ObjectInputStream(byteStream);

                            String name = (String) objectStream.readObject();
                            PlayerMP player = getPanelLevel3().getMapHelper().getPlayerByName(name);

                            //System.out.println(new String(restOfData));
                            if (player != null){
                                player.setName(name);
                                player.setPosition((Point2D.Double) objectStream.readObject());
                                player.setDirection((Direction) objectStream.readObject());
                                player.setOnGround((boolean) objectStream.readObject());

                                objectStream.close();
                                byteStream.close();
                            }else{
                                System.out.print(" player is null");

                            }

                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (message.equals("pong")) {
            Level0.changeConnectionStatus();
        }
    }

    public void getMap(){
        new Packet03Map(Packet03Map.MapPacketType.START).writeDataTCP(this);
    }

    public void sendDataUDP(byte[] data){
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, portUDP);
        try {
            socketUDP.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendDataTCP(byte[] data){
        Socket clientSocket;
        try {
            clientSocket = new Socket(ipAddress, portTCP);
            OutputStream outStream = clientSocket.getOutputStream();
            PrintWriter out = new PrintWriter(outStream, true);

            out.write(new String(data, StandardCharsets.UTF_8));
            System.out.println(new String(data, StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login() {//TODO tohle pak zmenit
        new Packet01Login(Packet01Login.LoginPacketType.CONNECT).writeDataTCP(this);
    }
    public void disconnect() {//TODO tohle pak zmenit
        new Packet01Login(Packet01Login.LoginPacketType.DISCONNECT).writeDataTCP(this);
    }

    public void move() {
        new Packet04Player(Packet04Player.PlayerPacketType.MOVE).writeDataUDP(this);
    }
    public void dead(){
        getPanelLevel3().updateInfo();
    }
}
