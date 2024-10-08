package edu.utep.cs4330.battleship;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import edu.utep.cs4330.battleship.dto.response.MqttResponse;
import edu.utep.cs4330.battleship.dto.Position;
import edu.utep.cs4330.battleship.service.MqttHandler;

/**
 * Created by Gerardo Cervantes and Eric Torres on 4/10/2017.
 */

public class NetworkAdapter {

    /**
     * The socket that will be used for the connection, null if no connection is established
     */
    private static Socket socket;

    /**
     * Used to write messages to socket
     */
    private static PrintWriter out;

    /**
     * Used to receive messages from other user
     */
    private static BufferedReader in;

    /**
     * Message constant, sent or received when all your ships have been placed
     */
    static final String PLACED_SHIPS = "SHIPS PLACED";

    /**
     * Message constant, Sent or received when a player requests to play a new game
     */
    static final String NEW_GAME = "NEW GAME REQUEST";

    /**
     * Message constant, representing when you accept a new game request
     */
    static final String ACCEPT_NEW_GAME_REQUEST = "ACCEPTED NEW GAME REQUEST";

    /**
     * Message constant, representing when you reject a new game request
     */
    static final String REJECT_NEW_GAME_REQUEST = "REJECT NEW GAME REQUEST";

    /**
     * Message constant, Sent or received when a place has been shot, message sent usually contains coordinates in the format of "PLACE SHOT 3,5"
     */
    static final String PLACE_SHOT = "PLACE SHOT";

    /**
     * Message constant, Used to tell other player to stop reading messages given
     */
    static final String STOP_READING = "STOP READING";

    static MqttHandler mqttHandler = null;

    //Methods accessed statically, prevents objects from being created to avoid confusion
    private NetworkAdapter() {
    }

    /**
     * Used to set a new socket, also initializes the printwriter and bufferedreader based on the socket
     * so messages can be sent
     */
    static void setSocket() {
        mqttHandler = MqttHandler.getInstance();
    }

    /**
     * Returns the socket being used
     */
    static Socket getSocket() {
        return socket;
    }

    /**To be called on a new thread - blocks the calling thread, if it returns, should be called again on new thread to continue listening to messages
     * Once it returns, it stops listening for messages
     * How to use, call the method and when it returns make a comparison with Constant values in Network Adapter, ex: run(){ if( readMessage().equals(NetworkAdapter.NEW_GAME) ) {...} }*/

    /**
     * Pseudocode:
     * void startReadingMessage(){
     *
     *     Thread readMessages = new Thread(new Runnable(){
     *         public void run(){
     *             while(true){
     *                  String msg = NetworkAdapter.readMessage();
     *                  if(msg == null){
     *                      //Connection lost handler
     *                  }
     *                  else if(msg.equals(NetworkAdapter.PLACED_SHIPS)){
     *                      //Do appropriate action
     *                  }
     *                  else if(msg.equals(NetworkAdapter.NEW_GAME)){
     *                      //request new game
     *                  }
     *                  else if(msg.startsWith(NetworkAdapter.PLACE_SHOT){
     *                      int[] coordinateShots = decipherPlaceShot(msg);
     *                      //shoot ship, check coordinateShots is not null
     *                  }
     *
     *             }
     *         }
     *
     *     }).start();
     *
     * }
     * */

    /**
     * Reads messages  by the other player and returns them, blocks the calling thread until a message is recieved
     *
     * @return null if connection was lost
     */
    static String readMessage() {

        try {
            Log.d("wifiMe", "Going to read messages part 1");
            if (in == null) {
                //Only returns null if sockets aren't set correctly

                if (socket == null) {
                    return null;
                } else {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
            }


            Log.d("wifiMe", "Going to read messages part 2");

            String msg;

            while ((msg = in.readLine()) != null) {
                Log.d("wifiMe", "Got message");
                if (msg.equals("") || msg.equals(" ")) { //Checking " " probably unnecessary
                    continue;
                }
                return msg;
            }

        } catch (IOException e) {
            Log.d("wifiMe", "IOException ON NETWORK ADAPTER CLASS, READ MESSAGES METHOD");
        }
        Log.d("wifiMe", "Return msg null");
        return null;
    }

    /**
     * Given the board String representation converts it back to the original board and returns it
     */
    static Board decipherPlaceShips(String opponentBoard) {
        if (opponentBoard == null || !opponentBoard.startsWith(PLACED_SHIPS)) {
            return null;
        }
        opponentBoard = opponentBoard.substring(NetworkAdapter.PLACED_SHIPS.length());

        Log.d("wifiMe", "Attempting to convert string to board, the string: " + opponentBoard);
        Board b = new Board(10);
        int traverseString = 0;
        char[] tb = opponentBoard.toCharArray();
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                int shipType = tb[traverseString];
                Place place = b.placeAt(j, i);

                if (shipType == '5')
                    place.setShip(new Ship("aircraftcarrier", 5));
                else if (shipType == '4')
                    place.setShip(new Ship("battleship", 4));
                else if (shipType == '3')
                    place.setShip(new Ship("submarine", 3));
                else if (shipType == '2')
                    place.setShip(new Ship("frigate", 2));
                else if (shipType == '1')
                    place.setShip(new Ship("minesweeper", 1));
                else {
                    //Don't set a ship
                }

                traverseString++;
            }
        }

        Log.d("wifiMe", "Deciphered board " + b.toString());
        return b;
    }


    /**
     * NOTE METHOD DOES NOT WORK IF BOARD IS OF SIZE BIGGER THAN 10, needs to be changed slightly to work with boards size bigger than 10
     *
     * @return null if was not a placeShot message, or message did not have 2 coordinates specified
     * @return integer array of size 2 with coordinates of places shot, coordinates use 0 based index, (0,0) - top left corner.  int[0] - x coordinate, int[1] - y coordinate
     */
    public static int[] decipherPlaceShot(String msg) {
        if (msg == null || !msg.startsWith(PLACE_SHOT)) {
            return null;
        }
        int[] coordinatesShot = new int[2];
        boolean firstDigitFound = false;
        for (int i = 0; i < msg.length(); i++) {
            char letter = msg.charAt(i);

            if (isDigit(letter)) {
                int digitFound = Character.getNumericValue(letter);

                if (firstDigitFound) {
                    coordinatesShot[1] = digitFound;
                    return coordinatesShot;
                } else {
                    coordinatesShot[0] = digitFound;
                }
                firstDigitFound = true;
            }
        }

        return coordinatesShot;
    }

    /**
     * Writes the board to the other player using the Board's toString method
     */
    static void writeBoardMessage(Board board) {
        out.println(PLACED_SHIPS + board.toString());
        out.flush();
        Log.d("wifiMe", "Board being sent: " + board.toString());
    }


    /**
     * Writes a place shot message, and places it in given coordinates
     */
    static void writePlaceShotMessage(int x, int y) {
        out.println(PLACE_SHOT + " " + x + "," + y);
        out.flush();//flush clears the message you just wrote
    }
    /**
     * Writes a message to other player to stop reading messages
     */
    static void writeStopReadingMessage() {
        out.println(STOP_READING + " ");
        out.flush();
    }

    /**
     * Writes a message to other player to request a new game
     */
    static void writeNewGameMessage() {
        out.println(NEW_GAME + " ");
        out.flush();
    }

    /**
     * Writes a message to other player to accept the new game request
     */
    static void writeAcceptNewGameMessage() {
        out.println(ACCEPT_NEW_GAME_REQUEST + " ");
        out.flush();
    }

    static void writeAcceptNewGameMessage(String topic) {
        mqttHandler.publish(topic, new MqttResponse(ACCEPT_NEW_GAME_REQUEST, null));
    }

    static void writeRejectNewGameMessage(String topic) {
        mqttHandler.publish(topic, new MqttResponse(REJECT_NEW_GAME_REQUEST, null));
    }

    static void writeNewGameMessage(String topic) {
        mqttHandler.publish(topic, new MqttResponse(NEW_GAME, null));
    }
    static void writeStopReadingMessage(String topic) {
        mqttHandler.publish(topic, new MqttResponse(STOP_READING, null));
    }

    static void writePlaceShotMessage(String topic, Integer x, Integer y) {
        mqttHandler.publish(topic, new MqttResponse(PLACE_SHOT, new Position(x,y)));
    }
    static void writeBoardMessage(String topic, Board board) {
        mqttHandler.publish(topic,new MqttResponse(PLACED_SHIPS,board));
    }

    /**
     * Writes a message to other player to reject the new game request
     */
    static void writeRejectNewGameMessage() {
        out.println(REJECT_NEW_GAME_REQUEST + " ");
        out.flush();
    }

    /**
     * Returns true if there is a connection with the other player
     */
    static boolean hasConnection() {
        return (socket != null);
    }

    /**
     * Returns true if character is a digit
     */
    private static boolean isDigit(char l) {
        return (l >= '0' && l <= '9');
    }


}
