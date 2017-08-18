package com.magendanz.villanovamec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client for getting and setting news and calendar items from the server
 */

public class MecClient {

    private static final String SPLIT_STRING = ";\\s+";

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Rep invariant: socket, in, out != null

    /**
     * Make a SquareClient and connect it to a server running on
     * hostname at the specified port.
     * @throws IOException if can't connect
     */
    public MecClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Send a request to the server. Requires this is "open".
     * @param infoToAdd any information to add to the server. The first section must be
     *                  an administrator key phrase in order for the add to succeed.
     * @throws IOException if network or server failure
     */
    public void sendRequest(String infoToAdd) throws IOException {
        out.print(infoToAdd + "\n");
        out.flush(); // important! make sure x actually gets sent
    }

    /**
     * Get the next set of MecItems from the server. Requires that the first line specify
     * the number of MecItems to receive
     * @return all of the MecItems sent by the server
     * @throws IOException if network or server failure or bad message formatting
     */
    public MecItem[] getReply() throws IOException {
        String reply = in.readLine();
        try{
            int entries = Integer.parseInt(reply);
            MecItem[] responses = new MecItem[entries];
            for (int i = 0; i < entries; i++){
                responses[i] = parse(reply);
            }
            return responses;
        } catch (NumberFormatException e){
            throw new IOException();
        }
    }

    /**
     * Closes the client's connection to the server.
     * This client is now "closed". Requires this is "open".
     * @throws IOException if close fails
     */
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    /**
     * @param input the input from a server's socket response for MEC data requests
     * @return the MecItem sent by the server
     * @throws IllegalArgumentException when error is encountered with parsing
     */
    private static MecItem parse(String input) throws IOException{
        String[] information = input.split(SPLIT_STRING);
        if (NewsItem.typeMatches(information[0])){
            return new NewsItem(information[1], information[2], information[3]);
        } else if (ScheduleItem.typeMatches(information[0])){
            return new NewsItem(information[1], information[2], information[3]);
        } else if (PointOfContact.typeMatches(information[0])){
            return new NewsItem(information[1], information[2], information[3]);
        } else {
            throw new IOException();
        }
    }
}
