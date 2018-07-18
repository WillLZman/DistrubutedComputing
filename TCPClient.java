   import java.io.*;
   import java.net.*;

    public class TCPClient implements Runnable {
        public int getSockNum() {
            return sockNum;
        }

        public void setSockNum(int sockNum) {
            this.sockNum = sockNum;
        }

        public int sockNum = 5555;


        @Override
        public void run(){
            // Variables for setting up connection and communication
            Socket Socket = null; // socket to connect with ServerRouter
            PrintWriter out = null; // for writing to ServerRouter
            BufferedReader in = null; // for reading form ServerRouter
            InetAddress addr = null;
            try {
                addr = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            String host = addr.getHostAddress(); // Client machine's IP
            String routerName = "10.100.50.120"; // ServerRouter host name
            /**
             * this below is changed to for dynamic socket allocation
             */
            int SockNum = sockNum + (int)Thread.currentThread().getId(); // port number

            // Tries to connect to the ServerRouter
            try {
                Socket = new Socket(routerName, SockNum);
                out = new PrintWriter(Socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            }
            catch (UnknownHostException e) {
                System.err.println("Don't know about router: " + routerName);
                System.exit(1);
            }
            catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to: " + routerName);
                System.exit(1);
            }

            // Variables for message passing
            Reader reader = null;
            try {
                reader = new FileReader("John Stuart Mill; His Life and Works by H. R. Fox Bourne et al.rtf");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
            String fromServer = null; // messages received from ServerRouter
            String fromUser; // messages sent to ServerRouter
            String address ="10.80.22.222"; // destination IP (Server)
            String destSocket= "5566";
            long t0, t1, t;

            // Communication process (initial sends/receives
            out.println(address);// initial send (IP of the destination Server)
            out.println(destSocket); // initial send of socket destination
            try {
                fromServer = in.readLine();//initial receive from router (verification of connection)
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("ServerRouter: 1" + fromServer);
            out.println(host); // Client sends the IP of its machine as initial send
            t0 = System.currentTimeMillis();

            // Communication while loop
            try {
                while ((fromServer = in.readLine()) != null) {
                    System.out.println("Server: " + fromServer);
                    t1 = System.currentTimeMillis();
                    if (fromServer.equals("Bye.")) // exit statement
                        break;
                    t = t1 - t0;
                    System.out.println("Cycle time: " + t);

                    fromUser = fromFile.readLine(); // reading strings from a file
                    if (fromUser != null) {
                        System.out.println("Client: " + fromUser);
                        out.println(fromUser); // sending the strings to the Server via ServerRouter
                        t0 = System.currentTimeMillis();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // closing connections
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       public static void main(String[] args) throws IOException {
        Thread[] threadPool = new Thread[50];
        for (int i =0; i<threadPool.length; i++){
            threadPool[i] = new Thread(new TCPClient());
            threadPool[i].start();
            }
        }
}

