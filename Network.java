import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Device implements Runnable {
    private String name;
    private String type;
    private Router router;
    private int connectionNumber;

    public Device(String name, String type, Router router, int connectionNumber) {
        this.name = name;
        this.type = type;
        this.router = router;
        this.connectionNumber = connectionNumber;
    }

    public int getConnectionNumber() {
        return connectionNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        try {
            System.out.println("- (" + name + ")(" + type + ") arrived");

            while (router.isConnectionOccupied(name)) {
                System.out.println("- (" + name + ")(" + type + ") waiting for connection");
                Thread.sleep(1000);
            }

            router.occupyConnection(name);

            System.out.println("- Connection " + connectionNumber + ": " + name + " login");
            // Simulate online activity
            Thread.sleep((long) (Math.random() * 1000));
            System.out.println("- Connection " + connectionNumber + ": " + name + " performs online activity");

            System.out.println("- Connection " + connectionNumber + ": " + name + " Logged out");
            router.releaseConnection(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Semaphore {
    protected int value = 0;

    protected Semaphore() {
        value = 0;
    }

    protected Semaphore(int initial) {
        value = initial;
    }

    public synchronized void acquire() throws InterruptedException {
        while (value == 0) {
            wait();
        }
        value--;
    }

    public synchronized void release() {
        value++;
        notify();
    }
     public synchronized int availablePermits() {
        return value;
    }
}
 class Router{
    private List<String> activeConnections = new ArrayList<>();
    private Semaphore semaphore;
   Router(int max_connection){
        semaphore=new Semaphore(max_connection);
  }
  public void occupyConnection(String deviceName) throws InterruptedException {
       semaphore.acquire();
       activeConnections.add(deviceName);
      System.out.println(deviceName + "  Occupied.");
  }
    public void releaseConnection(String deviceName){
        activeConnections.remove(deviceName);
        semaphore.release();
        System.out.println(deviceName + " released the connection.");
    }
    public boolean isConnectionOccupied(String deviceName){
        return activeConnections.contains(deviceName);
    }

    public boolean isAnyConnectionOccupied(){
        return activeConnections.size() > 0;
    }
    public int getActiveConnectionsCount(){
        return activeConnections.size();
    }
    public int getMaxConnections(){
        return semaphore.availablePermits();
    }
 }

public class Network {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

       
        System.out.print("Enter the max number of connections (N): ");
        int maxConnections = scanner.nextInt();

     
        System.out.print("Enter the total number of devices (TC): ");
        int totalDevices = scanner.nextInt();

        Router router = new Router(maxConnections);

       

        Device[] devices = new Device[totalDevices];
        // for (int i = 0; i < totalDevices; i++) {
        //     System.out.print("Enter the name of device " + (i + 1) + ": ");
        //     String deviceName = scanner.next();

        //     System.out.print("Enter the type of device " + (i + 1) + ": ");
        //     String deviceType = scanner.next();
        //     devices[i] = new Device(deviceName, deviceType, router, i+1);
        // }
        devices[0] = new Device("C1", "mobile", router, 1);
        devices[1] = new Device("C2", "tablet", router, 2);
        devices[2] = new Device("C3", "pc", router, 3);
        devices[3] = new Device("C4", "pc", router, 4);
        Thread[] threads = new Thread[totalDevices];
        for (int i = 0; i < totalDevices; i++) {
            threads[i] = new Thread(devices[i]);
            threads[i].start();
        }

        scanner.close();
    }
}
