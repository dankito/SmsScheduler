package net.dankito.smsscheduler.services;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.dankito.android.util.model.OneTimeJobConfig;
import net.dankito.android.util.services.AlarmManagerCronService;
import net.dankito.android.util.services.AndroidFileStorageService;
import net.dankito.android.util.services.ICronService;
import net.dankito.android.util.services.IPermissionsManager;
import net.dankito.android.util.services.PermissionRequestCallback;
import net.dankito.android.util.services.SmsService;
import net.dankito.smsscheduler.R;
import net.dankito.utils.services.IFileStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class ScheduledSmsManager extends BroadcastReceiver {

  protected static final String SCHEDULES_SMSES_FILENAME = "SchedulesSmses.json";

  protected static ScheduledSmsManager instanceForApp = null; // so that BroadcastReceiver instance can access App's ScheduledSmsManager instance (e.g. for calling its listeners)

  private static final Logger log = LoggerFactory.getLogger(ScheduledSmsManager.class);


  protected Activity activity;

  protected ICronService cronService;

  protected SmsService smsService;

  /**
   * Can be null if called from BroadcastReceiver's onReceive()
   */
  protected IPermissionsManager permissionsManager;

  protected IFileStorageService fileStorageService;

  protected ScheduledSmses scheduledSMSes;

  protected List<SchedulesSmsesListener> schedulesSmsesListeners = new ArrayList<>();


  public ScheduledSmsManager() { // for being waked up by AlarmManager

  }

  public ScheduledSmsManager(Activity activity, IPermissionsManager permissionsManager) {
    this.activity = activity;
    this.permissionsManager = permissionsManager;

    instanceForApp = this;

    setupDependencies(activity);
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


  public void scheduleSms(final ScheduledSms scheduledSms) {
    String rationale = activity.getString(R.string.rationale_send_sms, scheduledSms.getReceiverPhoneNumber());

    permissionsManager.checkPermission(Manifest.permission.SEND_SMS, rationale, new PermissionRequestCallback() {
      @Override
      public void permissionCheckDone(String permission, boolean isGranted) {
        if(isGranted) {
          scheduleSmsPermissionGranted(scheduledSms);
        }
      }
    });
  }

  protected void scheduleSmsPermissionGranted(ScheduledSms scheduledSms) {
    int cronJobId = cronService.scheduleOneTimeJob(new OneTimeJobConfig(scheduledSms.getScheduledTime(), ScheduledSmsManager.class));

    scheduledSms.setScheduledSmsId(cronJobId);
    scheduledSMSes.add(scheduledSms);

    saveSchedulesSMSes();
  }

  protected void saveSchedulesSMSes() {
    try {
      fileStorageService.writeObjectToFile(scheduledSMSes, SCHEDULES_SMSES_FILENAME);
    } catch(Exception e) { log.error("Could not save scheduled SMSe", e); }

    callSchedulesSmsesListeners();
  }


  protected void sendSms(ScheduledSms scheduledSms) {
    smsService.sendTextSms(scheduledSms.getReceiverPhoneNumber(), scheduledSms.getMessage());
  }


  public void unscheduleSms(ScheduledSms scheduledSms) {
    if(cronService.cancelJob(scheduledSms.getScheduledSmsId())) {
      scheduledSMSes.remove(scheduledSms.getScheduledSmsId());
      saveSchedulesSMSes();
    }
  }


  public boolean addSchedulesSmsesListener(SchedulesSmsesListener listener) {
    return schedulesSmsesListeners.add(listener);
  }

  public boolean removeSchedulesSmsesListener(SchedulesSmsesListener listener) {
    return schedulesSmsesListeners.remove(listener);
  }

  protected void callSchedulesSmsesListeners() {
    if(instanceForApp != null) { // so that App's listener get called even though BroadcastReceiver has committed changes
      for(SchedulesSmsesListener listener : instanceForApp.schedulesSmsesListeners) {
        listener.scheduledSmsesChanged(scheduledSMSes);
      }
    }
  }


  public ScheduledSmses getScheduledSMSes() {
    return scheduledSMSes;
  }


  @Override
  public void onReceive(Context context, Intent intent) {
    log.info("Woke up from BroadcastReceiver");
    setupDependencies(context);

    int cronJobId = intent.getIntExtra(AlarmManagerCronService.CRON_JOB_TOKEN_NUMBER_EXTRA_NAME, -1);
    log.info("ScheduledSms' Id is " + cronJobId);
    if(cronJobId > 0) {
      ScheduledSms scheduledSms = scheduledSMSes.getAndRemove(cronJobId);
      if(scheduledSms != null) {
        sendSms(scheduledSms);

        if(instanceForApp != null) {
          instanceForApp.scheduledSMSes = scheduledSMSes; // update App's scheduledSMSes
        }

        saveSchedulesSMSes();
      }
    }
  }

}
