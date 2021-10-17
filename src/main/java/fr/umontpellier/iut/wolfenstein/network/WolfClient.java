package fr.umontpellier.iut.wolfenstein.network;

import fr.umontpellier.iut.wolfenstein.MainMenu;
import fr.umontpellier.iut.wolfenstein.MainMenuController;
import fr.umontpellier.iut.wolfenstein.Player;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WolfClient extends Thread{

    private static WolfClient instance;

    private volatile int playerNumber;

    private boolean isRunning;

    private ArrayList<Player> players;

    private final MainMenuController menu;
    private final PrintStream outputStream;
    private final BufferedReader serverResponse;

    public WolfClient(String ipAdress, MainMenuController menu) throws Exception {
        this.menu = menu;
        this.isRunning = true;

        InetAddress serveur = InetAddress.getByName(ipAdress);
        int port = 9632;

        Socket socket = new Socket(serveur, port);

        serverResponse = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = new PrintStream(socket.getOutputStream());
    }

    public static void setInstance(String ipAdress, MainMenuController menu) throws Exception {
        if (instance == null){
            instance = new WolfClient(ipAdress, menu);
            instance.start();
        }
    }

    public static WolfClient getInstance(){
        if (instance == null){
            throw new RuntimeException("Le client n'a pas été set");
        }
        return instance;
    }

    public void sendCommand(String command){
        outputStream.println(command);
    }

    public void run(){
        while (isRunning){
            String message;
            try {
                message = serverResponse.readLine();
                if (message.startsWith("NEWLIST:")){
                    menu.updateList(message.substring(8));
                }
                else if (message.startsWith("PLAYERNB:")){
                    this.playerNumber = Integer.parseInt(message.substring(9));
                }
                else if (message.equals("STARTGAME")){
                    Platform.runLater(() -> menu.startMultiplayerGame(this.playerNumber));
                }
                else if (message.startsWith("PLAYERPOS")){
                    int playerNB = Integer.parseInt(message.substring(9, 10));
                    Platform.runLater(() -> players.get(playerNB-1).setPosWithString(message));
                }
                else if (message.equals("EXITGAME")){
                    this.isRunning = false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getPlayerNumber(){
        while (playerNumber == 0) {
            Thread.onSpinWait();
        }
        return this.playerNumber;
    }

    public void setPlayers(ArrayList<Player> players){
        this.players = players;
    }
}
