
import java.net.*;
import java.io.*;
import java.util.*;


import java.util.Calendar;
import java.text.SimpleDateFormat;

public class ClientHandler extends Thread {
    private Socket socket = null;
    public static final String dateTimeFormant = "dd/MM/yyyy : HH:mm:ss";

    public ClientHandler(Socket socket) {
        super("ClientHandler");
        this.socket = socket;
    }


    public void Logger(String logData){
        try{
            // append to end of file
            FileWriter fw = new FileWriter("log.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(logData+"\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InetAddress inet = socket.getInetAddress();

            // Get date-time
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormant);
            String dateTime = sdf.format(cal.getTime());


            // Request to be logged
            String log = dateTime+" : "+inet.getHostName()+" :";


            String command = in.readLine();     // get request
            log+=" "+command;                   // add request to log





            if (command.equals("list")){

                String fileList = "*=== Server Files ===";

                // Get list of files in dir
                File folder = new File("serverFiles");
                File[] listOfFiles = folder.listFiles();

                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        fileList += "*   " + listOfFiles[i].getName();  // add filename to list
                    } else if (listOfFiles[i].isDirectory()) {
                        fileList += "* ~ " + listOfFiles[i].getName();  // add directory name to list
                    }
                }

                out.println(fileList);  // return file list to client




            }else if (command.equals("get")){

                String filename = in.readLine();     // Get Filename
                log+=" "+filename;                   // Add name to log


                File file = new File("serverFiles\\"+filename);
                if(file.exists() && !file.isDirectory()) { 
                    out.println("t");                // Return file found flag

                    // // get the size of the file
                    // long length = file.length();
                    byte[] bytes = new byte[16 * 1024];
                    InputStream is = new FileInputStream(file);
                    OutputStream os = socket.getOutputStream();

                    int count;
                    while ((count = is.read(bytes)) > 0) {
                        os.write(bytes, 0, count);
                    }

                    os.close();
                    is.close();

                }else{
                    out.println("f");                 // Return file not found flag
                }
                
                socket.close();





            }else if (command.equals("put")){
                String filename = in.readLine();    // get command
                log+=" "+filename;                  // add name to log

                InputStream is = null;
                OutputStream os = null;


                try {
                    is = socket.getInputStream();
                } catch (IOException ex) {
                    System.out.println("Can't get socket input stream. ");
                }

                try {
                    os = new FileOutputStream("serverFiles\\"+ filename);
                } catch (FileNotFoundException ex) {
                    System.out.println("File not found. ");
                }


                byte[] bytes = new byte[16*1024];
                int count;
                while ((count = is.read(bytes)) > 0) {
                    os.write(bytes, 0, count);
                }

                os.close();
                is.close();
                socket.close();
            }




            System.out.println(log);    // print log serverside
            Logger(log);                // output info to log

            // close
            out.close();
            in.close();


        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);

        }

    }   
}













