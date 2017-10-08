/* Server app that will receive a request message from an HTTP client,
    will return a response messsage as well as the file requested.
    Joseph Medina
 */

import java.net.*;
import java.io.*;

public class HTTPServer
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverTCPSocket = null;
        boolean listening = true;

        try
        {
            serverTCPSocket = new ServerSocket(5090);
        }
        catch(IOException e)
        {
            System.err.println("Cannot listen on port 5090");
            System.exit(-1);
        }
        while(listening = true)
        {
            new HTTPMultiThreadServer(serverTCPSocket.accept()).start();
        }
            serverTCPSocket.close();
    }
}

