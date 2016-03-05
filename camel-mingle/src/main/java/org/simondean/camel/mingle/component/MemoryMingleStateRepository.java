package org.simondean.camel.mingle.component;

import org.apache.camel.support.ServiceSupport;

import java.util.HashMap;
import java.util.Map;

public class MemoryMingleStateRepository extends ServiceSupport implements MingleStateRepository {
  private Map<String, String> lastFeedURLs = new HashMap<>();
  private Map<String, String> lastFeedEntryIDs = new HashMap<>();

  @Override
  public String getLastFeedURL(String project) {
    return lastFeedURLs.get(project);
  }

  public void setLastFeedURL(String project, String lastFeedURL) {
    lastFeedURLs.put(project, lastFeedURL);
  }

  @Override
  public String getLastFeedEntryID(String project) {
    return lastFeedEntryIDs.get(project);
  }

  @Override
  public void setLastFeedEntryID(String project, String lastFeedEntryID) {
    lastFeedEntryIDs.put(project, lastFeedEntryID);
  }

  @Override
  protected void doStart() throws Exception {

  }

  @Override
  protected void doStop() throws Exception {

  }
}
