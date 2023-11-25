package drozdi.levels.level3.server;

import lombok.Getter;
import lombok.Setter;
import drozdi.levels.level3.client.PlayerMP;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class ServerWindow extends JFrame {
    private DefaultListModel<String> playerListModel;
    private JList<String> playerList;
    private GameServer gameServer;
    @Getter @Setter
    private double TPS;

    public ServerWindow(GameServer gameServer) {
        this.gameServer = gameServer;

        playerListModel = new DefaultListModel<>();
        playerList = new JList<>(playerListModel);

        Timer timer = new Timer(25, e -> update());
        timer.start();

        setupUI();
    }

    private void setupUI() {
        setTitle("Server stats");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(playerList);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JButton disconnectBtn = new JButton();
        disconnectBtn.setText("Disconnect all players");
        getContentPane().add(disconnectBtn, BorderLayout.BEFORE_FIRST_LINE);
        disconnectBtn.addActionListener(e -> gameServer.disconnectAll());

    }

    private void update() {
        setTitle("Server stats: TPS: " + TPS);
        Set<PlayerMP> players = gameServer.getHitBoxHelper().getMapHelper().getPlayerList();
        // Clear the existing list and add the updated players
        playerListModel.clear();
        for (PlayerMP player : players) {
            playerListModel.addElement(player.toStringServer());
        }
    }
}
