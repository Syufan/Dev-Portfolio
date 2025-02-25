package client;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

import remote.CanvasMsgInterface;
import remote.CanvasServerInterface;

public class Canvas extends JPanel {
    private static final long serialVersionUID = 1L;
	private CanvasServerInterface server;
    private String clientName;
    private Color currColor= Color.BLACK;
    private String currMode= "pen";
    private String text = "";
    private Point lastPt;
    private BufferedImage canvasImage;
    private Graphics2D canvasGraphics;
    private JButton penButton;
    private JPopupMenu penSizePopupMenu;
    private int currentPenSize = 1;
    private int currentEraserSize = 5;
    private JButton eraserButton;
    private JPopupMenu eraserSizePopupMenu;
    private JButton lineButton;
    private JButton rectangleButton;
    private JButton ovalButton;
    private JButton textButton;
    private JButton circleButton;
    private JPanel toolPanel = new JPanel();
    private Point startPt; 
    private Point previewPt;
    private Point circleCenterPt;
    private Point circleRadiusPt;
    private Point ovalStartPt;
    private Point ovalEndPt;
    private Point rectStartPt;
    private Point rectEndPt;
    private Point textPreviewPt;
    private Point textDrawPt;
    private boolean isLineStarted = false;
    private BasicStroke prevStroke;
    private JComboBox<Color> colorComboBox;
    private Color[] colors = {
        Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
        Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA,
        Color.ORANGE, Color.PINK, Color.RED, Color.WHITE,
        Color.YELLOW, new Color(128, 0, 0), new Color(0, 128, 0), new Color(0, 0, 128)
    };
    
    // Implement drawing
    public Canvas(String name, CanvasServerInterface remoteInterface, boolean isManager) {
        
    	this.clientName = name;
        this.server = remoteInterface;
        setDoubleBuffered(false);
        setPreferredSize(new Dimension(800, 600));
        canvasImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvasImage.createGraphics();
        canvasGraphics.setColor(Color.WHITE);
        canvasGraphics.fillRect(0, 0, 800, 600);
        
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentPt = e.getPoint();

                if (lastPt != null) {
                    if ("pen".equals(currMode)) {
                        canvasGraphics.drawLine(lastPt.x, lastPt.y, currentPt.x, currentPt.y);
                        try {
                            Draw item = new Draw("draw", clientName, currMode, currColor, currentPt, text, currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    } else if ("eraser".equals(currMode)) {
                    	
                        canvasGraphics.drawLine(lastPt.x, lastPt.y, currentPt.x, currentPt.y);
                        try {
                            Draw item = new Draw("draw", clientName, currMode, currColor, currentPt, text, currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    }
                    repaint();
                    lastPt = currentPt;
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if ("line".equals(currMode) && isLineStarted) {
                    previewPt = e.getPoint();
                    repaint();
                } else if ("circle".equals(currMode) && circleCenterPt != null) {
                    circleRadiusPt = e.getPoint();
                    repaint();
                } else if ("oval".equals(currMode) && ovalStartPt != null) {
                    ovalEndPt = e.getPoint();
                    repaint();
                }else if ("rectangle".equals(currMode) && rectStartPt != null) {
                    rectEndPt = e.getPoint();
                    repaint();
                }else if ("text".equals(currMode) && !text.isEmpty()) {
                    textPreviewPt = e.getPoint();
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	
                if ("line".equals(currMode)) {
                    if (!isLineStarted) {
                        startPt = e.getPoint();
                        isLineStarted = true;
                        
                        try {
                            Draw item = new Draw("startLine", clientName, currMode, currColor, startPt, "", currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        
                    } else {
                    	
                        Point endPt = e.getPoint();
                        canvasGraphics.drawLine(startPt.x, startPt.y, endPt.x, endPt.y);
                        repaint();
                        
                        try {
                            Draw item = new Draw("drawLine", clientName, currMode, currColor, endPt, text, currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        
                        startPt = null;
                        previewPt = null;
                        isLineStarted = false;
                    }
                } else if ("circle".equals(currMode)) {
                	
                    if (circleCenterPt == null) {
                        circleCenterPt = e.getPoint();
                        try {
                            Draw item = new Draw("startCircle", clientName, currMode, currColor, circleCenterPt, "", currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                    	
                        Point radiusPt = e.getPoint();
                        int radius = (int) Math.sqrt(Math.pow(radiusPt.x - circleCenterPt.x, 2) + Math.pow(radiusPt.y - circleCenterPt.y, 2));
                        canvasGraphics.drawOval(circleCenterPt.x - radius, circleCenterPt.y - radius, radius * 2, radius * 2);
                        repaint();
                        
                        try {
                            Draw item = new Draw("drawCircle", clientName, currMode, currColor, radiusPt, String.valueOf(radius), currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        
                        circleCenterPt = null;
                        circleRadiusPt = null;
                    }
                    
                } else if ("oval".equals(currMode)) {
                	
                    if (ovalStartPt == null) {
                        ovalStartPt = e.getPoint();
                        
                        try {
                            Draw item = new Draw("startOval", clientName, currMode, currColor, ovalStartPt, "", currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        
                    } else {
                    	
                        Point endPt = e.getPoint();
                        int width = Math.abs(endPt.x - ovalStartPt.x);
                        int height = Math.abs(endPt.y - ovalStartPt.y);
                        int x = Math.min(ovalStartPt.x, endPt.x);
                        int y = Math.min(ovalStartPt.y, endPt.y);
                        canvasGraphics.drawOval(x, y, width, height);
                        repaint();
                        
                        try {
                            Draw item = new Draw("drawOval", clientName, currMode, currColor, endPt, width + "," + height + "," + x + "," + y, currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        
                        ovalStartPt = null;
                        ovalEndPt = null;
                    }
                }else if ("rectangle".equals(currMode)) {
                	
                    if (rectStartPt == null) {
                        rectStartPt = e.getPoint();
                        try {
                            Draw item = new Draw("startRect", clientName, currMode, currColor, rectStartPt, "", currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                            
                        }
                    } else {
                    	
                        Point endPt = e.getPoint();
                        int width = Math.abs(endPt.x - rectStartPt.x);
                        int height = Math.abs(endPt.y - rectStartPt.y);
                        int x = Math.min(rectStartPt.x, endPt.x);
                        int y = Math.min(rectStartPt.y, endPt.y);
                        canvasGraphics.drawRect(x, y, width, height);
                        repaint();
                        
                        try {
                            Draw item = new Draw("drawRect", clientName, currMode, currColor, endPt, width + "," + height + "," + x + "," + y, currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        
                        rectStartPt = null;
                        rectEndPt = null;
                    }
                }else if ("text".equals(currMode)) {
                	
                    if (text.isEmpty()) {
                    	
                    	text = JOptionPane.showInputDialog("Enter text:");
                        if (text == null || text.trim().isEmpty()) {
                        	text = "";
                            return;
                            
                        }
                    } else {
                    	
                        textDrawPt = e.getPoint();
                        canvasGraphics.drawString(text, textDrawPt.x, textDrawPt.y);
                        repaint();

                        try {
                        	
                            Draw item = new Draw("drawText", clientName, currMode, currColor, textDrawPt, text, currentPenSize, currentEraserSize);
                            server.broadCastCanvas(item);
                            
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        
                        text = "";
                        textPreviewPt = null;
                        textDrawPt = null;
                    }
                }
                
                else {
                	
                    lastPt = e.getPoint();
                    
                    try {
                        Draw item = new Draw("start", clientName, currMode, currColor, lastPt, "", currentPenSize, currentEraserSize);
                        server.broadCastCanvas(item);
                    } catch (RemoteException ex) {
                    	ex.printStackTrace();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!"line".equals(currMode) && !"circle".equals(currMode) && !"oval".equals(currMode) && !"rectangle".equals(currMode) && !"text".equals(currMode)) {
                    try {
                        lastPt = e.getPoint();
                        Draw item = new Draw("end", clientName, currMode, currColor, lastPt, "", currentPenSize, currentEraserSize);
                        server.broadCastCanvas(item);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                    lastPt = null;
                }
            }
        });
        
        initializeToolButtons();
    }

    // for updating the canvas
	public void updateDrawing(CanvasMsgInterface message) throws RemoteException {

        // Extract drawing details from the message
        String receivedMode = message.getMode();
        Color receivedColor = message.getColor();
        Point currentPt = message.getPoint();
        String state = message.getState();
        int remotePenSize = message.getPenSize();
        int remoteEraserSize = message.getEraserSize();
        
        // Set the temporary color based on the received mode 
        Color tempColor;
        if ("eraser".equals(receivedMode)) {
            tempColor = Color.WHITE;
        } else {
            tempColor = receivedColor;
        }
        

        // Save the current graphics settings
        Color currentColor = canvasGraphics.getColor();
        Stroke currentStroke = canvasGraphics.getStroke();
        
        canvasGraphics.setColor(tempColor);
        
        if ("eraser".equals(receivedMode)) {
        	
            canvasGraphics.setStroke(new BasicStroke(remoteEraserSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
        } else if("pen".equals(receivedMode)) {
        	
            canvasGraphics.setStroke(new BasicStroke(remotePenSize));
            
        }else {
        	
        	canvasGraphics.setStroke(new BasicStroke(1));
        	
        }

        // Check the state of drawing
        if ("startLine".equals(state)) {
        	
            // Start a new line
            this.startPt = currentPt;
            this.isLineStarted = true;
            
        } else if ("drawLine".equals(state) && this.startPt != null) {
        	
            // Draw the line
            canvasGraphics.setColor(receivedColor);
            canvasGraphics.setStroke(new BasicStroke(1));
            canvasGraphics.drawLine(startPt.x, startPt.y, currentPt.x, currentPt.y);
            repaint();
            this.startPt = null;
            this.isLineStarted = false;
            
        } else if ("startCircle".equals(state)) {
        	
            // Start a new circle
            this.circleCenterPt = currentPt;
            
        }else if ("drawCircle".equals(state) && this.circleCenterPt != null) {
        	
            // Draw the circle
            int radius = Integer.parseInt(message.getText());
            canvasGraphics.setColor(receivedColor);
            canvasGraphics.drawOval(circleCenterPt.x - radius, circleCenterPt.y - radius, radius * 2, radius * 2);
            repaint();
            this.circleCenterPt = null;
            
        } else if ("startOval".equals(state)) {
            // Start a new oval
            this.ovalStartPt = currentPt;
            
        } else if ("drawOval".equals(state) && this.ovalStartPt != null) {
        	
            // Draw the oval
            String[] params = message.getText().split(",");
            int width = Integer.parseInt(params[0]);
            int height = Integer.parseInt(params[1]);
            int x = Integer.parseInt(params[2]);
            int y = Integer.parseInt(params[3]);
            canvasGraphics.setColor(receivedColor);
            canvasGraphics.drawOval(x, y, width, height);
            repaint();
            this.ovalStartPt = null;
            
        } else if ("startRect".equals(state)) {
        	
            // Start a new rectangle
            this.rectStartPt = currentPt;
            
        } else if ("drawRect".equals(state) && this.rectStartPt != null) {
        	
            // Draw the rectangle
            String[] params = message.getText().split(",");
            int width = Integer.parseInt(params[0]);
            int height = Integer.parseInt(params[1]);
            int x = Integer.parseInt(params[2]);
            int y = Integer.parseInt(params[3]);
            canvasGraphics.setColor(receivedColor);
            canvasGraphics.drawRect(x, y, width, height);
            repaint();
            this.rectStartPt = null;
            
        } else if ("drawText".equals(state)) {
        	
            // Draw the text
            String text = message.getText();
            canvasGraphics.setColor(receivedColor);
            canvasGraphics.drawString(text, currentPt.x, currentPt.y);
            repaint();
            
        } else if ("start".equals(state)) {
        	
            // Start a new line
            this.lastPt = currentPt;
            if ("line".equals(receivedMode)) {
                this.startPt = currentPt;
            }
            
        } else if ("draw".equals(state) && "line".equals(receivedMode) && this.startPt != null) {

            // Only draw the line at the end state for the "line" mode
            canvasGraphics.drawLine(startPt.x, startPt.y, currentPt.x, currentPt.y);
            repaint();
            this.lastPt = null;
            this.startPt = null;
            
        } else if ("draw".equals(state) && this.lastPt != null) {        	

            // Continue drawing for other modes and Update lastPt for the next drawing operation
            canvasGraphics.drawLine(lastPt.x, lastPt.y, currentPt.x, currentPt.y);
            this.lastPt = currentPt; 
            
        } else if ("end".equals(state)) {
        	
            // End drawing
            this.lastPt = null;
            this.startPt = null;
        }

        // Restore the original graphics settings
        canvasGraphics.setColor(currentColor);
        canvasGraphics.setStroke(currentStroke);
        repaint();
    }
	
	// Set color and size
    public void setMode(String mode) {
    	
        currMode = mode;
        
        if ("eraser".equals(mode)) {
        	
            canvasGraphics.setColor(Color.WHITE);
            canvasGraphics.setStroke(new BasicStroke(currentEraserSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
        } else if ("pen".equals(mode)) {
        	
            canvasGraphics.setColor(currColor);
            canvasGraphics.setStroke(new BasicStroke(currentPenSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
        } else {
        	
            canvasGraphics.setColor(currColor);
            canvasGraphics.setStroke(new BasicStroke(1));
            
        }
    }
    
    // Text button
    public void setText(String text) {
        this.text = text;
    }
    
    // Save file
    public void saveAsFile(String fileName) {
        try {
            ImageIO.write(canvasImage, "png", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // For refresh
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvasImage, 0, 0, null);

        if ("line".equals(currMode) && isLineStarted && previewPt != null) {
        	
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(currColor);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{10}, 0));
            g2d.drawLine(startPt.x, startPt.y, previewPt.x, previewPt.y);
            
        } else if ("circle".equals(currMode) && circleCenterPt != null && circleRadiusPt != null) {
        	
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(currColor);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{10}, 0));
            int radius = (int) Math.sqrt(Math.pow(circleRadiusPt.x - circleCenterPt.x, 2) + Math.pow(circleRadiusPt.y - circleCenterPt.y, 2));
            g2d.drawOval(circleCenterPt.x - radius, circleCenterPt.y - radius, radius * 2, radius * 2);
            
        } else if ("oval".equals(currMode) && ovalStartPt != null && ovalEndPt != null) {
        	
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(currColor);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{10}, 0));
            int width = Math.abs(ovalEndPt.x - ovalStartPt.x);
            int height = Math.abs(ovalEndPt.y - ovalStartPt.y);
            int x = Math.min(ovalStartPt.x, ovalEndPt.x);
            int y = Math.min(ovalStartPt.y, ovalEndPt.y);
            g2d.drawOval(x, y, width, height);
            
        } else if ("rectangle".equals(currMode) && rectStartPt != null && rectEndPt != null) {
        	
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(currColor);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{10}, 0));
            int width = Math.abs(rectEndPt.x - rectStartPt.x);
            int height = Math.abs(rectEndPt.y - rectStartPt.y);
            int x = Math.min(rectStartPt.x, rectEndPt.x);
            int y = Math.min(rectStartPt.y, rectEndPt.y);
            g2d.drawRect(x, y, width, height);
            
        } else if ("text".equals(currMode) && !text.isEmpty() && textPreviewPt != null) {
        	
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(currColor);
            g2d.drawString(text, textPreviewPt.x, textPreviewPt.y);
            
        }
    }
    
    // Create all buttons
    private void initializeToolButtons() {
    	
    	// Pen
        penButton = new JButton("PEN");
        penSizePopupMenu = new JPopupMenu();
        for (int size : new int[] { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21 }) {
            JMenuItem menuItem = new JMenuItem("Size " + size);
            menuItem.addActionListener(e -> {
                currentPenSize = size;
                canvasGraphics.setStroke(new BasicStroke(currentPenSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                setMode("pen");
            });
            penSizePopupMenu.add(menuItem);
            if (size == 1) {
                menuItem.setSelected(true);
            }
        }
        penButton.addActionListener(e -> penSizePopupMenu.show(penButton, 0, penButton.getHeight()));

        // Eraser
        eraserButton = new JButton("Eraser");
        eraserSizePopupMenu = new JPopupMenu();
        for (int size : new int[] {  1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 30, 35, 40, 45, 50 }) { 
            JMenuItem menuItem = new JMenuItem("Size " + size);
            menuItem.addActionListener(e -> {
            	currentEraserSize = size;
                canvasGraphics.setStroke(new BasicStroke(currentEraserSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                setMode("eraser");
            });
            eraserSizePopupMenu.add(menuItem);
            if (size == 10) {
                menuItem.setSelected(true);
            }
        }
        eraserButton.addActionListener(e -> eraserSizePopupMenu.show(eraserButton, 0, eraserButton.getHeight()));


        lineButton = new JButton("Line");
        lineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMode("line");
            }
        });

        rectangleButton = new JButton("Rectangle");
        rectangleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMode("rectangle");
            }
        });
        
        circleButton = new JButton("Circle");
        circleButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		setMode("circle");
        	}
        });
        ovalButton = new JButton("Oval");
        ovalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMode("oval");
            }
        });

        textButton = new JButton("Text");
        textButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMode("text");
                String inputText = JOptionPane.showInputDialog("Enter text:");
                if (inputText != null && !inputText.isEmpty()) {
                    setText(inputText);
                }
            }
        });
        
        // color button
        colorComboBox = new JComboBox<>(colors);
        colorComboBox.setRenderer(new ColorComboBoxRenderer());
        colorComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = (Color) colorComboBox.getSelectedItem();
                setColor(selectedColor);
                SwingUtilities.invokeLater(() -> colorComboBox.setPopupVisible(false));
            }
        });

        colorComboBox.setUI(new ColorComboBoxUI());
        colorComboBox.setSelectedItem(currColor);
        
        toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolPanel.setBounds(0, 0, 800, 39);
        toolPanel.add(penButton);
        toolPanel.add(eraserButton);
        toolPanel.add(lineButton);
        toolPanel.add(circleButton);
        toolPanel.add(ovalButton);
        toolPanel.add(rectangleButton);
        toolPanel.add(textButton);
        toolPanel.add(colorComboBox);
        
        add(toolPanel);
    }
    
    // Read the image, then draw the image
    public void drawImage(byte[] imageData) {
        try {
        	
            // Decode byte data into a BufferedImage
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            Graphics2D g2d = canvasImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            repaint();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Get the canvas image
    public BufferedImage getCurrentCanvasImage() {
        return canvasImage;
    }
    
    // For new button
    public void clearCanvas() {
    	
        canvasGraphics.setColor(Color.WHITE);
        canvasGraphics.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        
        // Inherit Drawing Mode
        prevStroke = (BasicStroke) canvasGraphics.getStroke();
        canvasGraphics.setColor(currColor);
        canvasGraphics.setStroke(prevStroke);
        repaint();
        
    }
    
    // For color button
    public void setColor(Color color) {
        currColor = color;
        colorComboBox.setSelectedItem(color);
        SwingUtilities.invokeLater(() -> {
            ColorComboBoxUI ui = (ColorComboBoxUI) colorComboBox.getUI();
            BasicArrowButton arrowButton = ui.getArrowButton();
            if (arrowButton != null) {
                arrowButton.setBackground(Color.WHITE);
                arrowButton.setForeground(Color.BLACK);
            }
            colorComboBox.repaint();
        });
        if (!"eraser".equals(currMode)) {
            canvasGraphics.setColor(color);
            canvasGraphics.setStroke(new BasicStroke(1));
        }
    }
    
    // For color button
    private class ColorComboBoxRenderer extends JPanel implements ListCellRenderer<Color> {
        private static final long serialVersionUID = 8652581127916655822L;

		public Component getListCellRendererComponent(JList<? extends Color> list, Color value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setBackground(value);
            setPreferredSize(new Dimension(50, 20));
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            return this;
        }
    }
    
    // For color button
    private class ColorComboBoxUI extends BasicComboBoxUI {
        private BasicArrowButton arrowButton;

        @Override
        protected JButton createArrowButton() {
            arrowButton = new BasicArrowButton(BasicArrowButton.SOUTH, Color.BLACK, Color.WHITE, Color.GRAY, Color.LIGHT_GRAY);
            arrowButton.setPreferredSize(new Dimension(20, 20));
            arrowButton.setBorder(BorderFactory.createEmptyBorder());
            arrowButton.setFocusPainted(false);
            return arrowButton;
        }

        public BasicArrowButton getArrowButton() {
            return arrowButton;
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(currColor);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

}
