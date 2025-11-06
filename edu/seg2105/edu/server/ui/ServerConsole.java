package edu.seg2105.edu.server.ui;

import java.util.Scanner;

import edu.seg2105.edu.server.backend.EchoServer;
import edu.seg2105.edu.server.backend.ServerCommandHandler;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for the server. It implements the
 * chat interface in order to activate the display() method.
 * This class is analogous to ClientConsole.
 *
 * @author Student
 */
public class ServerConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the server that created this ServerConsole.
   */
  EchoServer server;
  
  /**
   * The command handler for processing server commands.
   */
  ServerCommandHandler commandHandler;
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ServerConsole UI.
   *
   * @param port The port to listen on.
   */
  public ServerConsole(int port) 
  {
    server = new EchoServer(port, this);
    commandHandler = new ServerCommandHandler(server, this, "#");
    
    try 
    {
      server.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console. Once it is 
   * received, it sends it to the server's message handler.
   */
  public void accept() 
  {
    try
    {
      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        handleMessageFromServerUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  /**
   * This method handles messages from the server UI.
   * It checks for commands first, then handles regular messages.
   *
   * @param message The message from the server UI.
   */
  public void handleMessageFromServerUI(String message)
  {
    // Check if this is a command
    if (commandHandler.handleCommand(message)) {
      return; // Command was processed, don't send to clients
    }
    
    // Regular message - prefix and send to all clients
    String serverMessage = "SERVER MSG> " + message;
    
    // Display on server console
    display(serverMessage);
    
    // Send to all clients
    server.sendToAllClients(serverMessage);
  }

  /**
   * This method overrides the method in the ChatIF interface. It
   * displays a message onto the server console.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println(message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Server UI.
   *
   * @param args[0] The port number to listen on. Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
    
    ServerConsole serverConsole = new ServerConsole(port);
    serverConsole.accept(); //Wait for console data
  }
}