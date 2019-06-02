package blackjack;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
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
//      Client[] client = {new Client(ss), new Client(ss)};
      ArrayList<Client> clients = new ArrayList<>();

      for (int i = 0; i < 2; ++i) {
        Client client = new Client(ss);
        client.setName(client.receive());
        initializeHand(client);
        clients.add(client);
      }
      
      Boolean anybodyInGame;
      do {
        anybodyInGame = false;
        for (Iterator<Client> it = clients.iterator(); it.hasNext(); ) {
          Client client = it.next();
          if (client.getState() == ClientState.IN_GAME) {
              if (client.receive().equals("hit")) {  // receive client command: hit/stick
                addCardToHandAndSetState(client);
              } else { // cmd.equals("stick"
                client.setState(ClientState.STICK);
              }
          }
          switch (client.getState()) {
            case IN_GAME:
            case STICK:
              client.send(Integer.toString(client.getInHand()));
              break;
            case BUST:
              client.send(ClientState.BUST.getValue());
              break;
          }
          anybodyInGame = (anybodyInGame || (client.getState() == ClientState.IN_GAME));
        }
      } while (anybodyInGame);
      
      String winnerName = evaluateWinner(clients);
        for (Iterator<Client> it = clients.iterator(); it.hasNext(); ) {
        it.next().send(winnerName);
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
  
  private static String evaluateWinner(ArrayList<Client> clients) {
    String name = "Dealer/None";
    int maxInHand = 0;
        for (Iterator<Client> it = clients.iterator(); it.hasNext(); ) {
      Client client = it.next();
      if (client.getState() != ClientState.BUST && client.getInHand() > maxInHand) {
        maxInHand = client.getInHand();
        name = client.getName();
      }
    }
    return name;
  }

  private static int drawCard() {
    //return (int)(2.0 + 9 * Math.random());
    return (new Random()).nextInt(9) + 2;
  }
}
