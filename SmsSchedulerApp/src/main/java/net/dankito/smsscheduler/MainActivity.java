package net.dankito.smsscheduler;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.dankito.android.util.controls.DateTimePicker;
import net.dankito.android.util.controls.DateTimePickerCallback;
import net.dankito.android.util.services.IPermissionsManager;
import net.dankito.android.util.services.PermissionRequestCallback;
import net.dankito.android.util.services.PermissionsManager;
import net.dankito.smsscheduler.adapter.ScheduledSmsesAdapter;
import net.dankito.smsscheduler.services.ScheduledSms;
import net.dankito.smsscheduler.services.ScheduledSmsManager;
import net.dankito.smsscheduler.services.ScheduledSmses;
import net.dankito.smsscheduler.services.SchedulesSmsesListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

  protected static final int PICK_CONTACT_REQUEST_ID = 27388;

  public static final DateFormat EXECUTE_AT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");


  protected ScheduledSmsManager scheduledSmsManager;

  protected IPermissionsManager permissionsManager;


  protected RelativeLayout rlytScheduledSmses;

  protected TextView txtvwExecuteAt;

  protected EditText edtxReceiverPhoneNumber;

  protected TextView txtvwReceiverNameFromContacts;

  protected EditText edtxMessageText;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setupDependencies();

    setupUi();
  }


  protected void setupDependencies() {
    permissionsManager = new PermissionsManager(this);

    scheduledSmsManager = new ScheduledSmsManager(this, permissionsManager);
    scheduledSmsManager.addSchedulesSmsesListener(schedulesSmsesListener);
  }

  protected void setupUi() {
    setContentView(R.layout.activity_main);

    rlytScheduledSmses = (RelativeLayout)findViewById(R.id.rlytScheduledSmses);
    rlytScheduledSmses.setVisibility(scheduledSmsManager.getScheduledSMSes().getCount() > 0 ? View.VISIBLE : View.GONE);

    ListView lstvwScheduledSmses = (ListView)findViewById(R.id.lstvwScheduledSmses);
    lstvwScheduledSmses.setAdapter(new ScheduledSmsesAdapter(this, scheduledSmsManager));

    txtvwExecuteAt = (TextView)findViewById(R.id.txtvwExecuteAt);
    txtvwExecuteAt.setOnClickListener(txtvwExecuteAtClickListener);
    setExecuteAtToInitialValue();

    edtxReceiverPhoneNumber = (EditText)findViewById(R.id.edtxReceiverPhoneNumber);
    edtxReceiverPhoneNumber.addTextChangedListener(edtxReceiverPhoneNumberTextWatcher);

    Button btnPickReceiverPhoneNumberFromContacts = (Button)findViewById(R.id.btnPickReceiverPhoneNumberFromContacts);
    btnPickReceiverPhoneNumberFromContacts.setOnClickListener(btnPickReceiverPhoneNumberFromContactsClickListener);

    txtvwReceiverNameFromContacts = (TextView)findViewById(R.id.txtvwReceiverNameFromContacts);

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


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch(requestCode) {
      case PICK_CONTACT_REQUEST_ID:
        if (resultCode == Activity.RESULT_OK) {
          getPhoneNumberFromSelectedContact(data);
        }
        break;
    }
  }

  protected void getPhoneNumberFromSelectedContact(Intent data) {
    Uri contactData = data.getData();
    Cursor contactCursor = getContentResolver().query(contactData, null, null, null, null);
    if(contactCursor.moveToFirst()) {
      String id = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

      int hasPhone = contactCursor.getInt(contactCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
      if(hasPhone > 0) {
        Cursor phones = getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            new String[] { id }, null);

        if(phones.moveToFirst()) {
          String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
          edtxReceiverPhoneNumber.setText(phoneNumber);

          txtvwReceiverNameFromContacts.setText(contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
          txtvwReceiverNameFromContacts.setVisibility(View.VISIBLE);
        }
      }
    }
  }


  protected void pickReceiverFromContacts() {
    permissionsManager.checkPermission(Manifest.permission.READ_CONTACTS, R.string.rationale_read_contacts, new PermissionRequestCallback() {
      @Override
      public void permissionCheckDone(String permission, boolean isGranted) {
        if(isGranted) {
          Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
          startActivityForResult(intent, PICK_CONTACT_REQUEST_ID);
        }
      }
    });
  }

  protected void scheduleSms() {
    Calendar executeAt = (Calendar)txtvwExecuteAt.getTag();
    String receiverPhoneNumber = edtxReceiverPhoneNumber.getText().toString();
    String messageText = edtxMessageText.getText().toString();

    scheduledSmsManager.scheduleSms(new ScheduledSms(executeAt, receiverPhoneNumber, messageText));
  }


  protected SchedulesSmsesListener schedulesSmsesListener = new SchedulesSmsesListener() {
    @Override
    public void scheduledSmsesChanged(ScheduledSmses scheduledSmses) {
      rlytScheduledSmses.setVisibility(scheduledSmses.getCount() > 0 ? View.VISIBLE : View.GONE);
    }
  };

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

  protected TextWatcher edtxReceiverPhoneNumberTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      txtvwReceiverNameFromContacts.setVisibility(View.GONE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };

  protected View.OnClickListener btnPickReceiverPhoneNumberFromContactsClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      pickReceiverFromContacts();
    }
  };

  protected View.OnClickListener btnScheduleSmsClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      scheduleSms();
    }
  };

}
