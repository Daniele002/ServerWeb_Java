/*
 * This class allows the port binding and accepts incoming connections
 */

package WebServer;
import java.io.File;

import java.io.FileReader;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.json.simple.JSONObject;


import org.json.simple.parser.JSONParser;

public class CreateConnection implements Runnable{

	private static ServerSocket ws = null;
	public static ExecutorService pool = Executors.newCachedThreadPool();

	static int port;
	
	static JTextArea txtArea;
	static JLabel portLabel;
	

	static String MandatoryPath = "";

	static boolean JsonRead = false;
	static boolean MandatoryPathExists;
	
	
	//constructor
	public CreateConnection(JTextArea textAreaOutput, JLabel portLabel){
		//this.port = port;
		this.txtArea = textAreaOutput;
		this.portLabel = portLabel;
		
	}
	
	//function to read the json file
	public static void ReadConfFile(){

		JSONParser parser = new JSONParser();


		try{
			
			File fileJson = new File("./src/WebServer/JsonConfig/config.json");
			
			System.out.println(fileJson.getAbsolutePath());
			
			Object obj = parser.parse(new FileReader(fileJson));

			JSONObject jsonObject =  (JSONObject) obj;

			MandatoryPath  = (String) jsonObject.get("MandatoryPath");

			System.out.println("Valore di MandatoryPath letto da json: "+ MandatoryPath);
			
			MandatoryPathExists = new File(MandatoryPath).exists();
			
			if(MandatoryPathExists == false){
				txtArea.append("The mandatory path does not exist\n");
				txtArea.append("change the value in the json configuration\n");
			}
			
			String Stringport = (String) jsonObject.get("port");
			
			port = Integer.parseInt(Stringport);
			
			portLabel.setText("" + port);
			
			System.out.println("Valore di port letto da json: "+ port);
			
			JsonRead = true;
			
		}catch(Exception e){
			System.out.println("Error reading json file");
			txtArea.append("Error reading json configuration file");
			e.printStackTrace();
		}

	}



	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//port binding
		try {
			ws = new ServerSocket(port);
			txtArea.append("The server is listening on port: "+ port +"\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//listens and accepts the connections
		while(true)
		{
			try{
				Socket s = null;
				s = ws.accept();

				System.out.println("-----------------");
				System.out.println("connection accepted");
				txtArea.append("Connection accepted from: " + s.getInetAddress() + "\n");

				//instantiate a thread
				pool.execute(new ServerThread(s, MandatoryPath));

				System.out.println("-----------------");
			}catch (IOException e) {
				System.out.println("Error: impossible to accepet connection");
				txtArea.append("Error: impossible to accepet connection\n");
				e.printStackTrace();
				break;
			}
		}

	}

	/*
	 * try to see if the door is open
	 */
	/*
	public static boolean TestPort(int port){

		boolean flag = false;
		String host = "localhost";


		try {

            Socket socket = new Socket(host, port);
            socket.close();
            flag = true;

        }
        catch(Exception e) {
        	flag = false;
        }


		return flag;
	}
	 */



}
