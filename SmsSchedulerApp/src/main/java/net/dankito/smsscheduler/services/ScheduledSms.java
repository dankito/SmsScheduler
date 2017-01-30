package net.dankito.smsscheduler.services;

import java.util.Calendar;


public class ScheduledSms {

  protected Calendar scheduledTime;

  protected String receiverPhoneNumber;

  protected String receiverName;

  protected String message;

  protected int cronJobId;


  protected ScheduledSms() { // for Jackson

  }

  public ScheduledSms(Calendar scheduledTime, String receiverPhoneNumber, String message) {
    this.scheduledTime = scheduledTime;
    this.receiverPhoneNumber = receiverPhoneNumber;
    this.message = message;
  }

  public ScheduledSms(Calendar scheduledTime, String receiverPhoneNumber, String receiverName, String message) {
    this(scheduledTime, receiverPhoneNumber, message);

    this.receiverName = receiverName;
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

  public String getReceiverName() {
    return receiverName;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }

  public String getMessage() {
    return message;
  }

  protected void setMessage(String message) { // for Jackson
    this.message = message;
  }

  public int getCronJobId() {
    return cronJobId;
  }

  public void setCronJobId(int cronJobId) {
    this.cronJobId = cronJobId;
  }


  @Override
  public String toString() {
    return getScheduledTime().getTime() + " " + getReceiverPhoneNumber();
  }

}
