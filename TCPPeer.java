import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPPeer extends Thread{
    private Socket s;

    public TCPPeer(String host, int port, String file) {
        try {
            s = new Socket(host, port);
            sendFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ServerSocket ss;

    public TCPPeer(int port) {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //cilent stuff
    public void sendFile(String file) throws IOException {
        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];

        while (fis.read(buffer) > 0) {
            out.write(buffer);
        }

        fis.close();
        out.close();
        s.close();
    }

    //server stuff
    public void run() {
        while (true) {
            try {
                Socket clientSock = ss.accept();
                saveFile(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(Socket clientSock) throws IOException {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        FileOutputStream fos = new FileOutputStream("testfile.mp3");
        byte[] buffer = new byte[4096];

        int count;
        while ((count = dis.read(buffer)) > 0) {
            fos.write(buffer, 0, count);
        }

        fos.close();
        dis.close();
    }
/*
    public static void main(boolean isServer) {
        if(isServer){
            TCPPeer fs = new TCPPeer(1988);
            fs.start();
        }
        else{
            TCPPeer fc = new TCPPeer("localhost", 1988, "file.mp3");
        }
    }*/
public static void main(String[] args) {
    Thread[] threadPool = new Thread[50];

    Scanner input = new Scanner(System.in);
    System.out.printf("Enter Server/Client for config");
    String string = input.toString();
    int sockNum = 5555+(int)Thread.currentThread().getId();
    if (string == "Server"){
        for (int i =0; i<threadPool.length; i++){
            threadPool[i] = new Thread(new TCPPeer(sockNum));
            threadPool[i].start();
        }
    }else if (string =="Client"){
        for (int i =0; i<threadPool.length; i++){
            threadPool[i] = new Thread(new TCPPeer("10.100.50.120",sockNum,"testfile.mp3"));
            threadPool[i].start();
        }
    }
}
}
