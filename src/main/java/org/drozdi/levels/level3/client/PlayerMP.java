package org.drozdi.levels.level3.client;

import lombok.Data;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.Test;
import org.drozdi.levels.level3.Bullet;
import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Wall;
import org.drozdi.levels.level3.server.HitBoxHelper;
import org.drozdi.levels.level3.walls.Checkpoint;
import org.drozdi.levels.level3.walls.Door;
import org.drozdi.levels.level3.walls.Hedgehog;
import org.drozdi.levels.level3.walls.Ladder;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.util.Timer;

@Data
public class PlayerMP {
    private String name;
    private Point2D.Double position;
    private Point2D.Double size;
    private InetAddress ipAddress;
    private int port;
    private Checkpoint checkpoint;
    private boolean left, right, up, down;
    private boolean onGround = false;
    private int deathCount = 0;
    private Point2D.Double speed = new Point2D.Double(0, 0);
    private Point2D.Double maxSpeed;
    private Direction direction;
    private int shot;
    private boolean shooting;
    private long lastUpdated;

    public PlayerMP(String name){
        this.name = name;
        position = HitBoxHelper.defaultPosition;
        size = new Point2D.Double(0.9, 0.9);
    }

    public PlayerMP(String name, InetAddress ipAddress, int port) {
        this(name);
        System.out.println("Connected user:" +  name);
        this.ipAddress = ipAddress;
        this.port = port;
        position = HitBoxHelper.defaultPosition;
        size = new Point2D.Double(0.9, 0.9);
        lastUpdated = System.currentTimeMillis();
    }
    public void draw(Graphics2D g2d, Panel_level3 panel) {

        if (Test.isHitBoxPlayer()) {
            g2d.setColor(Color.green);
            g2d.draw(getHitBox(panel));
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

    private void drawPlayer(Graphics2D g2d, BufferedImage image, Panel_level3 panel) {
        Rectangle2D.Double hitBox = getHitBox(panel);
        /*int radius = 40;
        g2d.setColor(Color.RED);
        /*g2d.drawOval((int) (hitBox.x* panel.getCellSize() - radius),
                (int) (hitBox.y* panel.getCellSize() - radius),
                2 * radius, 2 * radius);*/
        g2d.drawImage( image, (int) (hitBox.x * panel.getCellSize()), (int) (hitBox.y  * panel.getCellSize()), (int) (size.x * panel.getCellSize()), (int) (size.y* panel.getCellSize()), null);
    }


    public void drawClientPlayer(Graphics2D g2d, Panel_level3 panel) {
        if (panel.getScreen().intersects(getHitBox(panel))) {
            g2d.setColor(Color.cyan);
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int x = (int) (position.x + size.x /2 -(fontMetrics.stringWidth(NesnupejteDrozdi.account)) / 2)* panel.getCellSize();
            g2d.drawString(NesnupejteDrozdi.account, x, (int) (position.y -10));
            g2d.drawString((position.x + panel.getShift().x)* panel.getCellSize() + ";"
                    + (position.y + panel.getShift().y)* panel.getCellSize() + "("
                    + panel.getMapHelper().getPlayerShots().size()* panel.getCellSize()
                    + ")" + "(" + panel.getMapHelper().getEntityShots().size()* panel.getCellSize() + ")\n ",
                    (int) ((position.x - size.x*2 )* panel.getCellSize()), (int) (position.y* panel.getCellSize()));

            draw(g2d, panel);
        }
    }

    public Rectangle2D.Double getHitBox(Panel_level3 panel){
        return new Rectangle2D.Double(position.x - panel.getShift().x, position.y, size.x, size.y);
    }

    public Rectangle2D.Double getHitBoxServer(){
        return new Rectangle2D.Double(position.x, position.y, size.x, size.y);
    }

    public void updatePosition(HitBoxHelper hitBoxHelper) {
        long delta = System.currentTimeMillis() - lastUpdated;
        lastUpdated = System.currentTimeMillis();

        Rectangle2D.Double hitBox = getHitBoxServer();

        double comparison = 50;
        if (onGround) {
            maxSpeed = new Point2D.Double(6*comparison, 6*comparison);
        } else {
            maxSpeed = new Point2D.Double(4*comparison, 6*comparison);
        }

        if (left && right || !left && !right)
            speed.x *= 0.8;
        else if (left)
            speed.x = speed.x - comparison;
        else speed.x = speed.x + comparison;

        if (speed.x < 0.1*comparison && speed.x > -0.1*comparison) {
            speed.x = 0;
        }

        //speed limit X
        if (speed.x > maxSpeed.x) {
            speed.x = maxSpeed.x;
        } else if (speed.x < -maxSpeed.x) {
            speed.x = -maxSpeed.x;
        }

        if (up && onGround) {
            speed.y = -0.7*maxSpeed.y;
            onGround = false;
        }
        //gravitation
        speed.y += comparison;

        speed.x *= (double) delta/10000;
        hitBox.x += speed.x;
        for (Wall wall : hitBoxHelper.getMapHelper().getWalls()) {
            if (hitBox.intersects(wall.getHitBoxServer())) {
                hitBox.x -= speed.x;
                while (!wall.getHitBoxServer().intersects(hitBox))
                    hitBox.x += Math.signum(speed.x);
                hitBox.x -= Math.signum(speed.x);
                speed.x = 0;
            }
        }
        position.x = hitBox.x;

        onGround = false;
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
        }

        speed.y *= (double) delta/10000;
        hitBox.y += speed.y;
        for (Wall wall : hitBoxHelper.getMapHelper().getWalls()) {
            if (hitBox.intersects(wall.getHitBoxServer())) {
                hitBox.y -= speed.y;
                while (!wall.getHitBoxServer().intersects(hitBox))
                    hitBox.y += Math.signum(speed.y);
                hitBox.y -= Math.signum(speed.y);
                if (speed.y > 0)
                    onGround = true;
                speed.y = 0;
            }
        }
        position.y = hitBox.y;

        if (speed.x > 0) {
            direction = Direction.RIGHT;
        }

        if (speed.x < 0) {
            direction = Direction.LEFT;
        }

        hitBox.x = position.x;
        hitBox.y = position.y;

        if (down) {
            direction = Direction.DOWN;
        }
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
    public void updateClientShift(Panel_level3 panel) {
        //panel.getShift().x += speed.x;
        //panel.getShift().y += speed.y;
        //TODO update shift podle pozice
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
}
