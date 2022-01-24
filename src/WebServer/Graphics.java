
/*
 * The class implements the graphical part of the program
 */


package WebServer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

public class Graphics implements ActionListener {
	
	//static int port = 8080;

	//Main function
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Instance of the graphic elements
		JFrame frmMenu = new JFrame("Mango HTTP Server");
		JPanel pnlContainer = new JPanel();
		
		//Container panel layout
		pnlContainer.setLayout(new BorderLayout());
		
		//Instance of objects inside the container panel
		JPanel pnlLabel = new JPanel();
		pnlLabel.setPreferredSize(new Dimension(200, 300));
		JPanel pnlPort = new JPanel();
		pnlPort.setPreferredSize(new Dimension(200, 300));
		JPanel pnlButton = new JPanel();
		pnlButton.setPreferredSize(new Dimension(200, 300));
		JPanel pnlTextArea = new JPanel();
		pnlTextArea.setPreferredSize(new Dimension(600,100));
		
		
		JTextArea txtAreaOutput = new JTextArea("Welcome\n");
		txtAreaOutput.append("Check your configuration before you start\n");
		txtAreaOutput.append("It will no longer be possible to change it once started\n");
		//txtAreaOutput.setPreferredSize(new Dimension(700, 100));
		JScrollPane areaScrollPane = new JScrollPane(txtAreaOutput);
		areaScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(690, 100));
		
		JLabel lblWebServer = new JLabel("Web Server");
		
		JButton btnStart = new JButton("Start");
		JButton btnStop = new JButton("Stop");
		JButton btnQuit = new JButton("Quit");
		JButton btnEditConf = new JButton("Config");
		JButton btnReadConf = new JButton("Read Conf");
		
		btnStart.setEnabled(false);
		
		btnStop.setEnabled(false);
		
		JLabel lblPort = new JLabel("");
		
		CreateConnection connection = new CreateConnection(txtAreaOutput, lblPort);

		Thread connectionThread = new Thread(connection);
		
		
		btnStart.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				
				
				//check if the connection already exists
				if(connectionThread.isAlive())
				{
					connectionThread.resume();
					txtAreaOutput.append("The service has resumed\n");
				}
				else{
					try{
						connectionThread.start();
						btnReadConf.setEnabled(false);
					}catch(Exception er){
						er.printStackTrace();
					}
					//lblPort.setText("" + CreateConnection.port);
					btnEditConf.setEnabled(false);
					txtAreaOutput.append("The service has started\n");
				}
				
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				btnEditConf.setEnabled(false);
			}
		});
		
		
		btnStop.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {

				connectionThread.suspend();
								
				txtAreaOutput.append("The service has stopped\n");
				btnStop.setEnabled(false);
				btnStart.setEnabled(true);
			}
		});
		
	
		
		btnReadConf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//read the json file
				try{
					CreateConnection.ReadConfFile();
					lblPort.setText("" + CreateConnection.port);
					if(connection.JsonRead == true && connection.MandatoryPathExists == true){
						btnStart.setEnabled(true);
						txtAreaOutput.append("Json file read correctly\n");
					}
					
				}catch(Exception er){
					txtAreaOutput.append("Error reading the json configuration file\n");
					er.printStackTrace();
				}
			}
		});
		
		//open the json file to change the configuration
		btnEditConf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try  
				{  
					//constructor of file class having file as argument  
					File fileJson = new File("./src/WebServer/JsonConfig/config.json");
					
					if(!Desktop.isDesktopSupported())//check if Desktop is supported by Platform or not  
					{  
						System.out.println("Desktop not supported");  
						return;  
					}  
					Desktop desktop = Desktop.getDesktop();  
					if(fileJson.exists())         			 //checks file exists or not  
						desktop.open(fileJson);              //opens the specified file  
				}  
				catch(Exception el) {  
					el.printStackTrace();  
				}
				
				
				
				
			}
		});
		
		
		
		btnQuit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		
		//lblPort.setText("" + CreateConnection.port);
		/*
		boolean PortTest = CreateConnection.TestPort(port);
		
		if(PortTest == true)
		{
			lblPort.setForeground(Color.green);
			txtAreaOutput.append("The Port is available\n");
			System.out.println("Port is available");
			btnStart.setEnabled(true);	
		}
		else{
			lblPort.setForeground(Color.red);
			txtAreaOutput.append("The Port is not available\n");
			System.out.println("Port is not available");
		}*/
		
		
		//Add objects to the panels
		
		pnlLabel.add(lblWebServer);
		
		pnlPort.add(lblPort);
		
		pnlTextArea.add(areaScrollPane);
		
		pnlButton.add(btnStart);
		pnlButton.add(btnStop);
		pnlButton.add(btnQuit);		
		pnlButton.add(btnEditConf);
		pnlButton.add(btnReadConf);
		
		
		//Add objects to the container panel
		pnlContainer.add(pnlLabel, BorderLayout.WEST);
		pnlContainer.add(pnlPort, BorderLayout.CENTER);
		pnlContainer.add(pnlButton, BorderLayout.EAST);
		pnlContainer.add(pnlTextArea, BorderLayout.SOUTH);
		
		
		frmMenu.setSize(720, 420);
		frmMenu.setResizable(false);
		frmMenu.add(pnlContainer);
		
		frmMenu.setVisible(true);
		
		frmMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
