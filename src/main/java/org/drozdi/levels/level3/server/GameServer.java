package org.drozdi.levels.level3.server;

import lombok.Data;
import lombok.Getter;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.net.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Data
public class GameServer extends Thread{
    private static DatagramSocket socketUDP;
    private ServerSocket socketTCP;
    private String mapPath;
    private HitBoxHelper hitBoxHelper;
    private int keys;
    @Getter
    private static ConsoleLogger logger = new ConsoleLogger();

    public GameServer(){
        mapPath = "/server_data/maps/map2.bmp";
        hitBoxHelper = new HitBoxHelper(this);

        try {
            socketUDP = new DatagramSocket(4250);
            socketTCP = new ServerSocket(4251);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private void sendMapToClient(Socket clientSocket) {
        byte[] mapData = readMapFile();

        System.out.println("All map: "+ new String(mapData));
        new Packet03Map(Packet03Map.MapPacketType.START).writeDataTCP(this, clientSocket);

        int chunkSize = NetSettings.getMapChunkSize();
        int totalChunks = (int) Math.ceil((double) mapData.length / chunkSize);

        for (int chunkNumber = 0; chunkNumber < totalChunks; chunkNumber++) {

            int offset = chunkNumber * chunkSize;
            int length = Math.min(chunkSize, mapData.length - offset);

            byte[] chunk = new byte[length];
            System.arraycopy(mapData, offset, chunk, 0, length);

            new Packet03Map(Packet03Map.MapPacketType.DATA).writeDataTCP(this, clientSocket, chunk);
        }
        Packet03Map stop = new Packet03Map(Packet03Map.MapPacketType.STOP);
        sendDataTCP(stop.buildPacket(null), clientSocket);
        //close socket for map
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateTick() {
        while (true){
            long startTime = System.currentTimeMillis();

            hitBoxHelper.update();
            hitBoxHelper.sendAllData();

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            long sleepDuration = Math.max(50 - elapsedTime, 0);// 20 TPS max
            ServerSeparated.serverWindow.setTPS(1000/(double) (elapsedTime+elapsedTime));

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
            receiveTCP();
            receiveUDP();
        }
    }

    private void receiveUDP() {
        byte[] data = new byte[NetSettings.getMapChunkSize()];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socketUDP.receive(packet);
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
            case PLAYER -> {
                Packet04Player.PlayerPacketType playerPacketType = Packet04Player.lookupPlayerPacket(Integer.parseInt(id.substring(2, 4)));
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
                            player.setRight((boolean) objectStream.readObject());
                            player.setDown((boolean) objectStream.readObject());
                            player.setLeft((boolean) objectStream.readObject());
                            player.setDown((boolean) objectStream.readObject());
                            player.setShooting((boolean) objectStream.readObject());

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

    private void receiveTCP() {
        Socket clientSocket;
        try {
            clientSocket = socketTCP.accept();
            InputStream inputStream = clientSocket.getInputStream();
            /*BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            message = in.readLine();
            System.out.println("Received from client: " + message);*/

            byte[] data = new byte[NetSettings.defaultPacketSize];
            int bytesRead;
            String receivedData = null;
            while ((bytesRead = inputStream.read(data)) != -1) {
                receivedData = new String(data,0, bytesRead);
            }
            System.out.println("RAW data: " + data);
            System.out.println("message: " + receivedData);


            String id = new String(data).substring(0,4);
            Packet.PacketType packetType = Packet.lookupPacket(Integer.parseInt(id.substring(0,2)));

            switch (packetType){
                case LOGIN -> {
                    Packet01Login.LoginPacketType loginPacketType = Packet01Login.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                    //System.out.println(new String(data));
                    switch (loginPacketType){
                        case CONNECT -> {
                            String name = new String(data).trim().substring(4);
                            if (hitBoxHelper.getMapHelper().playerConnected(hitBoxHelper, clientSocket.getInetAddress(), clientSocket.getPort())) {
                                hitBoxHelper.getMapHelper().removePlayer(clientSocket.getInetAddress(), clientSocket.getPort());
                            }
                            PlayerMP playerMP = new PlayerMP(name,clientSocket.getInetAddress(), clientSocket.getPort());
                            new Packet01Login(Packet01Login.LoginPacketType.CONNECT).sendToPlayer(this, playerMP, clientSocket);
                            hitBoxHelper.getMapHelper().addPlayer(playerMP);

                            logger.playerConnect(playerMP);

                            /*System.out.print("Players {");
                            for (PlayerMP player : getHitBoxHelper().getMapHelper().getPlayerList()) {
                                System.out.print(player.getName());
                            }
                            System.out.print("}");*/
                        }
                        case DISCONNECT -> {
                            String name = new String(data).trim().substring(4);
                            hitBoxHelper.getMapHelper().removePlayer(clientSocket.getInetAddress(), clientSocket.getPort());
                        }
                    }
                    clientSocket.close();
                }
                case MAP -> {
                    Packet03Map.MapPacketType mapPacketType = Packet03Map.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                    switch (mapPacketType){
                        case START -> {
                            sendMapToClient(clientSocket);
                        }
                        case STOP -> {
                            //TODO if player want stop downloading
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendDataUDP(byte[] data, InetAddress ipAddress, int port ){
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socketUDP.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendDataTCP(byte[] data, Socket clientSocket){
        try {
            OutputStream outStream = clientSocket.getOutputStream();
            PrintWriter out = new PrintWriter(outStream, true);
            out.write(new String(data, StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/**
     private void receiveTCP() {
     Socket clientSocket;
     String message;
     try {
     clientSocket = socketTCP.accept();
     System.out.println("Accepted connection from " + clientSocket.getInetAddress());
     InputStream inStream = clientSocket.getInputStream();
     BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
     message = in.readLine();
     System.out.println("Received from client: " + message);
     clientSocket.close();
     } catch (IOException e) {
     throw new RuntimeException(e);
     }
 Packet.PacketType packetType = Packet.lookupPacket(Integer.parseInt(message.substring(0,2)));

 switch (packetType){
 case LOGIN -> {
 Packet01Login.LoginPacketType loginPacketType = Packet01Login.lookupMapPacket(Integer.parseInt(message.substring(2, 4)));
 //System.out.println(new String(data));
 /*switch (loginPacketType){
 case CONNECT -> {
 String name = message.trim().substring(4);
 if (hitBoxHelper.getMapHelper().playerConnected(hitBoxHelper, packet.getAddress(), packet.getPort())) {
 hitBoxHelper.getMapHelper().removePlayer(packet.getAddress(), packet.getPort());
 }
 PlayerMP playerMP = new PlayerMP(name, packet.getAddress(), packet.getPort());
 new Packet01Login(Packet01Login.LoginPacketType.CONNECT).sendToPlayer(this, playerMP);
 hitBoxHelper.getMapHelper().addPlayer(playerMP);

 System.out.print("Players {");
 for (PlayerMP player : getHitBoxHelper().getMapHelper().getPlayerList()) {
 System.out.print(player.getName());
 }
 System.out.print("}");
 }
 case DISCONNECT -> {
 String name = new String(receivedData).trim().substring(4);
 System.out.println(name + " disconnected");
 hitBoxHelper.getMapHelper().removePlayer(packet.getAddress(), packet.getPort());
 }
 }
            }
                    }

                    }

private void receiveUDP() {
        byte[] data = new byte[NetSettings.getMapChunkSize()];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
        socketUDP.receive(packet);
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
        if (hitBoxHelper.getMapHelper().playerConnected(hitBoxHelper, packet.getAddress(), packet.getPort())) {
        hitBoxHelper.getMapHelper().removePlayer(packet.getAddress(), packet.getPort());
        }
        PlayerMP playerMP = new PlayerMP(name, packet.getAddress(), packet.getPort());
        new Packet01Login(Packet01Login.LoginPacketType.CONNECT).sendToPlayer(this, playerMP);
        hitBoxHelper.getMapHelper().addPlayer(playerMP);

        System.out.print("Players {");
        for (PlayerMP player : getHitBoxHelper().getMapHelper().getPlayerList()) {
        System.out.print(player.getName());
        }
        System.out.print("}");
        }
        case DISCONNECT -> {
        String name = new String(receivedData).trim().substring(4);
        System.out.println(name + " disconnected");
        hitBoxHelper.getMapHelper().removePlayer(packet.getAddress(), packet.getPort());
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
        Packet04Player.PlayerPacketType playerPacketType = Packet04Player.lookupPlayerPacket(Integer.parseInt(id.substring(2, 4)));
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
        player.setRight((boolean) objectStream.readObject());
        player.setDown((boolean) objectStream.readObject());
        player.setLeft((boolean) objectStream.readObject());
        player.setDown((boolean) objectStream.readObject());
        player.setShooting((boolean) objectStream.readObject());

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

public void sendDataUDP(byte[] data, InetAddress ipAddress, int port ){
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
        socketUDP.send(packet);
        } catch (IOException e) {
        e.printStackTrace();
        }
        }
public void sendDataTCP(byte[] data, InetAddress ipAddress, int port ){
        Socket socket = new Socket(ipAddress, port);
        OutputStream outStream = socket.getOutputStream();
        PrintWriter out = new PrintWriter(outStream, true);

        out.write(Arrays.toString(data));
        out.flush();

        socket.close();
        }
 **/