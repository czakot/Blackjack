package blackjack;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author czakot
 */
public class Client implements AutoCloseable {
  private ServerSocket ss;
  private Socket s;
  private Scanner sc;
  private PrintWriter pw;
  private String name;
  private int inHand;
  private ClientState state;
  
  Client(ServerSocket ss) {
    this.ss = ss;
    state = ClientState.IN_GAME;
    try {
      s = ss.accept();
      sc = new Scanner(s.getInputStream());
      pw = new PrintWriter(s.getOutputStream());
    } catch (IOException ex) {
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  void send(String msg) {
    pw.println(msg);
    pw.flush();
  }
  
  @SuppressWarnings("empty-statement")
  String receive() {
    while (!sc.hasNextLine());
    return sc.nextLine();
  }
  
  @Override
  public void close() {
    sc.close();
    pw.close();
  }

  public Scanner getSc() {
    return sc;
  }

  public PrintWriter getPw() {
    return pw;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getInHand() {
    return inHand;
  }

  public void setInHand(int inHand) {
    this.inHand = inHand;
  }

  public ClientState getState() {
    return state;
  }

  public void setState(ClientState state) {
    this.state = state;
  }
}