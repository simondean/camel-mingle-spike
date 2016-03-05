package org.simondean.spikes.mingleelasticsearch;

public interface Repository {
  Card getCard(int number);

  void insertCard(Card card);

  void updateCard(Card card);
}
