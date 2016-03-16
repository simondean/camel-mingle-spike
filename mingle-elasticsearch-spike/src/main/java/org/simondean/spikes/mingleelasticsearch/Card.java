package org.simondean.spikes.mingleelasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Card {
  private String projectName;
  private int number;
  private Instant timestamp;
  private String name;
  private Map<String, Object> properties = new HashMap<>();

  public Card() {
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  @JsonProperty("@timestamp")
  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }
}
