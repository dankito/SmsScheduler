package net.dankito.smsscheduler.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.dankito.android.util.model.OneTimeJobConfig;
import net.dankito.android.util.services.AlarmManagerCronService;
import net.dankito.android.util.services.AndroidFileStorageService;
import net.dankito.android.util.services.ICronService;
import net.dankito.android.util.services.SmsService;
import net.dankito.utils.services.IFileStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ScheduledSmsManager extends BroadcastReceiver {

  protected static final String SCHEDULES_SMSES_FILENAME = "SchedulesSmses.json";

  private static final Logger log = LoggerFactory.getLogger(ScheduledSmsManager.class);


  protected ICronService cronService;

  protected SmsService smsService;

  protected IFileStorageService fileStorageService;

  protected ScheduledSmses scheduledSMSes;


  public ScheduledSmsManager() { // for being waked up by AlarmManager

  }

  public ScheduledSmsManager(Context context) {
    setupDependencies(context);
  }

  protected synchronized void setupDependencies(Context context) {
    this.fileStorageService = new AndroidFileStorageService(context);

    try {
      scheduledSMSes = fileStorageService.readObjectFromFile(SCHEDULES_SMSES_FILENAME, ScheduledSmses.class);
    } catch(Exception e) {
      scheduledSMSes = new ScheduledSmses();
      log.error("Could not read schedules SMSes from file", e);
    }

    this.smsService = new SmsService();

    this.cronService = new AlarmManagerCronService(context, scheduledSMSes.getHighestSchedulesSmsId());
  }


  public void scheduleSms(ScheduledSms scheduledSms) {
    int cronJobId = cronService.scheduleOneTimeJob(new OneTimeJobConfig(scheduledSms.getScheduledTime(), ScheduledSmsManager.class));

    scheduledSms.setScheduledSmsId(cronJobId);
    scheduledSMSes.add(scheduledSms);

    saveSchedulesSMSes();
  }

  protected void saveSchedulesSMSes() {
    try {
      fileStorageService.writeObjectToFile(scheduledSMSes, SCHEDULES_SMSES_FILENAME);
    } catch(Exception e) { log.error("Could not save scheduled SMSe", e); }
  }


  protected void sendSms(ScheduledSms scheduledSms) {
    smsService.sendTextSms(scheduledSms.getReceiverPhoneNumber(), scheduledSms.getMessage());
  }


  @Override
  public void onReceive(Context context, Intent intent) {
    setupDependencies(context);

    int cronJobId = intent.getIntExtra(AlarmManagerCronService.CRON_JOB_TOKEN_NUMBER_EXTRA_NAME, -1);
    if(cronJobId > 0) {
      ScheduledSms scheduledSms = scheduledSMSes.getAndRemove(cronJobId);
      if(scheduledSms != null) {
        sendSms(scheduledSms);
        saveSchedulesSMSes();
      }
    }
  }

}
