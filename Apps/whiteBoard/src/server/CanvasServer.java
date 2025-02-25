package server;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import javax.swing.JOptionPane;
import remote.CanvasClientInterface;
import remote.CanvasMsgInterface;
import remote.CanvasServerInterface;

public class CanvasServer extends UnicastRemoteObject implements CanvasServerInterface, Serializable{

	private Manager manager;
	
	protected CanvasServer() throws RemoteException {
		this.manager = new Manager(this);
	}

	private static final long serialVersionUID = 1L;
	
	// Register client
	@Override
	public void registerClient(CanvasClientInterface client) throws RemoteException {
		// TODO Auto-generated method stub
		// Check if the client is already registered
		if (this.manager.getClientsList().contains(client)) {
			throw new IllegalStateException("Client already registered: " + client);
	    }
		
	    // If the client list is empty, assign the first client as the manager
	    if (this.manager.isClientListEmpty()) {
	        client.assignManager();
	        client.setName("*" + client.getName()); 
	        this.manager.addClient(client);
	        client.setPermission(true); 
	    } else {
	        // If the client list is not empty, request approval from the manager
	    	 try {
	             approveUserJoin(client);

	         } catch (IllegalStateException | IOException e) {
	             System.out.println(e.getMessage());
	         }
	    }
	    // Update the user list for all connected clients
	    updateClientlist();
	    for (CanvasClientInterface c : manager.getClientsList()) {
	        c.receiveSystemMessage("System Message: " + client.getName() + " joined");
	    }
	}
	
	// Get client list
	@Override
	public Set<CanvasClientInterface> getClientList() throws RemoteException {
		// TODO Auto-generated method stub
		return manager.getClientsList();
	}
	
	// Un-regrister client
	@Override
	public void unregisterClient(String clientName) throws RemoteException{
		// TODO Auto-generated method stub
		CanvasClientInterface clientToKick = null;
	    
	    // Find the client with the given name
	    for (CanvasClientInterface client : this.manager.getClientsList()) {
	        if (client.getName().equals(clientName)) {
	            clientToKick = client;
	            break;
	        }
	    }
	    
	    if (clientToKick != null) {
	        // Remove the client from the manager's client list
	        this.manager.removeClient(clientToKick);
	        
	        // Update the user list for all connected clients
	        updateClientlist();
		    for (CanvasClientInterface c : manager.getClientsList()) {
		        c.receiveSystemMessage("System Message: " + clientName + " left");
		    }
	        
	    } else {
	        System.out.println("Client not found: " + clientName);
	    }
		
	}
	
	// Board cast canvas
	@Override
	public void broadCastCanvas(CanvasMsgInterface message) throws RemoteException {
		// TODO Auto-generated method stub
	    for (CanvasClientInterface client : this.manager.getClientsList()) {
	        client.updateCanvas(message);
	    }
	}

	// Get current message
	@Override
	public byte[] getCurrentImage() throws RemoteException, IOException {
		// TODO Auto-generated method stub
		byte[] currentImage = null;
	    for (CanvasClientInterface connectedClient : this.manager.getClientsList()) {
	    	if(connectedClient.isManager()) {
	    		currentImage = connectedClient.getCurrentImage();
	    	}
	    }
		return currentImage;
	}

	// Administrator opens a new drawing board file
	@Override
	public void sendOpenedImage(byte[] Image) throws IOException {
		// TODO Auto-generated method stub
	    for (CanvasClientInterface connectedClient : this.manager.getClientsList()) {
	    	if(connectedClient.isManager() == false) {
	    		connectedClient.drawImage(Image);
	    	}
	    }
	}

	// Send chat message
	@Override
	public void addChatMsg(String message) throws RemoteException {
		// TODO Auto-generated method stub
	    for (CanvasClientInterface connectedClient : this.manager.getClientsList()) {
	    	try {
	    		connectedClient.addChat(message);
	    	}catch(Exception e) {
	    	}
	    }
	}

	// Update client list
	@Override
	public void updateClientlist() throws RemoteException {
		// TODO Auto-generated method stub
	    for (CanvasClientInterface connectedClient : this.manager.getClientsList()) {
	        connectedClient.updateUserList(this.manager.getClientsList());
	    }
	}

	// Kick out user
	@Override
	public void kickOutUser(String clientName) throws RemoteException {
		// TODO Auto-generated method stub
		CanvasClientInterface clientToKick = null;
	    
	    // Find the client with the given name
	    for (CanvasClientInterface client : this.manager.getClientsList()) {
	        if (client.getName().equals(clientName)) {
	            clientToKick = client;
	            break;
	        }
	    }
	    
	    if (clientToKick != null) {
	        // Set the client's permission to false
	        clientToKick.setPermission(false);
	        
	        // Remove the client from the manager's client list
	        this.manager.removeClient(clientToKick);
	        
	        clientToKick.notifyKicked();
	        
	        // Update the user list for all connected clients
	        updateClientlist();
	    } else {
	        System.out.println("Client not found: " + clientName);
	    }
	}

	// Open white board
	@Override
	public void openWhiteboard(byte[] imageData) throws RemoteException{
		// TODO Auto-generated method stub
	    for (CanvasClientInterface connectedClient : this.manager.getClientsList()) {
	    	connectedClient.drawImage(imageData);
	    	connectedClient.sendNotification();
	    }
	}

	// Allow user to join
	@Override
	public void approveUserJoin(CanvasClientInterface client) throws RemoteException {
	    System.out.println("Approving user join for client: " + client.getName());
	    for (CanvasClientInterface c : this.manager.getClientsList()) {
	    	if(c.isManager()) {
	    		c.askManagerApproval(client);
	    	}
	    }
	    
	    if (client.hasPermission()) {
	        this.manager.addClient(client);
	    } else {
	        String message = "Manager did not approve you to join the whiteboard session.";
	        JOptionPane.showMessageDialog(null, message, "Permission Denied", JOptionPane.WARNING_MESSAGE);
	    }
	}

	// Close white board and remove all user
	@Override
	public void closeWhiteboard() throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Whiteboard colsed");
	    for (CanvasClientInterface connectedClient : this.manager.getClientsList()) {
	    	this.manager.removeClient(connectedClient);
	    	connectedClient.closeUI();
	    }
	}

	// Client exit
	@Override
	public void exit(String name) throws RemoteException {
		// TODO Auto-generated method stub
	    for (CanvasClientInterface connectedClient : this.manager.getClientsList()) {
	    	if(connectedClient.getName().equals(name)) {
	    		this.manager.removeClient(connectedClient);
	    		System.out.println(name + "quit the Canvas!");
	    	}
	    }
	    updateClientlist();
	}

	// Create new white board
	@Override
	public void newWhiteboard() throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("New Whiteboard");
	    for (CanvasClientInterface connectedClient : this.manager.getClientsList()) {
	    	connectedClient.newWhiteboard();
	    }
	}

	// Board cast message
	@Override
	public void broadcastMessage(String Name,String message) throws RemoteException {
		// TODO Auto-generated method stub
	    for (CanvasClientInterface client : this.manager.getClientsList()) {
	        try {
	            client.receiveMessage(Name,message); 
	        } catch (RemoteException e) {
	            System.out.println("Unable to send message to the client" + e.getMessage());
	        }
	    }
	}
	
	// Notify shutdown
	@Override
	public void notifyServerShutdown() throws RemoteException {
	    for (CanvasClientInterface client : manager.getClientsList()) {
	        client.serverShutdown();
	    }
	}
}
