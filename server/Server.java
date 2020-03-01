

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Server {

    // Maximum number of threads in thread pool 
    static final int maxThreads = 10;            

    public static void main(String[] args) throws IOException {

        ServerSocket server = null;
        ExecutorService service = null;
        
        // Try to open up the listening port
        try {
            server = new ServerSocket(8888);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8888.");
            System.exit(-1);
        }


        // creates a thread pool with maxThreads no. of threads as the fixed pool size.
        ExecutorService pool = Executors.newFixedThreadPool(maxThreads);   
        
        // For each new client, submit a new handler to the thread pool.
        while( true ){
            Socket client = server.accept();
            pool.submit( new ClientHandler(client) );
        }
    }
}