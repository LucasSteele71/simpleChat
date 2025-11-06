// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  private String login_id;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String login_id, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.login_id = login_id;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    try{
      clientUI.display(msg.toString());
    }
    
    catch(Exception e)
    {
      connectionException(e);
    }
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if(message.startsWith("#")){
        handleCommand(message);
      } else{
        sendToServer(message);
      }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  private void handleCommand(String command){
    if(command.equals("#quit")){
      quit();
    } else if(command.equals("#logoff")){
      try {
        closeConnection();
        clientUI.display("Connection closed");
      } catch (Exception e) {
        clientUI.display("Error closing");
      }
    } else if(command.equals("#gethost")){
      clientUI.display(getHost());
    }else if(command.equals("#getport")){
      clientUI.display(String.valueOf(getPort()));
    } else if(command.equals("#login")){
      if(!isConnected()){
        try {
          openConnection();
          clientUI.display("Logged on successfully");
        } catch (IOException e) {
          clientUI.display("Connection Error");
        }
      }
    } else if(command.startsWith("#sethost")){
      if(!isConnected()){
        setHost(command.substring(8,command.length()-2));
      } else{
        clientUI.display("Logoff to change host");
      }
    }else if(command.startsWith("#setport")){
      if(!isConnected()){
    	int newPort = Integer.parseInt(command.substring(9).trim());
        setPort(newPort);
        clientUI.display("Port is now "+newPort);
      }else{
        clientUI.display("Logoff to change port");
      }
    } else{
      clientUI.display("That command is not recognized");
    }
  }

  /**
	 * Implements the Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  @Override
  protected void connectionClosed(){
    clientUI.display("Connection Closed");
  }

  /**
	 * Implements the Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
	protected void connectionException(Exception exception) {
    clientUI.display("The server is shut down ");
    System.exit(0);
	}
  
  @Override
  protected void connectionEstablished() {
	  try
	  {
		  sendToServer("#login " + login_id);
		  clientUI.display("DEBUG: Sent login message: #login " + login_id);
	  } catch(IOException e){
		  clientUI.display("Error connecting to server ");
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class