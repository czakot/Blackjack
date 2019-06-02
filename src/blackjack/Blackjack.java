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
      
      Boolean anybodyInGame;
      do {
        anybodyInGame = false;
        i = client.length;
        while (i-- != 0) {
          if (client[i].getState() == ClientState.IN_GAME) {
              if (client[i].receive().equals("hit")) {  // receive client command: hit/stick
                addCardToHandAndSetState(client[i]);
              } else { // cmd.equals("stick"
                client[i].setState(ClientState.STICK);
              }
          }
          switch (client[i].getState()) {
            case IN_GAME:
            case STICK:
              client[i].send(Integer.toString(client[i].getInHand()));
              break;
            case BUST:
              client[i].send(ClientState.BUST.getValue());
              break;
          }
          anybodyInGame = (anybodyInGame || (client[i].getState() == ClientState.IN_GAME));
        }
      } while (anybodyInGame);
      
      String winnerName = evaluateWinner(client);
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
  
  private static void addCardToHandAndSetState(Client client) {
    int cardValueDrawn = drawCard();
    int inHand = client.getInHand() + cardValueDrawn;
    client.setInHand(inHand);
    client.send(Integer.toString(cardValueDrawn));
    if (inHand > 21) {
      client.setState(ClientState.BUST);
    }
  }
  
  private static String evaluateWinner(Client[] client) {
    String name = "Dealer/None";
    int maxInHand = 0;
    int i = client.length;
    while (i-- != 0) {
      Client cl = client[i];
      if (cl.getState() != ClientState.BUST && cl.getInHand() >= maxInHand) {
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
