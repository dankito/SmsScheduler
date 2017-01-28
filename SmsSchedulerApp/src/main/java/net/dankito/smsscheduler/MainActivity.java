package net.dankito.smsscheduler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.dankito.android.util.services.AlarmManagerCronService;
import net.dankito.android.util.services.ICronService;

public class MainActivity extends AppCompatActivity {


  protected ICronService cronService;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setupDependencies();

    setupUi();
  }


  protected void setupDependencies() {
    cronService = new AlarmManagerCronService(this);
  }

  protected void setupUi() {
    setContentView(R.layout.activity_main);
  }

}
