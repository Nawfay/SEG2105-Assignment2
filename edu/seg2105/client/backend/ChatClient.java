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
  
  /**
   * The command handler for processing client commands.
   */
  ClientCommandHandler commandHandler; 

  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, String loginID, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.loginID = loginID;
    this.clientUI = clientUI;
    this.commandHandler = new ClientCommandHandler(this, clientUI, "#");
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
    clientUI.display(msg.toString());
    
    
  }

  /**
   * Hook method called after the connection has been closed.
   */
  public void connectionClosed() {
    clientUI.display("Connection to server has been closed. (1)");
  }

  /**
   * Hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server.
   */
  public void connectionException(Exception exception) {
    clientUI.display("Connection to server has been closed. (2)");
  }

  /**
   * Hook method called after a connection has been established.
   */
  protected void connectionEstablished() {
    try {
      sendToServer("#login " + loginID);
    } catch (IOException e) {
      clientUI.display("Could not send login ID to server.");
    }
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    // Check if this is a command
    if (commandHandler.handleCommand(message)) {
      return; // Command was processed, don't send to server
    }
    
    // Regular message - send to server
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
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
