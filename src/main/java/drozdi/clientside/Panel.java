package drozdi.clientside;
import drozdi.gameobjects.Bullet;
import drozdi.FileManager;
import drozdi.MoveInput;
import drozdi.gameobjects.Player.ClientPlayer;
import drozdi.gameobjects.Player.Player;
import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;
import drozdi.gameobjects.walls.*;
import drozdi.map.ClientMaper;
import drozdi.map.Maper;
import lombok.Getter;
import lombok.Synchronized;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;


public class Panel extends JPanel {
  static private final ConsoleLogger log = new ConsoleLogger(ConsoleColors.BLACK_BRIGHT);

  @Getter
  private final ClientPlayer player;
  @Getter
  private final Client client;
  @Getter
  private final ClientMaper maper;
  @Getter(onMethod_={@Synchronized})
  private Point2D.Float shift = new Point2D.Float(0,0);
  @Getter(onMethod_={@Synchronized})
  private int cellSize;
  @Getter
  private Graphics2D g2d;
  @Getter
  private Rectangle screen;

  //help sets
  @Getter
  private Set<Wall> wallsOnScreen = new HashSet<>();


  public Panel(String playerName, Client client) {
    ClientPlayer.setPanel(this);
    player = new ClientPlayer(playerName);
    this.client = client;

    setFocusable(true);
    addKeyListener(new MoveInput(this));

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        cellSize = (int) (getHeight() / 18.25);
        Bullet.size = new Point((int) (cellSize / 2.2), (int) (cellSize / 2.2));
        screen = new Rectangle(0, 0, getWidth(), getHeight());
        repaint();
      }
    });

    setBackground(new Color(112, 146, 161));
    //setBounds(0, 100, get().getWidth(),getParent().getHeight());
    //cellSize = (int) (getHeight() / 18.25);
    //setPreferredSize(new Dimension(800,400));


    maper = new ClientMaper();

    BufferedImage map = FileManager.loadResource("client_data/maps/server_map.bmp");
    //BufferedImage map = FileManager.loadResource("server_data/maps/map2.bmp");
    if (map != null){
      maper.loadMap(map);
    }else{
      log.error("Map cannot be loaded");
    }

  }

  public void updateClientShift() {
    //TODO update shift podle pozice
    shift.x = player.getPosition().x - FileManager.defaultMapPosition.x;
    shift.y = player.getPosition().y - FileManager.defaultMapPosition.y;
  }

  @Override
  public void paint(Graphics graphics) {
    super.paint(graphics);
    g2d = (Graphics2D) graphics;

    //shift update
    updateClientShift();

    for (Wall wall : maper.getWalls()) {
      if (screen.intersects(wall.getHitBox(this))) {
        wallsOnScreen.add(wall);
      }
    }
    //updateClientShift();

    for (Wall wall : wallsOnScreen) {
      wall.draw(this);
    }
    for (Hedgehog hedgehog : maper.getHedgehogs()) {
      hedgehog.draw(this);
    }
    for (Ladder ladder : maper.getLadders()) {
      ladder.draw(this);
    }
    for (Checkpoint checkpoint : maper.getCheckpoints()) {
      checkpoint.draw(this);
    }
    for (Key key : maper.getKeys()) {
      key.draw(this);
    }
    for (Door door : maper.getDoors()) {
      door.draw(this);
    }
    for (Tower tower : maper.getTowers()) {
      tower.draw(this);
      /*if (Test.isLinesTower()) {
        if (player.getPosition() != null && player.getSize() != null){
          tower.drawLine(player, this);
        }else{
          System.out.println("position or size is null");
        }
      }*/
    }
    for (Slug slug : maper.getSlugs()) {
      slug.draw(this);
      /*if (Test.isLinesSlug()){
        if (player.getPosition() != null && player.getSize() != null){
          slug.drawLine(player, this);
        }else{
          System.out.println("position or size is null");
        }
      }*/
    }
    //log.debug("adawdaw");
    player.draw(this);

    for (Player player :  maper.getPlayerList()) {
      player.draw(this);
    }

    for (Bullet bullet : maper.getPlayerShots()) {
      bullet.draw(this);
    }
    for (Bullet bullet : maper.getEntityShots()) {
      bullet.draw(this);
    }

    /*if (Test.isHitBoxScreen()) {
      g2d.setColor(Color.red);
      drawHitBox(screen);
    }*/
  }

  public void keyReleased(KeyEvent e) {
    switch (Character.toLowerCase(e.getKeyChar())) {
      case 'w' ->	player.setUp(false);
      case 'a' -> player.setLeft(false);
      case 's' ->	player.setDown(false);
      case 'd' -> player.setRight(false);
      case 't' -> 	client.stop();
      case KeyEvent.VK_SPACE -> player.setShooting(false);
      default -> {
        return;
      }
    }
    player.move();
  }

  public void keyPressed(KeyEvent e) {
    switch (Character.toLowerCase(e.getKeyChar())) {
      case 'w' -> player.setUp(true);
      case 'a' -> player.setLeft(true);
      case 's' -> player.setDown(true);
      case 'd' -> player.setRight(true);
      case KeyEvent.VK_SPACE -> player.setShooting(true);
      default -> {
        return;
      }
    }
    player.move();
  }

  public void renderStart() {
    while (true){
      repaint();
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
