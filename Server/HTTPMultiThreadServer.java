/* A second class to create a multithreaded HTTP server. This class
    will handled the multi-threading portion of running the HTTP server
    so we can accept multiple clients.
    Joseph Medina
 */

import javax.sound.midi.SysexMessage;
import java.nio.Buffer;
import java.util.*;
import java.text.*;
import java.io.*;
import java.net.*;

public class HTTPMultiThreadServer extends Thread
{
    private Socket clientTCPSocket = null;

    public HTTPMultiThreadServer(Socket socket)
    {
        super("HTTPMultiThreadServer");
        clientTCPSocket = socket;
    }

    public void run()
    {
        try
        {
            PrintWriter cSocketOut = new PrintWriter(clientTCPSocket.getOutputStream(), true);
            BufferedReader cSocketIn = new BufferedReader(new InputStreamReader(clientTCPSocket.getInputStream()));
            cSocketOut.println("220: Testing connecting..");
            String fromClient, toClient;
            String requestMethod, version, fileName, status;

            while((fromClient = cSocketIn.readLine()) != null)
            {
                //fromClient = cSocketIn.readLine(); //This reads the header line...
                if(fromClient.equals("Client Disconnected."))
                {
                    cSocketOut.close();
                    cSocketIn.close();
                    clientTCPSocket.close();
                    break;
                }
                System.out.println("Request Message from Client: \n");
                System.out.println(fromClient); //This prints the header line...

                //Create new parse array
                String[] parseRequest = new String[1000];
                parseRequest = fromClient.split("\\s+"); //This parses through the header line.

                requestMethod = parseRequest[0];
                fileName = parseRequest[1].replaceAll("/", "");
                version = parseRequest[2];

                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Calendar cal = Calendar.getInstance();


                fromClient = cSocketIn.readLine(); //This reads the next line, should be the hostline.
                System.out.println(fromClient); //Prints the host line.

                fromClient = cSocketIn.readLine(); //Reads the next line, should be the user-agent.
                System.out.println(fromClient); //Prints out the user-agent.

                //Begin constructing what we are sending back.
                toClient = null;

                //Check what the parsed request method was.
                if (requestMethod.equals("GET"))
                {
                    status = "200 OK";
                } else
                {
                    status = "400 BAD REQUEST";
                }

                if (status.equals("200 OK"))
                {
                    try
                    {
                        File file = new File(fileName);
                        FileReader fileReader = new FileReader(fileName);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        StringBuffer stringBuffer = new StringBuffer();
                        String line;

                        while ((line = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(line);
                            stringBuffer.append("\n");
                        }

                        fileReader.close();

                        toClient = version + " " + status + "\n" + "Date: " + dateFormat.format(cal.getTime()) + "\n"
                                + "Server: HTTP Server Networks Class" + "\n" + "\n" + stringBuffer + "\n" + "\n" + "\n" + "\n";
                        //System.out.println("Message we will send back to client: ");
                        //System.out.println(toClient);
                        cSocketOut.println(toClient);
                    }
                    catch (IOException e)
                    {
                        status = "404 FILE NOT FOUND";
                    }
                }
                else if (status.equals("400 BAD REQUEST"))
                {
                    cSocketOut.println(status);
                }

                if (status.equals("404 FILE NOT FOUND"))
                {
                    cSocketOut.println(status);
                }

                cSocketIn.readLine(); //Should read first blank line..

                //Check if reads another blank line.
                //If blank, close the connection, else restart while.
                if(cSocketIn.readLine().equals(null))
                {
                    cSocketOut.close();
                    cSocketIn.close();
                    clientTCPSocket.close();
                    break;
                }
            }
        }
        catch(IOException e)
        {
        e.printStackTrace();
        }

    }
}