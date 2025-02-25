package remote;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface CanvasClientInterface extends Remote{

	// Assign manager
	public void assignManager() throws RemoteException;

	// Get client name
	public String getName() throws RemoteException;

	// Set client name
	public void setName(String name) throws RemoteException;

	// Get manager
	public boolean isManager() throws RemoteException;

	// For Permission
	public void setPermission(boolean b) throws RemoteException;

	// For Permission
	public boolean hasPermission() throws RemoteException;

	// Update client list
	public void updateUserList(Set<CanvasClientInterface> clientsList) throws RemoteException;

	// Update canvas
	public void updateCanvas(CanvasMsgInterface message)throws RemoteException;
	
	// Kick user
	public void notifyKicked() throws RemoteException;

	// Close client UI
	public void closeUI()throws RemoteException;

	// For chat
	public void addChat(String message)throws RemoteException;

	// Get current image
	public byte[] getCurrentImage()throws RemoteException;

	// Draw Image
	public void drawImage(byte[] image)throws RemoteException;
	
	// Draw white board
	public void drawWhiteBoard(CanvasServerInterface server)throws RemoteException,IOException;

	// Notify that a new canvas has been opened
	public void newWhiteboard()throws RemoteException;

	// Notify that the file has been read
	public void sendNotification()throws RemoteException;

	// For chat send message to every one
	public void receiveMessage(String Name,String message)throws RemoteException;
	
	// Use to notify client
	public void receiveSystemMessage(String message) throws RemoteException;

	// When the server shuts down, notify and close the client
	public void serverShutdown() throws RemoteException;

	// Ask manager to approve
	public void askManagerApproval(CanvasClientInterface client) throws RemoteException;

}
