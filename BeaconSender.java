import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BeaconSender extends Thread {

    public BeaconSender() {
    }

    public void run() {
        while (true) {
            try (DatagramSocket clientSocket = new DatagramSocket()) {
                ArrayList<Node> toRemovePeers = new ArrayList<>();

                for (Node peer : Main.peers) {
                    if (System.currentTimeMillis() - peer.lastMessage > 10000) {
                        toRemovePeers.add(peer);
                        System.out.println("response timeout, dropping node " + peer.nodeID);
                    } else {
                        byte[] sendData = Main.myPing.toBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                                InetAddress.getByName(peer.ping.IP), peer.ping.port);
                        clientSocket.send(sendPacket);
                    }
                }
                if (toRemovePeers.size() > 0)
                    Main.peers.removeAll(toRemovePeers);

                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                //System.err.println("PingSender Error: " + e.getMessage());
                //e.printStackTrace();
            }
        }
    }
}