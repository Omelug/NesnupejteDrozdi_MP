package org.drozdi.levels.level3.server;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level3.client.PlayerMP;

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

        Timer timer = new Timer(50, e -> updatePlayerList());
        timer.start();

        setupUI();
        updatePlayerList();
    }

    private void setupUI() {
        setTitle("Server stats");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Server stats: TPS: " + TPS);
        JScrollPane scrollPane = new JScrollPane(playerList);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void updatePlayerList() {
        Set<PlayerMP> players = gameServer.getHitBoxHelper().getMapHelper().getPlayerList();

        // Clear the existing list and add the updated players
        playerListModel.clear();
        for (PlayerMP player : players) {
            playerListModel.addElement(player.toStringServer());
        }
    }
}
