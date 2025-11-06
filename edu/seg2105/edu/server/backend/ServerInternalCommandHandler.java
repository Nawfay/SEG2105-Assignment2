package edu.seg2105.edu.server.backend;

import java.io.IOException;

import ocsf.server.ConnectionToClient;

// Handles commands FROM clients TO server (like #login)
public class ServerInternalCommandHandler 
{
  private EchoServer server;
  private String prefix;
  
  public ServerInternalCommandHandler(EchoServer server, String prefix) 
  {
    this.server = server;
    this.prefix = prefix;
  }
  
  // Process internal commands from clients
  public boolean handleClientMessage(String message, ConnectionToClient client) 
  {
    if (message == null || !message.startsWith(prefix)) {
      return false; // Not an internal command
    }

    
    // Remove the prefix and split into command and arguments
    String commandLine = message.substring(prefix.length()).trim();
    String[] parts = commandLine.split("\\s+");
    String command = parts[0].toLowerCase();
    
    try {
      switch (command) {
        case "login":
          handleLogin(parts, client);
          return true;
        default:
          // Unknown internal command - let server handle it normally
          return false;
      }
    } catch (Exception e) {
      System.out.println("Error processing internal command from client: " + e.getMessage());
      return true; // Command was processed (even if it failed)
    }
  }
  
  // Handle login command - only allowed as first command
  private void handleLogin(String[] parts, ConnectionToClient client) 
  {
    // Check if client is already logged in
    if (client.getInfo("loginID") != null) {
      try {
        client.sendToClient("ERROR - You are already logged in. Terminating connection.");
        client.close();
      } catch (IOException e) {
        System.out.println("Could not send error message or close connection for client");
      }
      return;
    }
    
    // Check if login ID is provided
    if (parts.length < 2) {
      try {
        client.sendToClient("ERROR - No login ID provided. Terminating connection.");
        client.close();
      } catch (IOException e) {
        System.out.println("Could not send error message or close connection for client");
      }
      return;
    }
    
    String loginID = parts[1];
    
    // Store the login ID in the client's info
    client.setInfo("loginID", loginID);
    
    // Mark that the client has logged in (for first command validation)
    client.setInfo("hasLoggedIn", true);
    
    // Print that the message was reciveid because the test case wnats it 
    System.out.println("Message received: #login " + loginID + " from " + client);
    
    // Log the login on server console
    System.out.println("Client " + client + " logged in as: " + loginID);
    
    // Send confirmation to client
    try {
      client.sendToClient("Login successful - Welcome " + loginID + "!");
    } catch (IOException e) {
      System.out.println("Could not send login confirmation to client");
    }
    
    // Notify all other clients about the new user
    server.sendToAllClients(loginID + " has joined the chat.");
  }
}