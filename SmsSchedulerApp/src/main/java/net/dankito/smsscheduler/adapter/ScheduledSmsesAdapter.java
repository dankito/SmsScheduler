package net.dankito.smsscheduler.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.dankito.smsscheduler.MainActivity;
import net.dankito.smsscheduler.R;
import net.dankito.smsscheduler.services.ScheduledSms;
import net.dankito.smsscheduler.services.ScheduledSmsManager;
import net.dankito.smsscheduler.services.ScheduledSmses;
import net.dankito.smsscheduler.services.SchedulesSmsesListener;

import java.util.ArrayList;
import java.util.List;


public class ScheduledSmsesAdapter extends BaseAdapter {

  protected Activity activity;

  protected ScheduledSmsManager scheduledSmsManager;

  protected List<ScheduledSms> scheduledSmses = new ArrayList<>();


  public ScheduledSmsesAdapter(Activity activity, ScheduledSmsManager scheduledSmsManager) {
    this.activity = activity;
    this.scheduledSmsManager = scheduledSmsManager;

    scheduledSmsManager.addSchedulesSmsesListener(schedulesSmsesListener);
    setSchedulesSmses();
  }

  protected void setSchedulesSmses() {
    this.scheduledSmses = new ArrayList<>(scheduledSmsManager.getScheduledSMSes().getScheduledSMSes().values());

    notifyDataSetChanged();
  }


  @Override
  public int getCount() {
    return scheduledSmses.size();
  }

  @Override
  public Object getItem(int position) {
    return scheduledSmses.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if(convertView == null) {
      convertView = activity.getLayoutInflater().inflate(R.layout.list_item_scheduled_sms, parent, false);
    }

    ScheduledSms scheduledSms = (ScheduledSms)getItem(position);

    TextView txtScheduledSmsExecuteAt = (TextView)convertView.findViewById(R.id.txtScheduledSmsExecuteAt);
    txtScheduledSmsExecuteAt.setText(MainActivity.EXECUTE_AT_DATE_FORMAT.format(scheduledSms.getScheduledTime().getTime()));

    TextView txtScheduledSmsReceiverPhoneNumber = (TextView)convertView.findViewById(R.id.txtScheduledSmsReceiverPhoneNumber);
    txtScheduledSmsReceiverPhoneNumber.setText(scheduledSms.getReceiverPhoneNumber());

    TextView txtScheduledSmsMessage = (TextView)convertView.findViewById(R.id.txtScheduledSmsMessage);
    txtScheduledSmsMessage.setText(scheduledSms.getMessage());

    ImageView imgvwScheduledSmsDelete = (ImageView)convertView.findViewById(R.id.imgvwScheduledSmsDelete);
    imgvwScheduledSmsDelete.setTag(scheduledSms);
    imgvwScheduledSmsDelete.setOnClickListener(imgvwScheduledSmsDeleteClickListener);

    return convertView;
  }


  protected SchedulesSmsesListener schedulesSmsesListener = new SchedulesSmsesListener() {
    @Override
    public void scheduledSmsesChanged(ScheduledSmses scheduledSmses) {
      setSchedulesSmses();
    }
  };


  protected View.OnClickListener imgvwScheduledSmsDeleteClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      ScheduledSms scheduledSms = (ScheduledSms)view.getTag();
      scheduledSmsManager.unscheduleSms(scheduledSms);
    }
  };

}
