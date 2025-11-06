package edu.seg2105.edu.server.backend;

import java.io.IOException;

import edu.seg2105.client.common.AbstractCommandHandler;
import edu.seg2105.client.common.ChatIF;

// Handles server operator commands (#quit, #stop, etc.)
public class ServerCommandHandler extends AbstractCommandHandler 
{
  private EchoServer server;
  
  public ServerCommandHandler(EchoServer server, ChatIF ui, String prefix) 
  {
    super(ui, prefix);
    this.server = server;
  }
  @Override
  protected boolean processCommand(String command, String[] parts) throws Exception 
  {
    switch (command) {
      case "quit":
        commandQuit();
        return true;
      case "stop":
        commandStop();
        return true;
      case "close":
        commandClose();
        return true;
      case "setport":
        commandSetPort(parts);
        return true;
      case "start":
        commandStart();
        return true;
      case "getport":
        commandGetPort();
        return true;
      default:
        return false; // Unknown command
    }
  }
  
  private void commandQuit() throws IOException {
    ui.display("Server shutting down...");
    try {
      server.close();
    } catch (IOException e) {
      // Ignore close errors
    }
    System.exit(0);
  }

  private void commandStop() throws IOException {
    if (!server.isListening()) {
      ui.display("Server is not currently listening");
    } else {
      server.stopListening();
    }
  }

  // Stop listening and disconnect all clients
  private void commandClose() throws IOException {
    if (server.isListening()) {
      server.stopListening();
    }
    
    try {
      server.close();
    } catch (IOException e) {
      ui.display("Error closing server: " + e.getMessage());
    }
  }

  // Set port - only when server is closed
  private void commandSetPort(String[] parts) {
    if (parts.length < 2) {
      ui.display("Usage: #setport <port>");
      return;
    }

    try {
      int port = Integer.parseInt(parts[1].trim());
      
      if (server.isListening()) {
        ui.display("Cannot change port while server is listening. Use #close first.");
        return;
      }
      
      server.setPort(port);
      ui.display("Port set to " + port);
    } catch (NumberFormatException e) {
      ui.display("Invalid port number");
    }
  }

  // Start listening - only if stopped
  private void commandStart() throws IOException {
    if (server.isListening()) {
      ui.display("Server is already listening");
    } else {
      try {
        server.listen();
        ui.display("Server started listening for connections on port " + server.getPort());
      } catch (IOException e) {
        ui.display("Could not start server: " + e.getMessage());
      }
    }
  }

  private void commandGetPort() {
    ui.display("Current port: " + server.getPort());
  }
}