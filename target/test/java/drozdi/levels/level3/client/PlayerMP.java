package drozdi.levels.level3.client;

import drozdi.levels.level3.server.HitBoxHelper;
import drozdi.net.NetSettings;
import lombok.Data;
import drozdi.game.Test;
import drozdi.levels.level3.Bullet;
import FileManager;
import Panel;
import drozdi.levels.level3.Wall;
import drozdi.levels.level3.walls.Checkpoint;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.Socket;

@Data
public class PlayerMP {
    private String name;
    private Point2D.Double position;
    private Point2D.Double size;
    private Checkpoint checkpoint;
    private boolean left, right, up, down;
    private boolean onGround;
    private int deathCount = 0;
    private Point2D.Double speed = new Point2D.Double(0, 0);
    private Point2D.Double maxSpeed;
    private Direction direction;
    private int shot;
    private boolean shooting;
    private long lastUpdated;
    private Socket clientSocket;
    private int UDPPort = NetSettings.defaultClientListeningUDPPort;

    public PlayerMP(String name){
        this.name = name;
        position = new Point2D.Double( HitBoxHelper.defaultPositionX, HitBoxHelper.defaultPositionY);
        size = new Point2D.Double(0.9, 0.9);
    }

    public PlayerMP(String name, Socket clientSocket,int UDPPort) {
        this(name);
        this.clientSocket = clientSocket;
        this.UDPPort = UDPPort;
        lastUpdated = System.currentTimeMillis();
    }
    public void draw(Graphics2D g2d, GamePanel panel) {

        if (Test.isHitBoxPlayer()) {
            g2d.setColor(Color.green);
            panel.drawHitBox(getHitBox(panel));
        }

        if (direction == null){
            drawPlayer(g2d,FileManager_lvl3.playerRight, panel);
            return;
        }
        switch (direction){
            case UP -> drawPlayer(g2d, FileManager_lvl3.playerUp, panel);
            case RIGHT -> drawPlayer(g2d,FileManager_lvl3.playerRight, panel);
            case DOWN -> drawPlayer(g2d,FileManager_lvl3.playerDown, panel);
            case LEFT -> drawPlayer(g2d,FileManager_lvl3.playerLeft, panel);
        }
    }

    private void drawPlayer(Graphics2D g2d, BufferedImage image, GamePanel panel) {
        Rectangle2D.Double hitBox = getHitBox(panel);
        g2d.drawImage( image, (int) (hitBox.x * panel.getCellSize()), (int) (hitBox.y  * panel.getCellSize()), (int) (size.x * panel.getCellSize()), (int) (size.y* panel.getCellSize()), null);
    }


    public void drawClientPlayer(Graphics2D g2d, GamePanel panel) {
        if (panel.getScreen().intersects(getHitBox(panel))) {
            /*g2d.setColor(Color.cyan);
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int x = (int) ((position.x + size.x /2 -(fontMetrics.stringWidth(name)) / 2)- panel.getShift().x)* panel.getCellSize();
            g2d.drawString(name, x, (int) (position.y -10));
            g2d.drawString((position.x - panel.getShift().x)* panel.getCellSize() + ";"
                    + (position.y - panel.getShift().y)* panel.getCellSize() + "("
                    + panel.getMapHelper().getPlayerShots().size()* panel.getCellSize()
                    + ")" + "(" + panel.getMapHelper().getEntityShots().size()* panel.getCellSize() + ") - " + onGround,
                    (int) ((position.x - size.x*2 )* panel.getCellSize()), (int) (position.y* panel.getCellSize()));

            draw(g2d, panel);*/
        }
    }

    public Rectangle2D.Double getHitBox(GamePanel panel){
        return new Rectangle2D.Double(position.x - panel.getShift().x, position.y, size.x, size.y);
    }
    public Rectangle2D.Double getHitBoxServer(){
        return new Rectangle2D.Double(position.x, position.y, size.x, size.y);
    }
    public void updatePosition(HitBoxHelper hitBoxHelper) {
        long delta = System.currentTimeMillis() - lastUpdated;
        double deltaDouble = (double) delta/10e3;
        lastUpdated = System.currentTimeMillis();

        Rectangle2D.Double hitBox = getHitBoxServer();
        
        double speedConst = 50*deltaDouble; //TODO constant of speed
        if (onGround) {
            maxSpeed = new Point2D.Double(10*speedConst, 10*speedConst);
        } else {
            maxSpeed = new Point2D.Double(10*speedConst, 10*speedConst);
        }

        if (left && right || !left && !right) {
            double decelerationFactor = 0.7;
            //System.out.println("Player move " + Math.pow(decelerationFactor, speedConst*5));
            speed.x = speed.x * Math.pow(decelerationFactor, speedConst*5); //TODO slowDown constant
        }
        if (left){
            speed.x = -speedConst;
        }
        if (right){
            speed.x = speedConst;
        }

        //gravitation
        if (!onGround) {
            speed.y += speedConst/3; //TODO gravitation constant
        }

        //speed limits
        if (speed.x > maxSpeed.x) {
            speed.x = maxSpeed.x;
        } else if (speed.x < -maxSpeed.x) {
            speed.x = -maxSpeed.x;
        }
        /*if (speed.y > maxSpeed.y) {
            speed.y = maxSpeed.y;
        } else if (speed.y < -maxSpeed.y) {
            speed.y = -maxSpeed.y;
        }*/

        /**
         for (Ladder ladder : hitBoxHelper.getMapHelper().getLadders()) {
            if (hitBox.intersects(ladder.getHitBoxServer())) {
                onGround = true;
                direction = Direction.UP;
                if (up) {
                    speed.y = -maxSpeed.y;
                } else if (down) {
                    speed.y = maxSpeed.y / 2;
                } else {
                    speed.y = 0;
                }
            }
        }*/

       if (up && onGround) {
           speed.y = -0.4; //TODO jump constant
           onGround = false;
           System.out.println("onGround turned off, " + -0.3*maxSpeed.y);
        }

        if (speed.x != 0 && speed.x < 0.1*speedConst && speed.x > -0.1*speedConst) {
            speed.x = 0;
        }
        hitBox.x += speed.x;

        for (Wall wall : hitBoxHelper.getMapHelper().getWalls()) {
            if (hitBox.intersects(wall.getHitBoxServer())) {
                hitBox.x -= speed.x;
              while (!wall.getHitBoxServer().intersects(hitBox))
                    hitBox.x += 0.01*Math.signum(speed.x);
                hitBox.x -= 0.01*Math.signum(speed.x);
            }
        }
        position.x = hitBox.x;

        hitBox.y += speed.y;
        onGround = false;
        for (Wall wall : hitBoxHelper.getMapHelper().getWalls()) {
            if (hitBox.intersects(wall.getHitBoxServer())) {
                hitBox.y -= speed.y;
                while (!wall.getHitBoxServer().intersects(hitBox)){
                    hitBox.y += 0.01*Math.signum(speed.y*100);
                    //System.out.println("" +Math.signum(speed.y*100));
                }
                hitBox.y -= 0.01*Math.signum(speed.y);
                if (hitBox.y < wall.getHitBoxServer().y) {
                    onGround = true;
                }else{
                    speed.y = 0;
                }
                //System.out.println("onGround turned on, " + speed);
            }
        }
        position.y = hitBox.y;

        if (speed.x > 0) {
            direction = Direction.RIGHT;
        }

        if (speed.x < 0) {
            direction = Direction.LEFT;
        }

        if (down) {
             direction = Direction.DOWN;
        }
        hitBox.x = position.x;
        hitBox.y = position.y;

        //System.out.println("Speed: " + speed.x + "," + speed.y);

        //TODO System.out.println("Player - speed: " + speed + ", "+ onGround);
        /**
        for (Hedgehog hedgehog : hitBoxHelper.getMapHelper().getHedgehogs()) {
            hedgehog.collisionControl(this);
        }
        for (Checkpoint checkpoint : hitBoxHelper.getMapHelper().getCheckpoints()) {
            checkpoint.collisionControl(this);
        }
        for (Door door : hitBoxHelper.getMapHelper().getDoors()) {
            door.collisionControl(this);
        }**/
    }

    public synchronized void shot(HitBoxHelper hitBoxHelper) {
        shot++;
        if (shot > 20 && (shooting || direction == Direction.DOWN)) {
            shot = 0;
            hitBoxHelper.getMapHelper().getPlayerShots().add(new Bullet(this));
        }
    }

    public void dead() {
        deathCount++;
        //TODO dead
    }

    public String toStringServer() {
        return name + "{"+getIp()+":"+ clientSocket.getPort() +", position["+position.x+";"+ position.y +"], "+getMove()+"}";
    }

    public InetAddress getIp(){
        return clientSocket.getInetAddress();
    }
    public int getTCPPort(){
        return clientSocket.getPort();
    }

    private String getMove() {
        String result = "";
        if (up){
            result += 'w';
        }
        if (left){
            result += 'a';
        }
        if (down){
            result += 's';
        }
        if (right){
            result += 'd';
        }
        result += '_';

        if (shooting){
            result += 'S';
        }
        return result;
    }
    public enum Direction {
        UP, DOWN,RIGHT,LEFT
    }
}

