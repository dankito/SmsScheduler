package net.dankito.smsscheduler.services;

import java.util.HashMap;
import java.util.Map;


public class ScheduledSmses {

  protected Map<Integer, ScheduledSms> scheduledSMSes = new HashMap<>();


  public ScheduledSms get(int scheduledSmsId) {
    return scheduledSMSes.get(scheduledSmsId);
  }

  public ScheduledSms getAndRemove(int scheduledSmsId) {
    ScheduledSms scheduledSms = scheduledSMSes.remove(scheduledSmsId);

    return scheduledSms;
  }

  public void add(ScheduledSms scheduledSms) {
    scheduledSMSes.put(scheduledSms.getScheduledSmsId(), scheduledSms);
  }


  public Map<Integer, ScheduledSms> getScheduledSMSes() { // for Jackson
    return scheduledSMSes;
  }

  protected void setScheduledSMSes(Map<Integer, ScheduledSms> scheduledSMSes) { // for Jackson
    this.scheduledSMSes = scheduledSMSes;
  }

}
