package server;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import remote.CanvasClientInterface;

public class Manager implements Iterable<CanvasClientInterface>{
	
	// clientmanager 储存一个clients set 然后通过这个set去管理clients
	private Set<CanvasClientInterface> clientsList;
	
	public Manager(CanvasServer canvasServer) {
		// TODO Auto-generated constructor stub
		this.clientsList = Collections.newSetFromMap(new ConcurrentHashMap<CanvasClientInterface, Boolean>());
	}
	
    public void addClient(CanvasClientInterface client) {
        clientsList.add(client);
    }

    public void removeClient(CanvasClientInterface client) {
        clientsList.remove(client);
    }
    
    public Set<CanvasClientInterface> getClientsList(){
    	return this.clientsList;
    }
    
    public boolean isClientListEmpty() {
        return clientsList.isEmpty();
    }
	
	@Override
	public Iterator<CanvasClientInterface> iterator() {
		// TODO Auto-generated method stub
		return clientsList.iterator();
	}

}
