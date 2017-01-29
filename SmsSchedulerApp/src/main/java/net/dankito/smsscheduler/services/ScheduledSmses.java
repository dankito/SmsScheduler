package net.dankito.smsscheduler.services;

import java.util.ArrayList;
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


  public int getCount() {
    return scheduledSMSes.size();
  }

  public int getHighestSchedulesSmsId() {
    int highestId = 0;

    for(int id : new ArrayList<>(scheduledSMSes.keySet())) {
      if(id > highestId) {
        highestId = id;
      }
    }

    return highestId;
  }

}
