package fr.umontpellier.iut.wolfenstein.reseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Toute cette classe ne contient rien d'important dans le cadre du projet, qui avait pour objectif principal de recréer le moteur graphique de
 * wolfenstein 3D. Il n'est donc probablement pas pertinent pour la correction de celui çi de regarder ce que fait cette classe.
 * En résumé : Il s'agit du serveur qui sera créé par le joueur qui hébergera la partie de jeu, et qui mettra en contact tous les clients.
 */
public class WolfServer extends Thread {

    private static boolean serverRunning = false;
    private static ServerSocket mainSocket;
    private static ArrayList<PrintStream> connectedClients;

    private String state;
    private Socket socket;

    public static void startServer() {
        if (!serverRunning){
            try {
                connectedClients = new ArrayList<>();
                mainSocket = new ServerSocket(9632);
                WolfServer mainServer = new WolfServer(null, "parent");
                mainServer.start();
                serverRunning = true;
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        else {
            System.out.println("Le serveur principal tourne déjà");
        }
    }

    public WolfServer(Socket socket, String state){
        this.state = state;
        this.socket = socket;
    }

    public void run() {
        if (state.equals("child")){
            try {
                traitements();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            connexions();
        }
    }

    private void connexions() {
        while (serverRunning){
            try {
                Socket socketClient = mainSocket.accept();
                WolfServer childServer = new WolfServer(socketClient, "child");
                childServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void traitements() throws IOException {

        System.out.println("Connexion avec le client : " + socket.getInetAddress());

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream out = new PrintStream(socket.getOutputStream());
        if (!connectedClients.contains(out)){
            connectedClients.add(out);
            out.println("PLAYERNB:" + connectedClients.size());
        }
        while (serverRunning){
            try {
                String message;

                message = in.readLine();

                if (message.startsWith("ADDPLAYER")){
                    StringBuilder playerList = new StringBuilder();
                    for (int i = 0; i < connectedClients.size(); i++) {
                        playerList.append("player").append(i + 1).append(", ");
                    }
                    playerList = new StringBuilder(playerList.substring(0, playerList.length() - 2));
                    sendToAll("NEWLIST:" + playerList.toString());
                } else if (message.startsWith("PLAYERPOS")){
                    sendToOthers(message, out);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendToAll(String command){
        for (PrintStream connectedClient : connectedClients) {
            connectedClient.println(command);
        }
    }
    public static void sendToOthers(String command, PrintStream client){
        for (PrintStream connectedClient : connectedClients) {
            if (!connectedClient.equals(client)){
                connectedClient.println(command);
            }
        }
    }
}
