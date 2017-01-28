package net.dankito.smsscheduler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.dankito.android.util.services.AlarmManagerCronService;
import net.dankito.android.util.services.ICronService;
import net.dankito.android.util.services.SmsService;

public class MainActivity extends AppCompatActivity {


  protected ICronService cronService;

  protected SmsService smsService;


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

    edtxReceiverPhoneNumber = (EditText)findViewById(R.id.edtxReceiverPhoneNumber);

    edtxMessageText = (EditText)findViewById(R.id.edtxMessageText);

    Button btnScheduleSms = (Button)findViewById(R.id.btnScheduleSms);
    btnScheduleSms.setOnClickListener(btnScheduleSmsClickListener);
  }


  protected void scheduleSms() {
    String receiverPhoneNumber = edtxReceiverPhoneNumber.getText().toString();
    String messageText = edtxMessageText.getText().toString();

    smsService.sendTextSms(receiverPhoneNumber, messageText);
  }


  protected View.OnClickListener btnScheduleSmsClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      scheduleSms();
    }
  };

}
