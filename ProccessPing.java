import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ProccessPing extends Thread {

    PingPong ping;

    public ProccessPing(PingPong ping) {
        this.ping = ping;
    }

    public void run() {
        if (ping.equals(Main.myPing))
            return;

        boolean found = false;
        for (Node peer : Main.peers) {
            if (peer.ping.equals(ping)) {
                peer.lastMessage = System.currentTimeMillis();
                peer.ping = ping;
                found = true;
            }
        }

        if (!found) {
            Node temp = new Node(ping, System.currentTimeMillis());
            Main.peers.add(temp);
            System.out.println("new node with ID " + ping.ID + " at " + ping.IP);
        }

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            byte[] data = Main.myPing.toBytes();

            DatagramPacket packet = new DatagramPacket(data, data.length,
                    InetAddress.getByName(ping.IP), ping.port);
            clientSocket.send(packet);

            data = ping.toBytes();
//            System.out.println(ping.toString());
            for (Node peer : Main.peers) {
                if (peer.ping.equals(ping))
                    continue;
                packet = new DatagramPacket(data, data.length, InetAddress.getByName(peer.ping.IP),
                        peer.ping.port);
                clientSocket.send(packet);
            }
        } catch (Exception e) {
            //System.err.println("ProccessPing Error: " + e.getMessage());
            //e.printStackTrace();
        }
    }
}
