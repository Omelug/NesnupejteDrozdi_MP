package drozdi.net;


/*
public class Packet01Login extends Packet {


    public void sendToPlayer(ServerPlayer player) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

            objectStream.writeObject(player.getPosition());
            objectStream.writeObject(player.getSize());

            objectStream.flush();
            byte[] data = byteStream.toByteArray();

            objectStream.close();
            byteStream.close();

            TCP.sendDataTCP(player.getTcpClientSocket(),buildPacket(data));
        } catch (IOException e) {
            TClog.error e.printStackTrace();
        }
    }



}*/
