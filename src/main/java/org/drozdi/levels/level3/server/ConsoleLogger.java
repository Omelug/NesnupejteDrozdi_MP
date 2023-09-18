package org.drozdi.levels.level3.server;

import org.drozdi.levels.level3.client.PlayerMP;

public class ConsoleLogger {
    //TODO colors
    public ConsoleLogger(){
    }
    public void playerConnect(PlayerMP playerMP){
        System.out.println("     + " + playerMP.getName() + "(" + playerMP.getIpAddress() +"/"+ playerMP.getPort()+")");
    }
    public void playerDisconnect(PlayerMP playerMP){
        System.out.println("     - " + playerMP + "(" + playerMP.getIpAddress() +"/"+ playerMP.getPort()+")");
    }
    private enum LogType {
        PLAYER_CONNECT, PLAYER_DISCONNECT, ERROR, INFO, WARN
    }
}
