package org.simondean.spikes.mingleelasticsearch;

public class Main {
  public static void main(String[] args) {
    Importer importer = new Importer();
    try {
      importer.run("https", getHost(), 443, getUsername(), getPassword(), getProjectName());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getHost() {
    return getSiteName() + ".mingle-api.thoughtworks.com";
  }

  private static String getSiteName() {
    return System.getenv("MINGLE_SITE_NAME");
  }

  private static String getUsername() {
    return System.getenv("MINGLE_USERNAME");
  }

  private static String getPassword() {
    return System.getenv("MINGLE_PASSWORD");
  }

  public static String getProjectName() {
    return System.getenv("MINGLE_PROJECT_NAME");
  }

}
