import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class QuerySender extends Thread {

    Query query;

    public QuerySender(Query query) {
        this.query = query;
        Main.servicedQueries.add(query.id);
    }

    public void run() {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            for (Node peer : Main.peers) {
                byte[] sendData = this.query.toBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        InetAddress.getByName(peer.ping.IP), peer.ping.port);
                clientSocket.send(sendPacket);
            }
        } catch (Exception e) {
            //System.err.println("QuerySender Error: " + e.getMessage());
            //e.printStackTrace();
        }

        try (ServerSocket serverSocket = new ServerSocket(query.requestPort)) {
            Socket socket = serverSocket.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            int dataLength = in.readInt();
            byte[] data = new byte[dataLength];
            in.read(data);
            FileOutputStream fos = new FileOutputStream(Main.dir + "output");
            fos.write(data);

            fos.close();
            in.close();

            System.out.println("Query finished, " + data.length + " bytes written to " + Main.dir + "output");
        } catch (Exception e) {
            System.err.println("QuerySender Socket Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}