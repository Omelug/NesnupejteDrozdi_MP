package drozdi.clientside;

import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;
import drozdi.net.Packet;
import drozdi.net.Packet.FirstPacketID;
import drozdi.net.Packet.SecondPacketID;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window extends JFrame {
  static private final ConsoleLogger log = new ConsoleLogger(ConsoleColors.WHITE_UNDERLINED);
  private final Client client;

  public Window(Client client) {

    this.client = client;
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(new Dimension(800, 600));
    setMinimumSize(new Dimension(400, 300));

    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());

    JPanel infoLine = new JPanel();
    infoLine.setPreferredSize(new Dimension(800, 50));
    infoLine.setBackground(Color.RED);
    contentPane.add(infoLine, BorderLayout.NORTH);

    setContentPane(contentPane);
    pack();

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        log.info("Window is closed. Closing the network connection.");
        new Packet(FirstPacketID.LOGIN, SecondPacketID.DISCONNECT).sendTCP(client.getTcpClientSocket());
        client.stop();
      }
    });

  }

  public Panel createPanel(Panel panel) {
    add(panel, BorderLayout.CENTER);
    pack();
    return panel;
  }

  public void renderStart() {
    setVisible(true);
  }

}
