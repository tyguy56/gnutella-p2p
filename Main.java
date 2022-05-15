import java.util.ArrayList;
import java.io.*;
import java.net.*;
import java.util.Random;

public class Main {

    public final static short DEFAULT_PORT = 5050;
    public static String DEFAULT_DIR = System.getenv("HOME") + "/cs487-hw4/";

    public static void main(String[] args) throws Exception {
        Main g = new Main();

        // PARSER
        for (int i = 0; i < args.length; ++i) {
            try {
                switch (args[i++]) {
                    case "--port":
                        g.setPort(Integer.parseInt(args[i]));
                        break;
                    case "--connect":
                        g.connect(args[i]);
                        break;
                    case "--query":
                        g.addQuery(args[i], Long.parseLong(args[++i]));
                        break;
                    case "--dir":
                        g.setDir(args[i]);
                        System.out.println("set dir to " + dir);
                        break;
                    default:
                        System.err.println("Invalid argument: " + args[i - 1]);
                        return;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Invalid arguments");
            }
        }

        g.start();
    }

    // GLOBALS
    public static ArrayList<Node> peers = new ArrayList<>();
    public static ArrayList<Integer> servicedQueries = new ArrayList<>();
    public static ArrayList<File> files = new ArrayList<>();
    public static ArrayList<Query> queries = new ArrayList<>();

    public static String dir = DEFAULT_DIR;
    public static PingPong myPing;
    Random rand = new Random();
    int id = rand.nextInt(1000) + 1;

    // INITIATE
    Main() {
        System.out.println("NODE ID:" + id);
        System.out.println("------------");
        Main.myPing = new PingPong(DEFAULT_PORT, getLocalIP(), 0, 0, id);
    }

    public void setPort(int port) {
        myPing.port = (short) port;
    }

    // CONNECTION
    public void connect(String addr) {
        String[] split = addr.split(":", 2);
        String ip = split[0];
        if (ip.equalsIgnoreCase("localhost") || ip.equalsIgnoreCase("127.0.0.1")) {
            ip = getLocalIP();
        }
        short connectPort = DEFAULT_PORT;
        if (split.length > 1)
            connectPort = Short.parseShort(split[1]);

        peers.add(new Node(new PingPong(connectPort, ip, 0, 0, id), System.currentTimeMillis()));
    }

    public void addQuery(String search, long timeToLive) {
        System.out.println("adding query for " + search);
        queries.add(new Query(myPing.IP, (short) (myPing.port + queries.size() + 1), search, timeToLive));
    }

    public void setDir(String dir) {
        if (!dir.endsWith("/"))
            dir += "/";
        Main.dir = dir;
    }

    // THREAD MANAGER
    public void start() {
        PingListener pingListener = new PingListener();
        BeaconSender pingSender = new BeaconSender();
        FileSystem fileSystem = new FileSystem(Main.dir);
        ArrayList<QuerySender> querySenders = new ArrayList<>();
        for (Query query : queries) {
            querySenders.add(new QuerySender(query));
        }

        fileSystem.start();
        pingListener.start();
        pingSender.start();

        for (QuerySender querySender : querySenders) {
            querySender.start();
        }
    }

    public static String getLocalIP() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 8080);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            //System.err.println("Error getting local IP: " + e.getMessage());
           // e.printStackTrace();
        }

        return null;
    }
}