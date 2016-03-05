package org.simondean.spikes.mingleelasticsearch;

public class ElasticsearchRepository implements Repository {
  @Override
  public Card getCard(int number) {
    System.out.println("Get card " + number);
    return null;
  }

  @Override
  public void insertCard(Card card) {
    System.out.println("Insert card " + card.getNumber());
  }

  @Override
  public void updateCard(Card card) {
    System.out.println("Update card " + card.getNumber());
  }
}
