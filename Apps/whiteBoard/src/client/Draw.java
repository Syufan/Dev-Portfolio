package client;

import java.awt.Color;
import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import remote.CanvasMsgInterface;

public class Draw extends UnicastRemoteObject implements CanvasMsgInterface{

	private static final long serialVersionUID = 1L;
	
	private String state;
	
	private String clientName;
	
	private String mode;
	
	private Color color;
	
	private Point point;
	
	private String text;
	
	private int penSize;
	
	private int eraserSize;
	
	
	public Draw(String state, String name, String mode, Color color, Point pt, String text, int penSize, int eraserSize) throws RemoteException {
	    this.state = state;
	    this.clientName = name;
	    this.mode = mode;
	    this.color = color;
	    this.point = pt;
	    this.text = text;
	    this.penSize = penSize;
	    this.eraserSize = eraserSize;
	}

	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return this.clientName;
	}

	@Override
	public String getState() throws RemoteException {
		// TODO Auto-generated method stub
		return this.state;
	}

	@Override
	public Color getColor() throws RemoteException {
		// TODO Auto-generated method stub
		return this.color;
	}

	@Override
	public String getMode() throws RemoteException {
		// TODO Auto-generated method stub
		return this.mode;
	}

	@Override
	public String getText() throws RemoteException {
		// TODO Auto-generated method stub
		return this.text;
	}

	@Override
	public Point getPoint() throws RemoteException {
		// TODO Auto-generated method stub
		return this.point;
	}

	@Override
	public int getPenSize() throws RemoteException {
	    return penSize;
	}

	@Override
	public void setPenSize(int penSize) throws RemoteException {
	    this.penSize = penSize;
	}

	@Override
	public int getEraserSize() throws RemoteException {
		// TODO Auto-generated method stub
		return eraserSize;
	}

	@Override
	public void setEraserSize(int eraserSize) throws RemoteException {
		// TODO Auto-generated method stub
		this.eraserSize = eraserSize;
	}
	


}
