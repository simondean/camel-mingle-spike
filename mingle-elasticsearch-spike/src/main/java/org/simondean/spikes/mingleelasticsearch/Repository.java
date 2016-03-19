package org.simondean.spikes.mingleelasticsearch;

import java.io.IOException;

public interface Repository {
  void init() throws IOException;

  Card getCard(String projectName, int cardNumber) throws IOException;

  void upsertCard(Card card) throws IOException;

  void insertCardEvent(CardEvent cardEvent) throws IOException;
}
