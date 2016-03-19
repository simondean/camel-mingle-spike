package org.simondean.spikes.mingleelasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CardEvent {
  private int eventId;
  private Card card;
  private Instant timestamp;
  private Map<String, PropertyChange> propertyChanges = new HashMap<>();

  public int getEventId() {
    return eventId;
  }

  public void setEventId(int eventId) {
    this.eventId = eventId;
  }

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  @JsonProperty("@timestamp")
  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public Map<String, PropertyChange> getPropertyChanges() {
    return propertyChanges;
  }
}
