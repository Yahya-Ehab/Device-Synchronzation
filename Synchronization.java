import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
 }

public class Network {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

       
        System.out.print("Enter the max number of connections (N): ");
        int maxConnections = scanner.nextInt();

     
        System.out.print("Enter the total number of devices (TC): ");
        int totalDevices = scanner.nextInt();

        //Router router = new Router(maxConnections);

        for (int i = 0; i < totalDevices; i++) {
            System.out.print("Enter the name of device " + (i + 1) + ": ");
            String deviceName = scanner.next();

            System.out.print("Enter the type of device " + (i + 1) + ": ");
            String deviceType = scanner.next();

           // new Device(deviceName, deviceType, router).start();
        }

        scanner.close();
    }
}
