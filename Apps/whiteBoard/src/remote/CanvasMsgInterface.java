package remote;

import java.awt.Color;
import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CanvasMsgInterface extends Remote{
	
	public String getName() throws RemoteException;
	
	public String getState() throws RemoteException;

	public Color getColor() throws RemoteException;
	
	public String getMode() throws RemoteException;
	
	public String getText() throws RemoteException;
	
	public Point getPoint() throws RemoteException;
	
	public int getPenSize() throws RemoteException;
	
	public void setPenSize(int penSize) throws RemoteException;
	
	public int getEraserSize() throws RemoteException;
	
	public void setEraserSize(int eraserSize) throws RemoteException;

}
