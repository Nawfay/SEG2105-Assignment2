package edu.seg2105.client.backend;

import java.io.IOException;

import edu.seg2105.client.common.AbstractCommandHandler;
import edu.seg2105.client.common.ChatIF;

// Handles client commands (#quit, #login, #sethost, etc.)
public class ClientCommandHandler extends AbstractCommandHandler 
{
  private ChatClient client;
  
  public ClientCommandHandler(ChatClient client, ChatIF ui, String prefix) 
  {
    super(ui, prefix);
    this.client = client;
  }
  
  @Override
  protected boolean processCommand(String command, String[] parts) throws Exception 
  {
    switch (command) {
      case "quit":
        commandQuit();
        return true;
      case "logoff":
        commandLogoff();
        return true;
      case "sethost":
        commandSetHost(parts);
        return true;
      case "setport":
        commandSetPort(parts);
        return true;
      case "login":
        commandLogin();
        return true;
      case "gethost":
        commandGetHost();
        return true;
      case "getport":
        commandGetPort();
        return true;
      default:
        return false; // Unknown command
    }
  }
  
  private void commandQuit() throws IOException {
    try {
      client.closeConnection();
    } catch (IOException e) {
      // Ignore connection close errors
    }
    System.exit(0);
  }

  private void commandLogoff() throws IOException {
    try {
      client.closeConnection();
      ui.display("Disconnected from server");
    } catch (IOException e) {
      ui.display("Error disconnecting from server");
    }
  }

  private void commandSetHost(String[] parts) {
    if (parts.length < 2) {
      ui.display("Usage: #sethost <host>");
      return;
    }

    String host = parts[1].trim();

    if (client.isConnected()) {
      ui.display("Cannot change host while connected");
      return;
    } 
    
    client.setHost(host);
    ui.display("Host set to " + host);
  }

  private void commandSetPort(String[] parts) {
    if (parts.length < 2) {
      ui.display("Usage: #setport <port>");
      return;
    }

    try {
      int port = Integer.parseInt(parts[1].trim());
      if (client.isConnected()) {
        ui.display("Cannot change port while connected");
        return;
      }
      client.setPort(port);
      ui.display("Port set to " + port);
    } catch (NumberFormatException e) {
      ui.display("Invalid port number");
    }
  }

  // Connect to server - only if not already connected
  private void commandLogin() throws IOException {
    if (client.isConnected()) {
      ui.display("Already connected to server");
    } else {
      try {
        client.openConnection();
        ui.display("Connected to server");
      } catch (IOException e) {
        ui.display("Could not connect to server");
      }
    }
  }

  private void commandGetHost() {
    ui.display("Current host: " + client.getHost());
  }

  private void commandGetPort() {
    ui.display("Current port: " + client.getPort());
  }
}