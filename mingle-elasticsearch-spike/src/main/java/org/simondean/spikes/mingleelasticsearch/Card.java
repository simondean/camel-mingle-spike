package org.simondean.spikes.mingleelasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Card {
  private String projectName;
  private int number;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

//  @JsonProperty("other")
  public Map<String, Object> getProperties() {
    return properties;
  }
}
