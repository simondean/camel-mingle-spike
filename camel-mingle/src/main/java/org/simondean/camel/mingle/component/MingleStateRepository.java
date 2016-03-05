package org.simondean.camel.mingle.component;

public interface MingleStateRepository {

  String getLastFeedURL(String project);

  void setLastFeedURL(String project, String lastURL);

  String getLastFeedEntryID(String project);

  void setLastFeedEntryID(String project, String lastEntryID);

}
