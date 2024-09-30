package edu.utep.cs4330.battleship;

import android.util.Log;

import edu.utep.cs4330.battleship.dto.UserSingleton;
import edu.utep.cs4330.battleship.dto.object.User;
import edu.utep.cs4330.battleship.dto.response.MqttObject;
import edu.utep.cs4330.battleship.dto.object.Position;
import edu.utep.cs4330.battleship.dto.response.NewGameResponse;
import edu.utep.cs4330.battleship.service.MqttHandler;

/**
 * Created by Gerardo Cervantes and Eric Torres on 4/10/2017.
 */

public class NetworkAdapter {

    static final String PLACED_SHIPS = "SHIPS PLACED";
    static final String NEW_GAME = "NEW GAME REQUEST";
    static final String ACCEPT_NEW_GAME_REQUEST = "ACCEPTED NEW GAME REQUEST";

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
    static UserSingleton userSingleton = null;

    //Methods accessed statically, prevents objects from being created to avoid confusion
    private NetworkAdapter() {
    }

    /**
     * Used to set a new socket, also initializes the printwriter and bufferedreader based on the socket
     * so messages can be sent
     */
    static void setSocket() {
        userSingleton = UserSingleton.getInstance();
        mqttHandler = new MqttHandler();
        mqttHandler.subscribe("battleship/" + userSingleton.getId());
    }
    /**
     * Reads messages  by the other player and returns them, blocks the calling thread until a message is recieved
     *
     * @return null if connection was lost
     */
    static String readMessage() {

//        try {
//            Log.d("wifiMe", "Going to read messages part 1");
//            if (in == null) {
//                //Only returns null if sockets aren't set correctly
//            }
//
//
//            Log.d("wifiMe", "Going to read messages part 2");
//
//            String msg;
//
//            while ((msg = in.readLine()) != null) {
//                Log.d("wifiMe", "Got message");
//                if (msg.equals("") || msg.equals(" ")) { //Checking " " probably unnecessary
//                    continue;
//                }
//                return msg;
//            }
//
//        } catch (IOException e) {
//            Log.d("wifiMe", "IOException ON NETWORK ADAPTER CLASS, READ MESSAGES METHOD");
//        }
//        Log.d("wifiMe", "Return msg null");
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

        Log.d("wifiMe", "Board being sent: " + board.toString());
    }


    /**
     * Writes a place shot message, and places it in given coordinates
     */
    static void writePlaceShotMessage(int x, int y) {

    }
    /**
     * Writes a message to other player to stop reading messages
     */
    static void writeStopReadingMessage() {

    }

    /**
     * Writes a message to other player to request a new game
     */
    static void writeNewGameMessage() {

    }

    /**
     * Writes a message to other player to accept the new game request
     */
    static void writeAcceptNewGameMessage() {

    }

    static void writeAcceptNewGameMessage(String topic) {
        mqttHandler.publish(topic, new MqttObject(ACCEPT_NEW_GAME_REQUEST, userSingleton.getId(),userSingleton.getUsername(),null));
    }

    static void writeRejectNewGameMessage(String topic) {
        mqttHandler.publish(topic, new MqttObject(REJECT_NEW_GAME_REQUEST,userSingleton.getId(),userSingleton.getUsername(), null));
    }

    static void writeNewGameMessage(String topic) {
        mqttHandler.publish(topic, new MqttObject(NEW_GAME, userSingleton.getId(),userSingleton.getUsername(), null));
    }
    static void writeStopReadingMessage(String topic) {
//        mqttHandler.publish(topic, new MqttObject(STOP_READING, null, null));
    }

    static void writePlaceShotMessage(String topic,Integer x, Integer y) {
        mqttHandler.publish(topic, new MqttObject(PLACE_SHOT,userSingleton.getId(),userSingleton.getUsername(),new Position(x,y)));
    }
    static void writeBoardMessage(String topic,Board board) {
        mqttHandler.publish(topic,new MqttObject(PLACED_SHIPS,userSingleton.getId(),userSingleton.getUsername(),new String(PLACED_SHIPS + board.toString())));
    }

    /**
     * Writes a message to other player to reject the new game request
     */
    static void writeRejectNewGameMessage() {

    }

    /**
     * Returns true if there is a connection with the other player
     */
    static boolean hasConnection() {

        return true;
    }

    /**
     * Returns true if character is a digit
     */
    private static boolean isDigit(char l) {
        return (l >= '0' && l <= '9');
    }


}
