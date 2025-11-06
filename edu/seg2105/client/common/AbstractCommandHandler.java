package edu.seg2105.client.common;

// Base class for command handlers
public abstract class AbstractCommandHandler 
{
  protected ChatIF ui;
  protected String prefix;
  
  public AbstractCommandHandler(ChatIF ui, String prefix) 
  {
    this.ui = ui;
    this.prefix = prefix;
  }
  
  // Process commands that start with prefix
  public boolean handleCommand(String message) 
  {
    if (message == null || !message.startsWith(prefix)) {
      return false;
    }
    
    String commandLine = message.substring(prefix.length()).trim();
    String[] parts = commandLine.split("\\s+");
    String command = parts[0].toLowerCase();
    
    try {
      if (processCommand(command, parts)) {
        return true;
      } else {
        ui.display("Unknown command: " + command);
      }
    } catch (Exception e) {
      ui.display("Error executing command: " + e.getMessage());
    }
    
    return true;
  }
  
  // Subclasses implement this to handle specific commands
  protected abstract boolean processCommand(String command, String[] parts) throws Exception;
}