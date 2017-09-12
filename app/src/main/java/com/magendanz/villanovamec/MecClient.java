package com.magendanz.villanovamec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Client for getting and setting news and calendar items from the server. All responses and
 * requests are semicolon separated sequences of strings
 */

public class MecClient {

    private static final String SPLIT_STRING = ";";

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Rep invariant: socket, in, out != null

    /**
     * Make a SquareClient and connect it to a server running on
     * hostname at the specified port.
     * @throws IOException if can't load default file
     */
    public MecClient(String hostname, int port, int maxWaitTime) throws IOException {
        socket = new Socket(hostname, port);
        socket.setSoTimeout(maxWaitTime);
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
     * @return an array of all of the MecItems sent by the server or null in the place of an
     *          invalid response
     * @throws IOException if network or server failure or bad message formatting
     */
    public MecItem[] getReply() throws IOException, SocketTimeoutException {
        try {
            String reply = in.readLine();
            int entries = Integer.parseInt(reply);
            return getResponses(in, entries);
        } catch (NumberFormatException e){
            System.err.println("Invalid response format");
            throw new IOException();
        }
    }

    /**
     * @param inReader the BufferedReader being used to read the response
     * @param entries the number of entries in the message
     * @return all of the MecItems from the response
     * @throws IOException
     */
    public static MecItem[] getResponses(BufferedReader inReader, int entries) throws IOException{
        MecItem[] responses = new MecItem[entries];
        for (int i = 0; i < entries; i++){
            try {
                String reply = inReader.readLine();
                responses[i] = parse(reply);
            } catch (IllegalArgumentException | SocketTimeoutException e){
                responses[i] = null;
                System.err.println("Response cannot be parsed.");
            }
        }
        return responses;
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
    private static MecItem parse(String input) throws IllegalArgumentException{
        String[] information = input.split(SPLIT_STRING);
        if (NewsItem.typeMatches(information[0])){
            return new NewsItem(information);
        } else if (ScheduleItem.typeMatches(information[0])){
            return new ScheduleItem(information);
        } else if (PointOfContact.typeMatches(information[0])){
            return new PointOfContact(information);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
