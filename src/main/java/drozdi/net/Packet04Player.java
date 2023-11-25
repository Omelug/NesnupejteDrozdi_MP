package drozdi.net;

/*
public class Packet04Player extends Packet{



    public void writeDataUDP(Client client) {
        if (Objects.requireNonNull(playerPacketType) == PlayerPacketType.MOVE) {
            ClientPlayer player = client.getPanel().getPlayer();
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
                UDP.sendDataUDP(client.get ,buildPacket(data));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeDataUDP(ServerPlayer player) {
        switch (playerPacketType) {
            case NEXT_PLAYER, MOVE -> {
                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

                    objectStream.writeObject(player.getName());
                    objectStream.writeObject(player.getPosition());
                    objectStream.writeObject(player.getDirection());
                    //objectStream.writeObject(player.isOnGround());

                    objectStream.flush();
                    byte[] data = byteStream.toByteArray();
                    UDP.sendDataUDP(player.getIp(), player.getUDPPort(),buildPacket(data));

                    objectStream.close();
                    byteStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

   public void writeAllClientsUDP(Server server, ServerPlayer player) {
        for (Player playerMP : server.getHitBoxHelper().getMapHelper().getPlayerList()){
            if (player.getIp().equals(playerMP.getIp()) && player.getUDPPort() == playerMP.getUDPPort()) {
                playerPacketType = PlayerPacketType.MOVE;
            }else{
                GameServer.logger.TCPInfoSend("another player move");
                playerPacketType = PlayerPacketType.NEXT_PLAYER;
            }
            writeDataUDP(server, player);
        }
    }

}*/
