package net.dankito.smsscheduler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.dankito.android.util.controls.DateTimePicker;
import net.dankito.android.util.controls.DateTimePickerCallback;
import net.dankito.android.util.services.AlarmManagerCronService;
import net.dankito.android.util.services.ICronService;
import net.dankito.android.util.services.SmsService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

  protected static final DateFormat EXECUTE_AT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");


  protected ICronService cronService;

  protected SmsService smsService;


  protected TextView txtvwExecuteAt;

  protected EditText edtxReceiverPhoneNumber;

  protected EditText edtxMessageText;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setupDependencies();

    setupUi();
  }


  protected void setupDependencies() {
    cronService = new AlarmManagerCronService(this);
    smsService = new SmsService();
  }

  protected void setupUi() {
    setContentView(R.layout.activity_main);

    txtvwExecuteAt = (TextView)findViewById(R.id.txtvwExecuteAt);
    txtvwExecuteAt.setOnClickListener(txtvwExecuteAtClickListener);
    setExecuteAtToInitialValue();

    edtxReceiverPhoneNumber = (EditText)findViewById(R.id.edtxReceiverPhoneNumber);

    edtxMessageText = (EditText)findViewById(R.id.edtxMessageText);

    Button btnScheduleSms = (Button)findViewById(R.id.btnScheduleSms);
    btnScheduleSms.setOnClickListener(btnScheduleSmsClickListener);
  }

  protected void setExecuteAtToInitialValue() {
    Calendar nowPlusFiveMinutes = Calendar.getInstance();
    nowPlusFiveMinutes.setTimeInMillis(System.currentTimeMillis());
    nowPlusFiveMinutes.add(Calendar.MINUTE, 5);

    setExecuteAt(nowPlusFiveMinutes);
  }

  protected void setExecuteAt(Calendar executeAt) {
    String formattedExecutedAt = EXECUTE_AT_DATE_FORMAT.format(executeAt.getTime());

    txtvwExecuteAt.setText(formattedExecutedAt);
    txtvwExecuteAt.setTag(executeAt);
  }


  protected void scheduleSms() {
    String receiverPhoneNumber = edtxReceiverPhoneNumber.getText().toString();
    String messageText = edtxMessageText.getText().toString();

    smsService.sendTextSms(receiverPhoneNumber, messageText);
  }


  protected View.OnClickListener txtvwExecuteAtClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Calendar currentValue = (Calendar)txtvwExecuteAt.getTag();

      DateTimePicker dateTimePicker = new DateTimePicker();
      dateTimePicker.pickDateTime(MainActivity.this, currentValue, new DateTimePickerCallback() {
        @Override
        public void selectingDateTimeFinished(boolean hasAValueBeenSelected, Calendar selectedValue) {
          if(hasAValueBeenSelected) {
            setExecuteAt(selectedValue);
          }
        }
      });
    }
  };

  protected View.OnClickListener btnScheduleSmsClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      scheduleSms();
    }
  };

}
