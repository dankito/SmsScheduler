package net.dankito.smsscheduler.services;

import java.util.Calendar;


public class ScheduledSms {

  protected Calendar scheduledTime;

  protected String receiverPhoneNumber;

  protected String message;

  protected int scheduledSmsId;


  protected ScheduledSms() { // for Jackson

  }

  public ScheduledSms(Calendar scheduledTime, String receiverPhoneNumber, String message) {
    this.scheduledTime = scheduledTime;
    this.receiverPhoneNumber = receiverPhoneNumber;
    this.message = message;
  }


  public Calendar getScheduledTime() {
    return scheduledTime;
  }

  protected void setScheduledTime(Calendar scheduledTime) { // for Jackson
    this.scheduledTime = scheduledTime;
  }

  public String getReceiverPhoneNumber() {
    return receiverPhoneNumber;
  }

  protected void setReceiverPhoneNumber(String receiverPhoneNumber) { // for Jackson
    this.receiverPhoneNumber = receiverPhoneNumber;
  }

  public String getMessage() {
    return message;
  }

  protected void setMessage(String message) { // for Jackson
    this.message = message;
  }

  public int getScheduledSmsId() {
    return scheduledSmsId;
  }

  public void setScheduledSmsId(int scheduledSmsId) {
    this.scheduledSmsId = scheduledSmsId;
  }
}
