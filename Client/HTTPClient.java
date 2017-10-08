/* Client app that will send a request for an object to
    a server and will await a response from the server. 

    Joseph Medina */

import java.io.*;
import java.net.*;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class HTTPClient
{
    public String methodType, fileName, version, userAgent;
    public static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws IOException
    {
        String serverName;
        Socket TCPSocket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;

        System.out.println("Enter the DNS name or IP address of the server: ");
        serverName = input.nextLine();

        long startTime = new GregorianCalendar().getTimeInMillis();
        String testStatus;

        try
        {
            TCPSocket = new Socket(serverName, 5090);
            socketOut = new PrintWriter(TCPSocket.getOutputStream(), true);
            socketIn = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));
            testStatus = socketIn.readLine();
            System.out.println(testStatus);
        } catch (UnknownHostException e)
        {
            System.err.println("Unable to connect to host: " + serverName);
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("Unable to establish I/O to:" + serverName);
            System.exit(1);
        }

        long endTime = new GregorianCalendar().getTimeInMillis();

        System.out.println("RTT of establishing connection: " + (endTime - startTime + "ms"));
        HTTPClient httpClient = new HTTPClient();
        httpClient.getHeaderInput();
        String continueConnection = "Yes";

        while(continueConnection.equalsIgnoreCase("Yes"))
        {
            String requestMessage = httpClient.methodType + " /" + httpClient.fileName + " HTTP/"
                    + httpClient.version + "\r\n" + "Host: " + serverName + "\r\n"
                    + "User-Agent: " + httpClient.userAgent + "\r\n" + "\n";

            System.out.println();
            System.out.println(requestMessage);

            startTime = new GregorianCalendar().getTimeInMillis();
            socketOut.println(requestMessage);

            String fromServer, returnStatus;
            int count = 0;

                fromServer = socketIn.readLine();
                System.out.println("Response Message from Server: ");
                System.out.println(fromServer); //Print this line from the socket.

                //The read line should be the status line...
                String[] parseResponse = new String[1000]; //Construct an array to parse the status line.
                parseResponse = fromServer.split("\\s+"); //Break up the status line into pieces.
                returnStatus = parseResponse[1]; // Index 0: HTTPVersion, Index 1: status code, Index 2: status phrase

                if (returnStatus.equals("200"))
                {
                    fromServer = socketIn.readLine();
                    System.out.println(fromServer);
                    fromServer = socketIn.readLine();
                    System.out.println(fromServer);
                    fromServer = socketIn.readLine();

                    if (fromServer.isEmpty())
                    {
                        PrintWriter writer = new PrintWriter(httpClient.fileName, "UTF-8");

                        while ((fromServer = socketIn.readLine()) != null)
                        {
                            if (fromServer.isEmpty())
                            {
                                count++;
                                writer.println(fromServer);
                            } else
                            {
                                count = 0;
                                writer.println(fromServer);
                            }

                            if (count == 4)
                            {
                                writer.close();
                                break;
                            }
                        }
                    }
                }

                endTime = new GregorianCalendar().getTimeInMillis();
                System.out.println("RTT = " + (endTime - startTime + "ms"));

                System.out.println("Would you like to continue?(Yes/No): ");
                continueConnection = input.nextLine();

                if (continueConnection.equalsIgnoreCase("yes"))
                {
                    httpClient.getHeaderInput();
                    socketIn = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));
                } else if (continueConnection.equalsIgnoreCase("no"))
                {
                    socketOut.println("Client Disconnected.");
                    socketOut.close();
                    socketIn.close();
                    TCPSocket.close();
                }
            }
        }

    public void getHeaderInput()
    {
        //Obtain the method type from the user, GET/HEAD/POST/OPTIONS
        System.out.println("Enter the HTTP Method type:");
        methodType = input.nextLine();
        methodType = methodType.toUpperCase();
        //Implement some error checking on what can be entered here.

        //Obtain the file name that we want to request from the server.
        System.out.println("Enter the requested file name(include ext):");
        fileName = input.nextLine();

        //Obtain the HTTP Version from the user.
        System.out.println("Enter the HTTP Version:");
        System.out.println("Acceptable inputs are: 1.0, 1.1, or 2.0");
        version = input.nextLine();

        //Obtain the user-agent from the user.
        System.out.println("Enter the user-agent:");
        userAgent = input.nextLine();
    }
}
