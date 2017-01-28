package net.dankito.smsscheduler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.dankito.android.util.services.AlarmManagerCronService;
import net.dankito.android.util.services.ICronService;
import net.dankito.android.util.services.SmsService;

public class MainActivity extends AppCompatActivity {


  protected ICronService cronService;

  protected SmsService smsService;


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
  }

}
