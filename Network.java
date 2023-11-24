
import java.util.LinkedList;
import java.util.Queue;

class Device extends Thread {
    String name;
    String type;
    int connectionNumber;
    Router router;

    Device(String name, String type, Router router) {
        this.name = name;
        this.type = type;
        this.router = router;
    }

    public void setConnectionNumber(int number) {
        this.connectionNumber = number;
    }

    public int getConnectionNumber() {
        return this.connectionNumber;
    }

    @Override
    public void run() {
        try {
            router.connect(this);
            Thread.sleep((long) (Math.random() * 1000));
            System.out.println("Connection " + connectionNumber + ": " + name + " login"); 
            
            System.out.println("Connection " + connectionNumber + ": " + name + " Performs online activity");
            Thread.sleep((long) (Math.random() * 1000));
            router.release(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

   
}
class Semaphore {
    int maxConnections;
    int connectedNum;
    Queue<Integer> Connections = new LinkedList<>();

    Semaphore(int max) {
        this.maxConnections = max;
        connectedNum = 0;
        for(int i=1; i<=maxConnections; i++)
        {
            Connections.add(i);
        }
    }

    public synchronized boolean isPermit() {
        return connectedNum < maxConnections;
    }

    public synchronized void occupyConnection(Device device) throws InterruptedException {
        if(isPermit())
        {
            System.out.println("(" + device.name + ") " + "(" + device.type + ")" + " arrived");
        }
        else
        {
            System.out.println("(" + device.name + ") " + "(" + device.type + ")" + " arrived and waiting");
        }

        while (!isPermit()) 
        {

            wait();
        }
        connectedNum++;
        int connectionNum = Connections.poll();
        device.setConnectionNumber(connectionNum);
        System.out.println("Connection " + device.getConnectionNumber() + ": " + device.name + " Occupied");
    }

    public synchronized void releaseConnection(Device device) {
        connectedNum--;
        System.out.println("Connection " + device.getConnectionNumber() + ": " + device.name + " logged out");
        Connections.add(device.getConnectionNumber());
        notify();
    }
}
class Router {
    Semaphore semaphore;

    Router(int N) {
        semaphore = new Semaphore(N);
    }

    public void connect(Device device) throws InterruptedException {
        semaphore.occupyConnection(device);
    }

    public void release(Device device) {
        semaphore.releaseConnection(device);
    }
}


public class Network {
    public static void main(String[] args) {
        // Scanner scanner = new Scanner(System.in);

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

        // scanner.close();
    }
}






