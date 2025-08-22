package remote;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.Set;

public interface CanvasServerInterface extends Remote{
	
	// ---Client Registration & Lifecycle--- //
	// register user name manager
	public void registerClient(CanvasClientInterface client) throws RemoteException;
	
	// Allow user to join
	public void approveUserJoin(CanvasClientInterface client) throws RemoteException;
	
	// client disconnects, remove the client's remote reference from the server
	public void unregisterClient(String clientName) throws RemoteException;
	
	// Client exit
	public void exit(String name) throws RemoteException;

	//advanced method
	public void kickOutUser(String clientName) throws RemoteException;
	
	// Get client list
	public Set<CanvasClientInterface> getClientList() throws RemoteException;
	
	// Update client list
	public void updateClientlist() throws RemoteException;

	// ---Canvas Broadcast & Sync--- //
	// Board cast canvas
	public void broadCastCanvas(CanvasMsgInterface message) throws RemoteException;
	
	// Get current message
	public byte[] getCurrentImage() throws RemoteException, IOException;
	
	// Administrator opens a new drawing board file
	public void sendOpenedImage(byte[] rawImage) throws IOException;
	
	// Open white board
	public void openWhiteboard(byte[] imageData) throws RemoteException;
	
	// Create new white board
	public void newWhiteboard() throws RemoteException;
	
	// Close white board and remove all user
	public void closeWhiteboard() throws RemoteException;

	// ---Messaging--- //
	// Send chat message
	public void addChatMsg(String message) throws RemoteException;

	// Board cast message
	public void broadcastMessage(String Name,String message) throws RemoteException;
	
	// ---Shutdown--- //
	// Notify shutdown
	public void notifyServerShutdown() throws RemoteException;
	
}
