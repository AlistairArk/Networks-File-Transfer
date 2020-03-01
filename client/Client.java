import java.io.*;
import java.net.*;



public class Client {
    // Accept one of the follow commands as command line arguments, and performs the stated


    public void list() {

        try{

            Socket socket=new Socket("localhost",8888);     // establish connection
            
            // chain a writing & reading stream
            PrintWriter socketOutput = new PrintWriter(socket.getOutputStream(), true); 
            BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            socketOutput.println("list");               // send command to server

            String fileList = socketInput.readLine();   // get list of files
            fileList = fileList.replace("*","\n");      // add newlines to output
            System.out.println(fileList);               // print file list
            socket.close();

        } catch (IOException e) {
            System.err.println("I/O exception during execution\n");
            System.err.println(e);
            System.exit(1);
        }

    }






    public void put(String filename) {
        /*  
        – put, sends the filename from the client’s local folder 'clientFiles' and
                sends it to the server (to be placed in 'serverFiles').
        */
        try{

            File file = new File("clientFiles\\"+ filename);
            if(file.exists() && !file.isDirectory()) { 

                Socket socket = null;
                socket = new Socket("localhost", 8888);     // establish connection

                // chain a writing & reading stream
                PrintWriter socketOutput = new PrintWriter(socket.getOutputStream(), true);  
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream())); 

                socketOutput.println("put");        // send command to server
                socketOutput.println(filename);     // send filename to server

                // Get the size of the file
                long length = file.length();
                byte[] bytes = new byte[16 * 1024];
                InputStream in = new FileInputStream(file);
                OutputStream out = socket.getOutputStream();

                int i;
                while ((i = in.read(bytes)) > 0) {
                    out.write(bytes, 0, i);
                }

                out.close();
                in.close();
                socket.close();
            }else{
                System.out.println("Error: File '"+filename+"' not found in clientFiles");
            }

        } catch (IOException e) {
            System.err.println("I/O exception during execution\n");
            System.err.println(e);
            System.exit(1);
        }



    }









    public void get(String filename) {
        /*
        - get, Requests the server send the file 'filename', and save
                it to the client’s local folder 'clientFiles'.
        */

        try{

            Socket socket = null;
            socket = new Socket("localhost", 8888);
    
            // chain a writing & reading stream
            PrintWriter socketOutput = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            socketOutput.println("get");        // command
            socketOutput.println(filename);     // filename

            // check if file 'filename' exists on server
            String fileExists = socketInput.readLine();
            if (fileExists.equals("t")){
                InputStream in = null;
                OutputStream out = null;


                try {
                    in = socket.getInputStream();
                } catch (IOException ex) {
                    System.out.println("Can't get socket input stream. ");
                }

                try {
                    out = new FileOutputStream("clientFiles\\"+filename);
                } catch (FileNotFoundException ex) {
                    System.out.println("File not found. ");
                }


                byte[] bytes = new byte[16*1024];
                int i;
                while ((i = in.read(bytes)) > 0) {
                    out.write(bytes, 0, i);
                }

                out.close();
                in.close();
                socket.close();
            }else if (fileExists.equals("f")){
                System.out.println("Error: File '"+filename+"' not found in serverFiles");
            }else{
                System.out.println("Sever Error: Failed to determine existence of file");
            }

        } catch (IOException e) {
            System.err.println("I/O exception during execution\n");
            System.err.println(e);
            System.exit(1);
        }

    }



















    public static void main( String[] args )    {
        Client run = new Client();

        int argLen = args.length;


        // check to ensure correct number of arguments are supplied to the program
        if (argLen==1){ 
            if(args[0].equals("list")){             // Run "list" command
                run.list();
            }else{
                System.out.println("Error: Invalid command");
            }



        }else if(argLen==2){
            if (args[0].equals("get")){             // Run "get fname" command
                run.get(args[1]);
            }else if (args[0].equals("put")){       // Run "put fname" command
                run.put(args[1]);
            }else{
                System.out.println("Error: Invalid command");
            }

        }else if(argLen==0){
            System.out.println("Error: No command supplied.");
            System.out.println("\nPlease ensure you are supplying command line arguments in the following manner when executing the program: ");
            System.out.println("  - java Client list");
            System.out.println("  - java Client put <filename>");
            System.out.println("  - java Client get <filename>");
        }else{
            System.out.println("Error: Invalid number of arguments supplied");
        }

        // • Exits after completing each command.
    }
}



