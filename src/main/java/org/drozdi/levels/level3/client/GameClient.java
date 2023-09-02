package org.drozdi.levels.level3.client;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level0.Level0;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.net.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.net.*;

public class GameClient extends Thread {

    @Getter @Setter
    private InetAddress ipAddress;
    @Getter @Setter
    private int port;
    @Getter @Setter
    private Panel_level3 panelLevel3;
    private DatagramSocket socket;
    int chunkSize = NetSettings.getMapChunkSize();

    public GameClient(String ipAddress, int port){
        this.port = port;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean receivingMapData = false;
        ByteArrayOutputStream outputStream = null;

        while (true) {
            byte[] data = new byte[chunkSize +  NetSettings.getPacketHeaderSize()];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
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

                               outputStream.write(restOfData, packet.getOffset(), restOfData.length);
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
                case PLAYER -> {
                    Packet04Player.PlayerPacketType playerPacketType = Packet04Player.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                    //System.out.println(new String(data));
                    switch (playerPacketType){
                        case MOVE -> {
                            // ok, tohle asi k nicemu neni
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
    }
    public void getMap(){
        new Packet03Map(Packet03Map.MapPacketType.START).writeData(this);
    }

    public void sendData(byte[] data){
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void login() {
        new Packet01Login(Packet01Login.LoginPacketType.CONNECT).writeData(this);
    }

    public void move() {
        new Packet04Player(Packet04Player.PlayerPacketType.MOVE).writeData(this);
    }
    public void dead(){
        getPanelLevel3().updateInfo();
    }
}
