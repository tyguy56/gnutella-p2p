import java.io.DataOutputStream;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;

public class ProccessQuery extends Thread {

    Query query;

    public ProccessQuery(Query query) {
        this.query = query;
    }

    public void run() {
        if (System.currentTimeMillis() - query.timestamp >= query.timeToLive)
            return;
        if (Main.servicedQueries.contains(query.id))
            return;

        Main.servicedQueries.add(query.id);

        for (File file : Main.files) {
            if (query.searchString.equals(file.getName())) {
                try (Socket socket = new Socket(query.requestIP, query.requestPort)) {
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    out.writeInt(fileContent.length);
                    out.write(fileContent);
                    out.close();
                    socket.close();
                    System.out
                            .println("sending file " + fileContent.length + " bytes sent");
                } catch (Exception e) {
                    //System.err.println("ProccessQuery Socket Error: " + e.getMessage());
                    //e.printStackTrace();
                }
                return;
            }
        }
        // File not found
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            byte[] sendData = query.toBytes();

            for (Node peer : Main.peers) {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        InetAddress.getByName(peer.ping.IP), peer.ping.port);
                clientSocket.send(sendPacket);
            }
        } catch (Exception e) {
            System.err.println("ProccessQuery Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}