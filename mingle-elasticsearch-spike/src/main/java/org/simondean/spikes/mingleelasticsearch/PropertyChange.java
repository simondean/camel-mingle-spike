package org.simondean.spikes.mingleelasticsearch;

public class PropertyChange {
  private String newValue;
  private Object oldValue;
  private boolean changed;

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  public Object getOldValue() {
    return oldValue;
  }

  public void setOldValue(Object oldValue) {
    this.oldValue = oldValue;
  }

  public boolean getChanged() {
    return changed;
  }

  public void setChanged(boolean changed) {
    this.changed = changed;
  }
}
