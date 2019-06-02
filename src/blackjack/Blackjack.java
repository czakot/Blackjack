package blackjack;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author czakot
 */
public class Blackjack {

  static final int PORT = 2121;
  
  @SuppressWarnings("empty-statement")
  public static void main(String[] args) {
    try
      (
        ServerSocket ss = new ServerSocket(PORT);
      )
    {
      Client[] client = {new Client(ss), new Client(ss)};

      int i = client.length;
      while (i-- != 0) {
        client[i].setName(client[i].receive());
        initializeHand(client[i]);
      }
      System.out.println("nev1: " + client[0].getName()+ "   nev2: " + client[1].getName());
      
      String winnerName = null;
      
      while (winnerName == null) {
        Boolean nobodyInGame = null;
        i = client.length;
        while (i-- != 0) {
          nobodyInGame = true;
          Client cl = client[i];
          switch (cl.getState()) {
            case IN_GAME:
              nobodyInGame = false;
              if (cl.receive().equals("hit")) {  // receive client command: hit/stick
                addCardToHand(cl);
              } else { // cmd.equals("stick"
                cl.setState(ClientState.STICK);
              }
              break;
            case STICK:
              cl.send(Integer.toString(cl.getInHand()));
              break;
            case BUST:
              cl.send(ClientState.BUST.getValue());
              break;
          }
        }
        if (nobodyInGame) {
          winnerName = evaluateWinner(client);
        }
      }
      
      i = client.length;
      while (i-- != 0) {
        client[i].send(winnerName);
      }
    } catch (IOException ex) {
      Logger.getLogger(Blackjack.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private static void initializeHand(Client client) {
    int cardValueDrawn = drawCard() + drawCard();
    client.setInHand(cardValueDrawn);
    client.send(Integer.toString(cardValueDrawn));
  }
  
  private static void addCardToHand(Client client) {
    int cardValueDrawn = drawCard();
    int inHand = client.getInHand() + cardValueDrawn;
    client.setInHand(inHand);
    client.send(Integer.toString(cardValueDrawn));
    if (inHand <= 21) {
      client.send(Integer.toString(inHand));
    } else {
      client.setState(ClientState.BUST);
      client.send(ClientState.BUST.getValue());
    }
  }
  
  private static String evaluateWinner(Client[] client) {
    String name = "Dealer/None";
    int maxInHand = 0;
    int i = client.length;
    while (i-- != 0) {
      Client cl = client[i];
      if (cl.getState() != ClientState.BUST && cl.getInHand() > maxInHand) {
        maxInHand = cl.getInHand();
        name = cl.getName();
      }
    }
    return name;
  }

  private static int drawCard() {
    //return (int)(2.0 + 9 * Math.random());
    return (new Random()).nextInt(9) + 2;
  }
}
