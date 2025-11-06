package edu.seg2105.edu.server.backend;

import java.util.Scanner;

import edu.seg2105.client.common.ChatIF;

public class ServerConsol implements ChatIF{
	
	final public static int DEFAULT_PORT = 5555;
	
	EchoServer server;
	
	Scanner fromConsole; 
	
	public ServerConsol(int port) 
	  {
	    server= new EchoServer(port, this);
	    
	    // Create scanner object to read from console
	    fromConsole = new Scanner(System.in); 
	  }
	

	public static void main(String[] args) 
	  {
	    int port = DEFAULT_PORT; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
	    ServerConsol chat= new ServerConsol(port);
	    
	    try 
	    {
	      chat.server.listen(); //Start listening for connections
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println("ERROR - Could not listen for clients!");
	      ex.printStackTrace();
	    }
	    
	    chat.accept(); 
	  }
	
	public void accept() 
	  {
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }
	
	@Override
	public void display(String message) 
	  {
	    System.out.println("SERVER MSG> " + message);
	  }

}
