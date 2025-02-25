package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.JOptionPane;
import remote.CanvasServerInterface;

public class StartServer {

	private static Thread shutdownHook;

    public static void main(String[] args) {
    	
        if (args.length != 1) {
            System.err.println("Usage: java StartServer <port_number>");
            return;
        }

        int port;
        
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[0]);
            return;
        }
        
        if (port < 1024 || port > 65535) {
            System.err.println("Invalid port number: " + port);
            System.err.println("Please enter a port number between 1024 and 65535.");
            return;
        }

        
        try {
        	CanvasServerInterface server = new CanvasServer();
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("CanvasServer", server);
            JOptionPane.showMessageDialog(null, "Canvas server is ready on port " + port);
            System.out.println("Canvas Server is ready on port " + port);
            
            
            shutdownHook = new Thread(() -> {
                try {
                	server.notifyServerShutdown();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });

            Runtime.getRuntime().addShutdownHook(shutdownHook);
            
        } catch (Exception e) {
            System.err.println("Server setup failed on port " + port);
            e.printStackTrace();
        }
    }
}

