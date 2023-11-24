import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



class Router {
    private int maxConnections;
    private List<String> waitingList;
    private List<String> activeConnections;
    private int connectionCounter = 0;

    Router(int maxConnections) {
        this.maxConnections = maxConnections;
        waitingList = new ArrayList<>();
        activeConnections = new ArrayList<>();
    }

    public synchronized void occupyConnection(String deviceName, String deviceType) throws InterruptedException {
        if (activeConnections.size() == maxConnections) {
            waitingList.add(deviceName);
            System.out.println("- (" + deviceName + ")(" + deviceType + ") arrived and waiting");
            synchronized (this) {
                wait(); // Put the thread to sleep until notified
            }
        } else {
             System.out.println("- (" + deviceName + ")(" + deviceType + ") arrived");
            connectionCounter++;
            activeConnections.add(deviceName);
            System.out.println("- Connection " + (connectionCounter % maxConnections + 1) + ": " + deviceName + " Occupied");
        }
    }

    public synchronized void releaseConnection(String deviceName) {
        activeConnections.remove(deviceName);
        System.out.println("- Connection " + (connectionCounter % maxConnections + 1) + ": " + deviceName + " Logged out");
        connectionCounter--;
        notifyNextDevice();
    }

    private void notifyNextDevice() {
        if (!waitingList.isEmpty()) {
            String nextDevice = waitingList.remove(0);
            System.out.println("- Notifying next device in line: " + nextDevice);
            synchronized (this) {
                notify();
            }
        }
    }
     public int getOccupiedConnectionsCount() {
        return activeConnections.size();
    }

    public int getMaxConnections() {
        return maxConnections;
    }
}

class Device implements Runnable {
    private String name;
    private String type;
    private Router router;

    Device(String name, String type, Router router) {
        this.name = name;
        this.type = type;
        this.router = router;
    }

    @Override
    public void run() {
        try {
            router.occupyConnection(name, type);

            System.out.println("- Connection " + (router.getOccupiedConnectionsCount() % router.getMaxConnections() + 1) + ": " + name + " login");
            // Simulate online activity
            Thread.sleep((long) (Math.random() * 1000));
            System.out.println("- Connection " + (router.getOccupiedConnectionsCount() % router.getMaxConnections() + 1) + ": " + name + " performs online activity");

            router.releaseConnection(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Network {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // System.out.print("What is the number of WI-FI Connections? ");
        int maxConnections = 2;//scanner.nextInt();

        // System.out.print("What is the number of devices Clients want to connect? ");
        int totalDevices = 4;//scanner.nextInt();

        Router router = new Router(maxConnections);

        Device[] devices = new Device[totalDevices];
        // for (int i = 0; i < totalDevices; i++) {
        //     System.out.print("Enter device name and type (e.g., C1 mobile): ");
        //     String name = scanner.next();
        //     String type = scanner.next();
        //     devices[i] = new Device(name, type, router);
        // }
        devices[0] = new Device("c1", "pc", router);
        devices[1] = new Device("c2", "", router);
        devices[2] = new Device("c3", "pc", router);
        devices[3] = new Device("c4", "pc", router);

        Thread[] threads = new Thread[totalDevices];
        for (int i = 0; i < totalDevices; i++) {
            threads[i] = new Thread(devices[i]);
            threads[i].start();
        }

        scanner.close();
    }
}