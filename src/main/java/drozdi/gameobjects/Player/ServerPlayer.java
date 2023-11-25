package drozdi.gameobjects.Player;


import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;
import drozdi.gameobjects.GameCubeFloat;
import drozdi.gameobjects.walls.*;
import drozdi.map.Maper;
import drozdi.map.ServerMaper;
import drozdi.net.Packet;
import drozdi.net.connection.TCPReceiver;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ServerPlayer extends Player implements TCPReceiver{
  static private final ConsoleLogger log = new ConsoleLogger(ConsoleColors.YELLOW_UNDERLINED);

  //UDP
  private static DatagramSocket serverSocket;

  static {
    try {
      serverSocket = new DatagramSocket();
    } catch (SocketException e) {
      log.terror("Failed UDP socket creation " + e.getMessage());
    }
  }

  //movement
  private long lastUpdated;
  @Getter
  private Point2D.Float speed = new Point2D.Float(0, 0);
  @Getter
  private boolean onGround;
  public static final Point2D.Float PLAYER_ACCELERATION = new Point2D.Float(0.2F,2.5F);
  public static final int MAX_SPEED_GROUND = 90;
  public static final Point2D.Float MAX_SPEED_AIR = new Point2D.Float(70,70);
  public static final Point2D.Float FRICTION = new Point2D.Float(7,0.1F);

  public static final float PLAYER_JUMP_CONST = 0.135F;
  public static final float MIN_SPEED = 1F;
  public static final int MAX_NAME = 13;
  public static final double ADHESION = 0.001D;

  private Thread tcpStart;
  private final ServerMaper maper;
  @Getter
  private int udpPort;
  @Getter
  private Point2D.Float maxSpeed;

  //TCP
  @Getter
  private final Socket tcpClientSocket;
  @Getter
  private boolean runningTCP = true;
  public ServerPlayer(String name, Socket tcpClientSocket, ServerMaper maper) {
    super(name);
    this.tcpClientSocket = tcpClientSocket;
    this.maper = maper;

    tcpStart = new Thread(() -> receiveTCP(tcpClientSocket,log));
    tcpStart.start();
  }

  @Override
  public void processPacket(Packet packet) {
    switch (packet.getFirstId()) {
      case PLAYER -> {
        switch (packet.getSecondId()){
          case MOVE -> {
            setMovement(packet.getData());
          }
        }
      }
      case LOGIN -> {
        switch (packet.getSecondId()){
          case CONNECT -> {
            String[] data = new String(packet.getData(), StandardCharsets.UTF_8).split(";");

            String newName = data[0];
            if (!maper.nameTaken(newName, maper.getPlayerList())) {
              name = newName;
            }else{
              //TODO poslat uzivateli ze je jmeno zabrano
            }
            try {
              udpPort = Integer.parseInt(data[1].trim());
            }catch (NumberFormatException e) {
              log.error("Invalid UDP port in connection packet: " +  data[1].trim() + "ignored");
              return;
            }

            log.info("Player " + name + " listening on UDP port " + udpPort);

          }
          case DISCONNECT -> {
            log.info("Client wants be disconnected");
            maper.removePlayer(tcpClientSocket);
            runningTCP = false;
          }
        }
      }
      case MAP -> {
        switch (packet.getSecondId()){
          case START -> {
            log.info("sentMap START to " + tcpClientSocket);
            maper.sendMapToClient(tcpClientSocket);
            //log.debug("Map send to " + tcpClientSocket);
          }
        }
      }
    }
  }


  public void updatePosition(){ //TODO zkusit jestli to pujde s float hodnotama, kdyztak zpresnit na double
    if (lastUpdated == 0){
      lastUpdated = System.currentTimeMillis();
    }
    long delta = System.currentTimeMillis() - lastUpdated;
    //log.debug("delta " +delta);
    double elapsedTime = (double) delta/10e3;
    //log.debug("elapsedTime " +elapsedTime);

    lastUpdated = System.currentTimeMillis();

    //double elapsedTime =  maper.getHitboxer().getElapsedTime();

    //slow down
    //if (left && right || !left && !right) { //TODO zkonrolovat jestli to potřebuju kontrolovat
    //}
    if (speed.y<0){
      speed.y = (float) (speed.y * Math.pow(FRICTION.y, elapsedTime));
    }

    ///count maxSpeed
    if (onGround) {
      maxSpeed = new Point2D.Float((float) (MAX_SPEED_GROUND*elapsedTime), (float) (MAX_SPEED_GROUND*elapsedTime));
    } else {
      maxSpeed = new Point2D.Float((float) (MAX_SPEED_AIR.x*elapsedTime), (float) (MAX_SPEED_AIR.y*elapsedTime));
    }

    //System.out.println(" " + maxSpeed.x + " " + maxSpeed.y + " " + getSpeed());

    Rectangle2D.Float hitBox = getHitBox();
      /*
      for (Ladder ladder : maper.getLadders()) {
        if (hitBox.intersects(ladder.getHitBoxServer())) {
          onGround = true;
          if (up) {
            setDirection(Direction.UP);
            speed.y = -maxSpeed.y;
          } else if (down) {
            speed.y = maxSpeed.y / 2;
          } else {
            speed.y = 0;
          }
          break;
        }
      }*/
    if (!onGround) {
      //log.debug("start " + speed.y +" " + PLAYER_ACCELERATION.y +" * "+ elapsedTime+ " ="+(float) (PLAYER_ACCELERATION.y*elapsedTime));
      speed.y += (float) (PLAYER_ACCELERATION.y*elapsedTime);
      //log.debug("end" + speed.y);
      if ( speed.y > 12){
        System.exit(12);
      }
      //log.debug("gravitation" + (float) (PLAYER_ACCELERATION.y*elapsedTime));
    }

    //TODO player say, maybe add down
    if (left){
      if (speed.x > 0){
        speed.x *= -1;
      }
      //speed.x -= (float) (PLAYER_ACCELERATION.x *elapsedTime);
      speed.x -= (float) (MAX_SPEED_AIR.x*elapsedTime);

    }else if (right){
      if (speed.x < 0){
        speed.x *= -1;
      }
      //speed.x += (float) (PLAYER_ACCELERATION.x *elapsedTime);
      speed.x += (float) (MAX_SPEED_AIR.x*elapsedTime);
    }else{
      //TODO tohle prepsat pomoci signum
      if (Math.abs(speed.x) < MIN_SPEED*elapsedTime) {
        speed.x = 0;
      }
      if (speed.x > 0){
        //log.debug("Player speed X " + speed.x + " " + (float) (FRICTION.x *elapsedTime));
        speed.x -= (float) (FRICTION.x *elapsedTime);
      }else if (speed.x < 0){
        speed.x += (float) (FRICTION.x *elapsedTime);
      }
      //log.debug("friction start " + speed.x + "   " + Math.pow(FRICTION.x, elapsedTime)+ "  "+ elapsedTime);
      //log.debug("friction end " + speed.x);
    }

    if (up && onGround) {
      speed.y  = -PLAYER_JUMP_CONST;
      onGround = false;
    }

    //speed limits
    if (speed.x > maxSpeed.x) {
      speed.x = maxSpeed.x;
    } else if (speed.x < -maxSpeed.x) {
      speed.x = -maxSpeed.x;
    }
      /*
      if (speed.y > maxSpeed.y) {
        speed.y = maxSpeed.y;
      } else if (speed.y < -maxSpeed.y) {
        speed.y = -maxSpeed.y;
      }*/

    //Position Control
    hitBox.x += speed.x;

    for (Wall wall : maper.getWalls()) {
      if (hitBox.intersects(wall.getHitBoxServer())) {
        hitBox.x -= speed.x;
        while (!wall.getHitBoxServer().intersects(hitBox)){
          hitBox.x += (float) (ADHESION*Math.signum(speed.x));
        }
        hitBox.x -= (float) (ADHESION*Math.signum(speed.x));
        speed.x = 0;
      }
    }
    getPosition().x = hitBox.x;

    hitBox.y += speed.y;

    onGround = false;
    //log.debug("wall " + maper.getWalls() + " plyer " + getHitBox());
    for (Wall wall : maper.getWalls()) {
      if (hitBox.intersects(wall.getHitBoxServer())) {
        hitBox.y -= speed.y;
        while (!wall.getHitBoxServer().intersects(hitBox)) {
          hitBox.y += (float) (ADHESION * Math.signum(speed.y));
        }
        hitBox.y -= (float) (ADHESION * Math.signum(speed.y));

        if (hitBox.y < wall.getHitBoxServer().y) {
          onGround = true;
        }else{
          speed.y = 0;
        }

      }
    }
    getPosition().y = hitBox.y;


    if (speed.x > 0) {
      setDirection(Direction.RIGHT);
    }
    if (speed.x < 0) {
      setDirection(Direction.LEFT);
    }
    if (down) {
      setDirection(Direction.DOWN);
    }

    for (Hedgehog hedgehog : maper.getHedgehogs()) {
      if (getHitBox().intersects(hedgehog.getHitBoxServer())) {

        setPosition(new Point2D.Float(maper.getServerConfig().getStartPosition().x, maper.getServerConfig().getStartPosition().y));
        speed = new Point2D.Float(0,0);

        //TODO dead count
      }
    }
         /*for (
  Checkpoint checkpoint : hitBoxHelper.getMapHelper().getCheckpoints()) {
         checkpoint.collisionControl(this);
         }
         for (
  Door door : hitBoxHelper.getMapHelper().getDoors()) {
         door.collisionControl(this);
         }
        }*/

    //log.debug("" + getPosition()+ "");
  }
  @Override
  public String toString(){
    return name +": "+ tcpClientSocket.getInetAddress().getHostAddress() + " TCP:" + tcpClientSocket.getPort() +" UDP:"+ udpPort;
  }

  @Override
  public boolean getRunningTCP() {
    return runningTCP;
  }

  @Override
  public void setRunningTCP(boolean running) {
    runningTCP = running;
  }

  public void sendPosition(ServerPlayer receiver) {
    if (!serverSocket.isClosed() && receiver.getUdpPort() != 0){
      try {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

        objectStream.writeObject(name);
        objectStream.writeObject(getPosition());
        objectStream.writeObject(getDirection());
        objectStream.writeObject(onGround);

        objectStream.flush();
        byte[] data = byteStream.toByteArray();
        new Packet(Packet.FirstPacketID.PLAYER, Packet.SecondPacketID.POSITION, data).sendUDP(serverSocket, receiver.getTcpClientSocket().getInetAddress(), receiver.getUdpPort());

        objectStream.close();
        byteStream.close();

      } catch (IOException e) {
        log.error("Error create packet for player position");
      }
    }
  }
}
