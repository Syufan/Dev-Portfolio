package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import remote.CanvasClientInterface;
import remote.CanvasMsgInterface;
import remote.CanvasServerInterface;

public class Client extends UnicastRemoteObject implements CanvasClientInterface, Remote{
	
	private DefaultListModel<String> userList;
	private JList<String> uList;
	private String clientName;
	protected static boolean isManager;
	private boolean permission;
	protected static CanvasServerInterface server;
	private Canvas canvas;
	private JTextArea chatArea;
	

	protected Client() throws RemoteException {
		super();
		userList = new DefaultListModel<>();
		isManager = false;
		permission = false;
		new DefaultListModel<>();
	}

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws RemoteException {
		try {
			//Look up the white board server from the RMI name registry
	        if (args.length != 2) {
	            System.err.println("Usage: java client.Client <serverIPAddress> <serverPort>");
	            return;
	        }
	        
	        String serverIPAddress = args[0];
	        int serverPort;
	        try {
	            serverPort = Integer.parseInt(args[1]);
	        } catch (NumberFormatException e) {
	            System.err.println("Invalid server port: " + args[1]);
	            return;
	        }
	        

	        String serverAddress = "rmi://" + serverIPAddress + ":" + serverPort + "/CanvasServer";
	        server = (CanvasServerInterface) Naming.lookup(serverAddress);

	        CanvasClientInterface client = new Client();

			// Non empty and Start with English words,user must enter a user name to join in 
	        String client_name = null;
	        boolean isNameValid = false;
	        Pattern pattern = Pattern.compile("^[a-zA-Z].*"); 
	        
	        while(!isNameValid) {
	        	client_name = JOptionPane.showInputDialog("Please enter your name:");
	        	
	        	if(client_name == null) {
	        		System.out.println("User cancelled the operation.");
		            return;
		            
	        	} else if (client_name.trim().isEmpty()) {
	                JOptionPane.showMessageDialog(null, "User name cannot be empty. Please enter a valid name.");
	            } else if (!pattern.matcher(client_name).matches()) {
	                JOptionPane.showMessageDialog(null, "User name must start with an English letter.");
	            } else {
	            	
	            	// check duplicated name
	            	boolean isDuplicated = false;
	            	if (server.getClientList() != null) {
	            	    for (CanvasClientInterface c : server.getClientList()) {
	            	        String serverClientName = c.getName();
	            	        if (serverClientName.startsWith("*")) {
	            	            serverClientName = serverClientName.substring(1);
	            	        }
	            	        if (serverClientName.equals(client_name)) {
	            	            isDuplicated = true;
	            	            break;
	            	        }
	            	    }
	            	}
	                
	                if (isDuplicated) {
	                    JOptionPane.showMessageDialog(null, "User name already exists. Please enter a different name.");
	                } else {
	                    isNameValid = true; 
	                }
	            }
	        }

	        client.setName(client_name);
	        try {
	            server.registerClient(client);
	            System.out.println("Registered with remote server");
	        } catch (RemoteException e) {
	            System.err.println("Error registering with remote server");
	            e.printStackTrace();
	        }
	        
	        if(client.hasPermission()) {
	        	client.drawWhiteBoard(server);
	        }else {
	        	System.out.println("Client has no permission, the client will not start.");
	        	System.exit(0);
	        }

		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	

	@Override
	public void drawWhiteBoard(CanvasServerInterface server) throws RemoteException,IOException {
	    
		// Create the main window, disable resizing, set a fixed size for the main window
	    JFrame frame = new JFrame(clientName + "'s WhiteBoard");
	    frame.setSize(1000, 600);
	    frame.setResizable(false);
	    Container content = frame.getContentPane();
	    content.setLayout(new BorderLayout());

	    if (this.isManager()) {
	    	
		    // Create a menu bar
		    JMenuBar menuBar = new JMenuBar();
		    JMenu fileMenu = new JMenu("File");
		    JMenuItem newMenuItem = new JMenuItem("New");
		    JMenuItem openMenuItem = new JMenuItem("Open");
		    JMenuItem saveMenuItem = new JMenuItem("Save");
		    JMenuItem saveAsMenuItem = new JMenuItem("Save As");
		    JMenuItem closeMenuItem = new JMenuItem("Close");

		    // Add menu item to file menu
		    fileMenu.add(newMenuItem);
		    fileMenu.add(openMenuItem);
		    fileMenu.add(saveMenuItem);
		    fileMenu.add(saveAsMenuItem);
		    fileMenu.add(closeMenuItem);
		    menuBar.add(fileMenu);
		    frame.setJMenuBar(menuBar);
		    
		    closeMenuItem.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		        	try {
		        		server.closeWhiteboard();
		            } catch (RemoteException ex) {
		                ex.printStackTrace();
		            }
		        }
		    });
		    
		    // New
		    newMenuItem.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		            int choice = JOptionPane.showConfirmDialog(null, " The current whiteboard will be cleared and will not be saved. Are you sure?", "Create New Whiteboard", JOptionPane.YES_NO_OPTION);
		            if (choice == JOptionPane.YES_OPTION) {
		                try {
		                    server.newWhiteboard();
		                } catch (RemoteException ex) {
		                    ex.printStackTrace();
		                }
		            }
		        }
		    });
		   
		    // save
		    saveMenuItem.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		        	try {

		                byte[] imageData = server.getCurrentImage();
		                saveWhiteboard(imageData);

		            } catch (RemoteException ex) {
		                ex.printStackTrace();
		            } catch (IOException ex) {
		                ex.printStackTrace();
		            }
		        }
		    });
		    
		    // Save as
		    saveAsMenuItem.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		            if (isManager) {
		                try {

		                    byte[] imageData = server.getCurrentImage();
		                    File selectedFile = saveFileDialog();

		                    if (selectedFile != null) {
		                        saveWhiteboardAs(imageData, selectedFile.getAbsolutePath());
		                    }
		                } catch (RemoteException ex) {
		                    ex.printStackTrace();
		                } catch (IOException ex) {
		                    ex.printStackTrace();
		                }
		            } else {
		                JOptionPane.showMessageDialog(null, "只有管理员可以另存为画板");
		            }
		        }
		    });
		    
		    // Open file
		    openMenuItem.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		        	try {

		                byte[] imageData = openFileDialog();

		                if (imageData != null) {
		                    server.openWhiteboard(imageData);
		                }
		            } catch (RemoteException ex) {
		                ex.printStackTrace();
		            } catch (IOException ex) {
		                ex.printStackTrace();
		            }
		        }
		    });}
	    
	    // When the admin logs out, close everyone's canvas
	    frame.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            if (isManager) {
	                // If it's an admin, close all users' windows
	                try {
	                    server.closeWhiteboard();
	                } catch (RemoteException ex) {
	                    ex.printStackTrace();
	                }
	            } else {
	                // If it's a regular user, only close the current window
	            	try {
						server.unregisterClient(clientName);
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            	
	                frame.dispose();
	            }
	        }
	    });

	    //Create a Canvas object
	    canvas = new Canvas(clientName, server, isManager);
	    canvas.setPreferredSize(new Dimension(800, 600)); 

	    // Create user list panel
	    JPanel userListPanel = new JPanel(new BorderLayout());
	    uList = new JList<>(userList);
	    JScrollPane userListScrollPane = new JScrollPane(uList);
	    userListPanel.add(userListScrollPane, BorderLayout.CENTER);

	    if(this.isManager()) {
	        // Add mouse listener to user list for double-click to kick out user
	        uList.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
	                    int index = uList.locationToIndex(e.getPoint());
	                    if (index >= 0) {
	                    	// Get selected username
	                        String selectedUser = uList.getModel().getElementAt(index).toString();
	                        // Check if an admin is selected, if so, do not perform any action
	                        if (selectedUser.equals(clientName)) {
	                            return; 
	                        }
	                        // Show confirmation dialog
	                        int response = JOptionPane.showConfirmDialog(frame,
	                            "Do you want to kick " + selectedUser + "?",
	                            "Confirm Kick",
	                            JOptionPane.YES_NO_OPTION,
	                            JOptionPane.QUESTION_MESSAGE);
	                        if (response == JOptionPane.YES_OPTION) {
	                            try {
									server.kickOutUser(selectedUser);
								} catch (RemoteException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} 
	                            // Call server method to kick out user
								JOptionPane.showMessageDialog(frame, selectedUser + " has been kicked out.");
	                        }
	                    }
	                }
	            }
	        });
	    }
	    
	    // Create a chat panel
	    JPanel chatPanel = createChatPanel();

	    // Create the right panel, containing the user list and chat panel
	    JPanel rightPanel = new JPanel(new BorderLayout());
	    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, userListPanel, chatPanel);
	    splitPane.setDividerLocation(240); 
	    splitPane.setDividerSize(0); 
	    rightPanel.add(splitPane, BorderLayout.CENTER);

	    // Use JSplitPane to split the canvas and the right panel, and add it to the center of the content panel
	    JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas, rightPanel);
	    mainSplitPane.setDividerLocation(720); 
	    mainSplitPane.setDividerSize(0);
	    content.add(mainSplitPane, BorderLayout.CENTER);

	    // If previous whiteboard content exists, then draw it on the client.
	    byte[] currentImage = server.getCurrentImage();
	    if (currentImage != null) {
	        canvas.drawImage(currentImage);
	    }
	    
	    // Get the screen size and resolution Then Calculate and Set the window position
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int screenWidth = screenSize.width;
	    int screenHeight = screenSize.height;
	    int x = (screenWidth - frame.getWidth()) / 2;
	    int y = (screenHeight - frame.getHeight()) / 2;
	    frame.setLocation(x, y);

	    // Set the main window properties
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	}
	
	// For Permission
	@Override
	public void setPermission(boolean b) throws RemoteException {
		// TODO Auto-generated method stub
		this.permission = b;
		
	}

	// For Permission
	@Override
	public boolean hasPermission() throws RemoteException {
		// TODO Auto-generated method stub
		return this.permission;
	}

	// assign manager
	@Override
	public void assignManager() throws RemoteException {
		// TODO Auto-generated method stub
		Client.isManager = true;
	}

	// Get client name
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return this.clientName;
	}

	// Set client name
	@Override
	public void setName(String name) throws RemoteException {
		// TODO Auto-generated method stub
		this.clientName = name;
		
	}
	
	// get manager
	@Override
	public boolean isManager() throws RemoteException {
		// TODO Auto-generated method stub
		return Client.isManager;
	}
	
	// Update client list
	@Override
	public void updateUserList(Set<CanvasClientInterface> clientsList) throws RemoteException {
		// TODO Auto-generated method stub
		
		userList.clear();

	    for (CanvasClientInterface client : clientsList) {
	        userList.addElement(client.getName());
	    }
	}
	
	// Update canvas
	@Override
	public void updateCanvas(CanvasMsgInterface message) throws RemoteException {
	    if (message.getName().equals(clientName)) {
	        return;
	    }
	    canvas.updateDrawing(message);
	    
	}
	
	// Use to notify client
	@Override
	public void receiveSystemMessage(String message) throws RemoteException {
	    SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            if (chatArea != null) {
	                chatArea.append(message + "\n");
	            }
	        }
	    });
	}
	
	// For chat
	@Override
	public void addChat(String message) throws RemoteException {
		// TODO Auto-generated method stub
	    SwingUtilities.invokeLater(() -> {
	        chatArea.append(message + "\n");
	    });
	}
	
	// Kick user
	@Override
	public void notifyKicked() throws RemoteException {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, "You have been kicked out the current administrator's whiteboard.");
	    SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(canvas);
	            if (frame != null) {
	                frame.dispose();
	            }

	            System.exit(0);
	        }
	    });
	}

	//  Get canvas image then convert canvas image to byte array
	@Override
	public byte[] getCurrentImage() throws RemoteException {
		// TODO Auto-generated method stub
	    try {
	        // Get canvas image
	    	BufferedImage currentImage = canvas.getCurrentCanvasImage();

	        // Convert canvas image to byte array
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(currentImage, "png", baos);
	        baos.flush();
	        byte[] imageData = baos.toByteArray();
	        baos.close();

	        return imageData;
	    } catch (IOException e) {
	        throw new RemoteException("Error converting canvas image to byte array", e);
	    }
	}
	
	// Close client UI
	@Override
	public void closeUI() throws RemoteException {
		if(!this.isManager()) {
		    JOptionPane.showMessageDialog(null, "The administrator has exited. The whiteboard will be closed.");
		}
		
	    SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {

	            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(canvas);
	            if (frame != null) {
	                frame.dispose();
	            }

	            System.exit(0);
	        }
	    });
	}
	
	// Draw image
	@Override
	public void drawImage(byte[] image) throws RemoteException {
	    try {
	        canvas.drawImage(image);
	    } catch (Exception e) {
	        System.err.println("Error in drawImage method: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	// Notify that a new canvas has been opened
	@Override
	public void newWhiteboard() throws RemoteException {
		// TODO Auto-generated method stub
		if(!this.isManager()) {
		    JOptionPane.showMessageDialog(null, "The administrator has opened a new whiteboard.");
		}
		canvas.clearCanvas();
	}
	
	// Use current time as filename and save image date to file by default path
	private void saveWhiteboard(byte[] imageData) throws RemoteException, IOException {
	    // Use current time as filename
	    String fileName = "whiteboard_" + System.currentTimeMillis() + ".png";

	    // Default save path is the current working directory
	    String defaultPath = System.getProperty("user.dir");
	    String filePath = defaultPath + File.separator + fileName;

	    // Save image data to file
	    FileOutputStream fos = new FileOutputStream(filePath);
	    fos.write(imageData);
	    fos.close();
	    String message = "The whiteboard has been saved to: " + filePath;
	    JOptionPane.showMessageDialog(null, message);
	}
	
	// Used to open file
	private byte[] openFileDialog() throws IOException {
	    JFileChooser fileChooser = new JFileChooser();
	    int result = fileChooser.showOpenDialog(null);

	    if (result == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        return Files.readAllBytes(selectedFile.toPath());
	    }

	    return null;
	}
	
	// Notify that the file has been read
	@Override
	public void sendNotification() throws RemoteException {
		// TODO Auto-generated method stub
		if(!this.isManager()) {
			JOptionPane.showMessageDialog(null, "The administrator has loaded the pervious drawing files.");
		}
		
	}
	
	// Set the default filename for saving and restrict to PNG format
	private File saveFileDialog() {
		
	    JFileChooser fileChooser = new JFileChooser();

	    // Set the default filename for saving
	    fileChooser.setSelectedFile(new File("defaultImageName.png"));

	    // Restrict the file chooser to select or save only as PNG files
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Image", "png");
	    fileChooser.setFileFilter(filter);

	    int userSelection = fileChooser.showSaveDialog(null);
	    if (userSelection == JFileChooser.APPROVE_OPTION) {
	        File fileToSave = fileChooser.getSelectedFile();
	        // Ensure the file has the correct extension
	        if (!fileToSave.getPath().toLowerCase().endsWith(".png")) {
	            fileToSave = new File(fileToSave.getPath() + ".png");
	        }
	        return fileToSave;
	    }
	    return null;
	}

	// Save image data to a specified path
	private void saveWhiteboardAs(byte[] imageData, String filePath) throws RemoteException, IOException {
	    FileOutputStream fos = new FileOutputStream(filePath);
	    fos.write(imageData);
	    fos.close();
	}
	
	// The function of chatArea
	private JPanel createChatPanel() {
	    JPanel chatPanel = new JPanel();
	    chatPanel.setLayout(new BorderLayout());
	    
	    // Initialize chatArea
	    chatArea = new JTextArea();
	    chatArea.setEditable(false);
	    JScrollPane chatScrollPane = new JScrollPane(chatArea);
	    chatPanel.add(chatScrollPane, BorderLayout.CENTER);

	    // Create chat input area.
	    JPanel inputPanel = new JPanel();
	    inputPanel.setLayout(new BorderLayout());
	    JTextField inputField = new JTextField();
	    JButton sendButton = new JButton("Send");
	    
	    // Send message by keyboard
	    inputField.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                sendMessage(inputField.getText());
	                inputField.setText("");
	            }
	        }
	    });
	    
	    // Send message by button
	    sendButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            String message = inputField.getText();
	            if (!message.isEmpty()) {
	                // Send chat message to the server.
	                chatArea.append("You: " + message + "\n"); 
	                inputField.setText("");  

	                try {
						server.broadcastMessage(clientName,message);
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                
	            }
	        }
	    });

	    inputPanel.add(inputField, BorderLayout.CENTER);
	    inputPanel.add(sendButton, BorderLayout.EAST);
	    chatPanel.add(inputPanel, BorderLayout.SOUTH);

	    return chatPanel;
	}
	
	// For chat send message to server
	private void sendMessage(String message) {
	    if (!message.isEmpty()) {

	        chatArea.append("You: " + message + "\n");

	        try {
	            server.broadcastMessage(clientName, message);
	        } catch (RemoteException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	// For chat send message to every one
	@Override
	public void receiveMessage(String name, String message) throws RemoteException {
	    SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            if (chatArea != null) {
	                if (!name.equals(clientName)) {
	                	chatArea.append(name + ": " + message + "\n");
	                } 
	            }
	        }
	    });
	}
	
	// When the server shuts down, notify and close the client
	@Override
	public void serverShutdown() throws RemoteException {
		// TODO Auto-generated method stub
	    SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            JOptionPane.showMessageDialog(null, "The server is shutting down. Please close the application.");
	    	    SwingUtilities.invokeLater(new Runnable() {
	    	        @Override
	    	        public void run() {
	    	            // close the whiteboard
	    	            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(canvas);
	    	            if (frame != null) {
	    	                frame.dispose();
	    	            }
	    	            // Exit the program
	    	            System.exit(0);
	    	        }
	    	    });
	        }
	    });
		
	}

	// Approve user to join
	@Override
	public void askManagerApproval(CanvasClientInterface client) throws RemoteException {
		// TODO Auto-generated method stub
        String name = client.getName();
        int option = JOptionPane.showConfirmDialog(null, name + " Request to join the whiteboard session. Approve?", "Request to join", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            client.setPermission(true);
        } else {
            client.setPermission(false);
        }
        
		
	}
}
