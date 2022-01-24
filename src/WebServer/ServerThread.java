/*
 * this class processes the request and responds to clinet
 */

package WebServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable {

	String MandatoryPath = null;;
	
	Socket s = null;

	static String InexistentFile = "/ErrorPages/PagNotFound.html";
	static String IndexNotExist = "/ErrorPages/IndexNotExist.html";
	
	static String httpStatusCode;

	
	//constructor
	public ServerThread(Socket s, String MandatoryPath) {
		this.s = s;
		
		this.MandatoryPath = MandatoryPath;
	}

	String Request;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//get the request
		Request = getRequest().toString();
		
		//split the method from the request
		String method = ObtainMethod(Request);
		//split the path from the request
		String path = ObtainPath(Request);
		//check if the path exists
		path = checkPath(path);
		//get the type of object or file to return
		String type = ObtainType(path);
		//Send the response to the client
		SendResponse(type, method, path);
		

	}

	//get the request reading the socket input stream
	private StringBuilder getRequest(){
		
		StringBuilder result = new StringBuilder();;
		
		try {

			InputStream inputStream = s.getInputStream();

			
			do {
				result.append((char) inputStream.read());
			} while (inputStream.available() > 0);
			
			System.out.println(result);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error getting the request");
			httpStatusCode = "400 Bad Request";
			e.printStackTrace();
		}
		
		return result;		
		
	}
	
	//split the request to get the path of the requested resource
	private String ObtainPath(String Request){
		
		String path = null;

		String[] lines = null;
		lines = Request.split("\n");

		String[] parts = null;
		parts = lines[0].split(" ");

		path = parts[1];
		
		return path;
	}
	
	//split the request to get the method
	private String ObtainMethod(String Request){
		
		String method = null;
		
		String[] lines = null;
		lines = Request.split("\n");

		String[] parts = null;
		parts = lines[0].split(" ");

		method = parts[0];
		
		
		return method;
	}
	
	/*
	 * Perform a series of checks on the path I get from the request.
	 * 
	 * The server displays the index page by default if it is present
	 * 
	 */
	private String checkPath(String path){
		
		File file = new File(MandatoryPath + path);
		
		boolean isDir = file.isDirectory();
		if (isDir == false) {
			
			if (path.contains(".")) {
				//
			}
			else{
				path = path + ".html";
			}
			
		}
		
		if (isDir == true) {
			
			System.out.println("## Is a dir ##");
			
			File IndexFile = new File(MandatoryPath + path + "/index.html");

			
			//System.out.println(IndexFile.getPath());
			
			boolean IndexExists = IndexFile.exists();

			if (IndexExists == true) {
				System.out.println("The file index in " + path + " exist");
				path += "/index.html";
			} else { 
				System.out.println("The file index in " + path + " do not exist");
				httpStatusCode = "404 Not Found";
				//Shows an error page present in / FileHTML
				path = IndexNotExist;
			}
		}
		
		
		boolean exists = (new File(MandatoryPath + path)).exists();
		
		if (exists == false) {
			System.out.println("The path searched does not exist");
			//Shows an error page present in / FileHTML
			path = InexistentFile;
		}
		
		boolean isFile = (new File(MandatoryPath + path)).isFile();
		
		if (isFile == true) {
			if (exists) {
				httpStatusCode = "200 OK";
			} else {
				httpStatusCode = "404 Not Found";
				path = InexistentFile;
			}

		}
		
		//debug
		//System.out.println("final path: " + MandatoryPath + path);
		
		return path;
		
	}
	
	private String ObtainType(String path){
		
		String type=null;
		
		if(path.contains(".html") || path.contains(".htm")){
			type="text/html";
		}
		/*else if(path.contains(".php")){
			type="application/x-httpd-php";
		}*/
		else if(path.contains(".css")){
			type="text/css";
		}
		else if(path.contains(".js")){
			type="text/javascript";
		}
		else if(path.contains(".pdf")){
			type="application/pdf";
		}
		else if(path.contains(".png")){
			type="image/png";
		}
		else if(path.contains(".jpg")){
			type="image/jpeg";
		}
		else if(path.contains(".ico")){
			type="image/vnd.microsoft.icon";
		}
		else{
			System.out.println("Nessuna corrispondenza per il tipo");
		}
		
		
		return type;
	}
	
	
	//send the response to the client
	private void SendResponse(String type, String method, String path){
		
		OutputStream output = null;
		PrintWriter out = null;
		
		File file = new File(MandatoryPath + path);
		
		
		try{
			output = s.getOutputStream();
			out = new PrintWriter(output, true);
		}catch(Exception e){
			e.printStackTrace();
			httpStatusCode = "500 Internal Server Error";
		}
		
		/*
		 *based on the type of request made and on the method ,
		 *a different response is sent
		 */
		if(method.equals("GET") || method.equals("POST")){
			try {
				
				output = s.getOutputStream();
				//PrintWriter out = new PrintWriter(output, true);
				
				out.println("HTTP/1.1 "+httpStatusCode);
				out.println("Server: Mio Web Server");
				out.println("Content-type: " + type +"; charset=utf-8");
				// out.println("Date: " + new Date());
				out.println("Keep-Alive: timeout=5, max=2000");
				out.println("Connection: Keep-Alive");
				out.println("Content-Length: " + file.length());
				out.println("");
				
				//Read and convert the requested file in byte
				FileInputStream fin = new FileInputStream(file);
				
				byte[] bFile = new byte[(int) file.length() ];
				try{
					
					fin.read(bFile);
					fin.close();
					//send the converted file to the client
					s.getOutputStream().write(bFile);

				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				
				out.flush();
				out.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(method.contains("HEAD")){
			
			try{
				output = s.getOutputStream();
				//PrintWriter out = new PrintWriter(output, true);
				out.println("HTTP/1.1 " + httpStatusCode);
				out.println("Server: Mio Web Server");
				out.println("Content-type: " + type +"; charset=utf-8");
				// out.println("Date: " + new Date());
				out.println("Keep-Alive: timeout=5, max=2000");
				out.println("Connection: Keep-Alive");
				out.println("Content-Length: " + file.length());
				out.println("");
				
				out.flush();
				out.close();
				
			} catch(Exception e){
				e.printStackTrace();
			}
			
		}
		else{ //in case of not supported method
			try{
				output = s.getOutputStream();
				//PrintWriter out = new PrintWriter(output, true);
				out.println("HTTP/1.1 405 Method Not Allowed");
				out.println("Server: Mio Web Server");
				out.println("Allow: GET,POST,HEAD");
				out.println("Content-type: " + type +"; charset=utf-8");
				// out.println("Date: " + new Date());
				out.println("Keep-Alive: timeout=5, max=2000");
				out.println("Connection: Keep-Alive");
				out.println("Content-Length: " + file.length());
				out.println("");
				out.println("<html>"
						+ "<head>"
						+ "<title>Index do not exist</title>"
						+ "<body>"
						+ "<h3>405 Method not Allowed</h3>"
						+ "</body>"
						+ "</head>"
						+ "</html>");
				
				out.flush();
				out.close();
				
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
		try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

}
