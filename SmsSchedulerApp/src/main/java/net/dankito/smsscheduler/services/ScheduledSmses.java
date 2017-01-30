package net.dankito.smsscheduler.services;

import java.util.HashMap;
import java.util.Map;


public class ScheduledSmses {

  protected Map<Integer, ScheduledSms> scheduledSMSes = new HashMap<>();

  protected int lastScheduledSmsId = 0;


  public ScheduledSms get(int scheduledSmsId) {
    return scheduledSMSes.get(scheduledSmsId);
  }

  public boolean remove(int scheduledSmsId) {
    return getAndRemove(scheduledSmsId) != null;
  }

  public ScheduledSms getAndRemove(int scheduledSmsId) {
    return scheduledSMSes.remove(scheduledSmsId);
  }

  public void add(ScheduledSms scheduledSms) {
    scheduledSMSes.put(scheduledSms.getScheduledSmsId(), scheduledSms);

    lastScheduledSmsId = scheduledSms.getScheduledSmsId();
  }


  public Map<Integer, ScheduledSms> getScheduledSMSes() { // for Jackson
    return scheduledSMSes;
  }

  protected void setScheduledSMSes(Map<Integer, ScheduledSms> scheduledSMSes) { // for Jackson
    this.scheduledSMSes = scheduledSMSes;
  }


  public int getCount() {
    return scheduledSMSes.size();
  }

  public int getLastScheduledSmsId() {
    return lastScheduledSmsId;
  }

}
