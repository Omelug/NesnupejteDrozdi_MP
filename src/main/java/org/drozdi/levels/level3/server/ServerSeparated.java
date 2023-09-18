package org.drozdi.levels.level3.server;

public class ServerSeparated {
    static ServerWindow serverWindow;
    public static void main(String[] args) {
        GameServer gameServer = new GameServer();

        serverWindow = new ServerWindow(gameServer);
        serverWindow.setVisible(true);

        gameServer.start();
    }
}
