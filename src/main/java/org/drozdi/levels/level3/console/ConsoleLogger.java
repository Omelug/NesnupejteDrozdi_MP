package org.drozdi.levels.level3.console;

import org.drozdi.levels.level3.client.PlayerMP;

public class ConsoleLogger {

    private final String TCP_COLOR = ConsoleColors.CYAN;
    private final String UDP_COLOR = ConsoleColors.BLUE;

    //TODO colors
    public ConsoleLogger(){
    }
    public void playerConnect(PlayerMP playerMP){
        print(ConsoleColors.GREEN, "     + " + playerMP.getName() + "(" + playerMP.getIp() +"/"+ playerMP.getTCPPort()+")");
    }
    public void playerDisconnect(PlayerMP playerMP){
        print(ConsoleColors.RED, "     - " + playerMP.getName() + "(" + playerMP.getIp() +"/"+ playerMP.getTCPPort()+")");
    }

    public void serverStart() {
        print(ConsoleColors.CYAN," SERVER START ");
    }

    public void msgTCP(String message) {
        print(TCP_COLOR,message);
    }
    public void TCPInfoSend(String message) {
        print(TCP_COLOR,message + " -> ");
    }
    public void TCPInfoReceive(String message) {
        print(TCP_COLOR," -> " + message);
    }

    public void msgUDP(String message) {
        print(UDP_COLOR,message);
    }
    public void UDPInfoSend(String message) {
        print(UDP_COLOR, message + " -> ");
    }
    public void UDPInfoReceive(String message) {
        print(UDP_COLOR," -> " + message);
    }

    private void print(String color,String message){
        System.out.println(color + message + ConsoleColors.RESET);
    }

    public void printError(String errorMessage) {
        print(ConsoleColors.RED, errorMessage);
    }
}

