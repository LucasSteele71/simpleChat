package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ServerConsol serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ServerConsol serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  String message = msg.toString();
	  if(message.startsWith("#login") && client.getInfo("login_id")==null) {
		  String[] loginmsg = msg.toString().split(" ");
		  if (loginmsg.length >= 2) {
			  String loginID = loginmsg[1];
			  client.setInfo("login_id", loginID);
			  
			  serverUI.display("Message received: #login " + loginID + " from null.");
			  serverUI.display(loginID+" has logged on");
			  
			  try {
	              client.sendToClient(loginID+" has logged on");
	          } catch (IOException e) {
	              e.printStackTrace();
	          }
		  } else {
	            try {
	                client.sendToClient("Invalid login format.");
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	      }
	  } else if (client.getInfo("login_id") != null) {
	        String loginID = (String) client.getInfo("login_id");
	        serverUI.display("Message received: " + message + " from " + loginID);
	        sendToAllClients(loginID + ": " + message);
	    } else {
	        try {
	            client.sendToClient("You must login first.");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  
  @Override
  protected void clientConnected(ConnectionToClient client){
	  serverUI.display("A new client has connected to the server");
    try
			{
				client.sendToClient("Hello");
			}
			catch (Exception ex) {}
  }

  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
		// Since we don't track which ID belongs to this client directly,
		// remove by value.
	  serverUI.display(client.getInfo("login_id")+" has disconnected");
		super.clientDisconnected(client);
    try
			{
				client.sendToClient("Goodbye");
			}
			catch (Exception ex) {}
	}
  
  public void handleMessageFromServerUI(String message)
  {
    if(message.startsWith("#")){
        handleCommand(message);
      } else{
        serverUI.display(message);
        sendToAllClients("SERVER MSG> "+message);
      }
  }
  
  private void handleCommand(String command){
	    if(command.equals("#quit")){
	    	try {
	            close();
	        } catch (IOException e) {
	            serverUI.display("Error closing server: " + e.getMessage());
	        }
	        serverUI.display("Server shutting down.");
	        System.exit(0);
	    } else if(command.equals("#stop")){
	      stopListening();
	    } else if(command.equals("#close")){
	    	try {
	            close();
	        } catch (IOException e) {
	            serverUI.display("Error closing server: " + e.getMessage());
	        }
	    }else if(command.equals("#getport")){
	      serverUI.display(String.valueOf(getPort()));
	    } else if(command.equals("#start")){
	      if(!isListening()) {
	    	  try
	    	  {
	    		  listen();
	    		  serverUI.display("Server is listening");
	    	  }
	    	  catch (IOException e) {
	                serverUI.display("Error starting server: " + e.getMessage());
	           }
	      } else {
	    	  serverUI.display("Server is already on");
	      }
	    } else if(command.startsWith("#setport")){
	      if(!isListening()){
	    	  int newPort = Integer.parseInt(command.substring(9).trim());
	        setPort(newPort);
	        serverUI.display("Port is now "+newPort);
	      }else{
	        serverUI.display("Logoff to change port");
	      }
	    } else{
	      serverUI.display("That command is not recognized");
	    }
	  }
}
//End of EchoServer class
