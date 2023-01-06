/* MiniWebserver.java

Author: Nathan Mack, using Clark Elliott's WebResponse code 

1.0  

Point your browser to:

http://localhost:2540/ 

Question Repsonses:

1. 

The server sends an HTTP header containing metadata, followed by a blank line (two carriage returns), followed by the content the server 
is sending to the browser. One of the lines in the header is the "Content-Type" line. It looks something like this: Content-Type: text/html; 
This line in the header lets the browser know what Mime-type the server is sending. This contains a type, in this case text, and a subtype,
in this case html. There are only a few types which are general categories of the type of data being sent. The subtype is more specific
and there are many subtypes.

2.

In the HTTP header, I would include the line, Content-Type: text/html;
Then, after the two carriage returns, I would properly format the html content I wished to return.

3. 

In the HTTP header, I would include the line, Content-Type: text/plain;
Then, after the two carriage returns, I would incnlude the text I wished to return.


 */

import java.io.*;  // Needed for input/output
import java.net.*; // Needed for making threads
import java.util.HashMap; //Needed for HashMap used in query string parsing method

class ListenWorker extends Thread {  
	Socket sock;                   // member variable
	ListenWorker (Socket s) {sock = s;} // constructor method

	//method to parse query string
	public static HashMap<String, String> queryStringParser (String query) {
		query = query.substring(query.indexOf("?") + 1); //remove everything before and including "?"
		String[] removequery = query.split(" "); //split on space to split "HTTP/1.1"
		query = removequery[0]; //Remove "HTTP/1.1" from query		
		String[] queryArray = query.split("&"); //split by name/value pairs
		//Create a HashMap which will story names as keys and values as values
		HashMap<String,String> dictionary = new HashMap<>();
		//loop through array, split on "=" and add to HashMap
		for (int i = 0; i < queryArray.length; i++) {
			String nameValuePair = queryArray[i];
			String[] nameValuePairArray = nameValuePair.split("=");
			dictionary.put(nameValuePairArray[0], nameValuePairArray[1]);
		}
		return dictionary;
	}
		
	public void run(){
		//setting up input output for socket
		PrintStream out = null;   
		BufferedReader in = null; 
		try {
			out = new PrintStream(sock.getOutputStream());
			in = new BufferedReader
					(new InputStreamReader(sock.getInputStream()));

			String query = in.readLine(); //obtain query string from client
			//close socket and exit thread if favicon is detected
			if (query.contains("favicon")) {
				sock.close(); //close socket
				return;
			}
			HashMap<String,String> queryDictionary = queryStringParser(query); //run method to parse query, store in a HashMap		
			String person = queryDictionary.get("person"); //Indexing HashMap to get first value in query string
			String num1 = queryDictionary.get("num1"); //Indexing HashMap to get second value in query string
			String num2 = queryDictionary.get("num2");//Indexing HashMap to get third value in query string
			Integer number1 = Integer.parseInt(num1); //convert this piece of the parsed query into an integer
			Integer number2 = Integer.parseInt(num2); //convert this piece of the parsed query into an integer
			Integer number3 = number1 + number2; //adding the two numbers together

			System.out.println("Sending the HTML Reponse now: " +
					Integer.toString(MiniWebserver.i) + "\n" );
			MiniWebserver.i++; //Increment MiniWebserver
			//HTML Response which will be sent back to client via Firefox. Includes CSS styling. This data will be sent after HTTP header is sent.
			String HTMLResponse = 
					"<HTML>" +
					//CSS styling
					"<head>" +
					"<style>" + 
					"body {background-color: rgb(30,63,102);}" +
					"h1   {color: white;}" +
					"h2    {color: white;}" +
					"h3    {color: white;}" +
					"p    {color: white;}" +
					"input {background-color: rgb(111,143,175);}" +
					"input {color: white;}" +
					"</style>" +
					"</head>" +
					//Main Body of HTML
					"<BODY>" +
					"<H1> Nathan's WebAdd </H1>" +

					"<FORM method=\"GET\" action=\"http://localhost:2540/WebAdd.fake-cgi <view-source:http://localhost:2540/WebAdd.fake-cgi>\">" +

					"<p>Enter your name and two numbers. My program will return the sum:</p>" +

					"<INPUT TYPE=\"text\" NAME=\"person\" size=20 value=\"YourName\"><P>" +

					"<INPUT TYPE=\"text\" NAME=\"num1\" size=5 value=\"4\"> <br>" +
					"<INPUT TYPE=\"text\" NAME=\"num2\" size=5 value=\"5\"> <p>" +

					"<INPUT TYPE=\"submit\" VALUE=\"Submit Numbers\">" +

					"</FORM>" +

					"<h2>" + " Hello, " + person + ".</h2>" + 
					"<h3>" + num1 + " + " + num2 + " = " + number3 + "</h3></BODY></html>";	
	
			//HTTP header contains medata 
			out.println("HTTP/1.1 200 OK");
			out.println("Connection: close"); 
			out.println("Content-Length: 1200"); 
			out.println("Content-Type: text/html \r\n\r\n"); //This line indicates the type of data being returned. Then, two carriage returns lets browser know that HTTP header has ended
			out.println(HTMLResponse); //data to be returned follows the blank line

			sock.close(); // close socket connection;
		} catch (IOException x) {
			System.out.println("Error: Connetion reset. Listening again...");
		}
	}
}

public class MiniWebserver {

	static int i = 0;

	public static void main(String[] a) throws IOException {
		int q_len = 6; //Number of requests held in queue while waiting for a connection
		int port = 2540; //Port number required by assignment for MiniWebserver
		Socket sock;
		
		//Creates a socket that will be used to connect with client
		ServerSocket servsock = new ServerSocket(port, q_len);

		System.out.println("Nathan Mack's Miniserver running at 2540.");
		System.out.println("Point Firefox browser to http://localhost:2540/abc.\n");
		while (true) {
			//continually listens, creates a new thread for each new socket
			sock = servsock.accept();
			new ListenWorker(sock).start();
		}
	}
}



