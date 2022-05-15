import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class PingListener extends Thread {

    public PingListener() {
    }

    public void run() {
        try (DatagramSocket serverSocket = new DatagramSocket(Main.myPing.port)) {

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                byte[] data = receivePacket.getData();
                if (receivePacket.getLength() == PingPong.BASE_PACKET_LENGTH) {
                    PingPong recievedPing = new PingPong(data);
                    ProccessPing pp = new ProccessPing(recievedPing);
                    pp.start();
                } else {
                    Query recievedQuery = new Query(data);
                    ProccessQuery pq = new ProccessQuery(recievedQuery);
                    pq.start();
                }
            }
        } catch (Exception e) {
            System.err.println("PingListener Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
