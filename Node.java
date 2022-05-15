public class Node {
    PingPong ping;
    long lastMessage;
    int nodeID;

    public Node(PingPong ping, long lastMessage) {
        this.ping = ping;
        this.lastMessage = lastMessage;
        this.nodeID = ping.ID;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj.getClass() != this.getClass())
            return false;

        Node p = (Node) obj;

        return p.ping.equals(this.ping);
    }

    public String toString() {
        return "Ping: " + this.ping.toString() + "\nLast Message: " + this.lastMessage + "\nID: " + this.nodeID;
    }
}