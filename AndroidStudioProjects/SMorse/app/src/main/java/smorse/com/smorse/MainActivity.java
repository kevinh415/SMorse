package smorse.com.smorse;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private static final int SEND_SMS_PERMISSIONS_REQUEST = 1;
    private static final int RECEIVE_SMS_PERMISSIONS_REQUEST = 1;

    private static MainActivity inst;

    ArrayList<String> smsMessagesList = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    SmsManager smsManager = SmsManager.getDefault();
    HashMap<String, String> alphabet = new HashMap<>();


    // interval of the vibrations
    int shortVibrationSpeed = 20;
    int longVibrationSpeed = 100;
    int waitVibrationSpeed = 500;


    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView messages = (ListView) findViewById(R.id.messages);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        messages.setAdapter(arrayAdapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadContacts();
        } else {
            refreshSmsInbox();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReceiveSMS();
        } else {
            refreshSmsInbox();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            refreshSmsInbox();
        }

        Button contactButton = findViewById(R.id.contact);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, 1);
            }

        });

        // make the phone vibrate
        Button vibrationButton = findViewById(R.id.short_button);
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        vibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        });

        // make the phone vibrate
        Button longVibrationButton = findViewById(R.id.long_button);
        longVibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        });


        final EditText phoneNumber = findViewById(R.id.phone_number);
        final EditText message = findViewById(R.id.text);

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!phoneNumber.getText().toString().equals("") && !message.getText().toString().equals("")) {
                    Snackbar.make(view, "sending message...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    onSendClick(view, phoneNumber.getText().toString(), message.getText().toString());
                }
            }
        });

        alphabet.put("A", ".-");
        alphabet.put("B", "-...");
        alphabet.put("C", "-.-.");
        alphabet.put("D", "-..");
        alphabet.put("E", ".");
        alphabet.put("F", "..-.");
        alphabet.put("G", "--.");
        alphabet.put("H", "....");
        alphabet.put("I", "..");
        alphabet.put("J", ".---");
        alphabet.put("K", "-.-");
        alphabet.put("L", ".-..");
        alphabet.put("M", "--");
        alphabet.put("N", "-.");
        alphabet.put("O", "---");
        alphabet.put("P", ".---.");
        alphabet.put("Q", "--.-");
        alphabet.put("R", ".-.");
        alphabet.put("S", "...");
        alphabet.put("T", "-");
        alphabet.put("U", "..-");
        alphabet.put("V", "...-");
        alphabet.put("W", ".--");
        alphabet.put("X", "-..-");
        alphabet.put("Y", "-.--");
        alphabet.put("Z", "--..");
        alphabet.put("1", ".----");
        alphabet.put("2", "..---");
        alphabet.put("3", "...--");
        alphabet.put("4", "....-");
        alphabet.put("5", ".....");
        alphabet.put("6", "-....");
        alphabet.put("7", "--...");
        alphabet.put("8", "---..");
        alphabet.put("9", "----.");
        alphabet.put(".", ".-.-.-");
        alphabet.put(",", "--..--");
        alphabet.put("?", "..--..");
        alphabet.put("'", "-----");
        alphabet.put("!", "-.-.--");
        alphabet.put("/", "-..-.");
        alphabet.put("(", "-.--.");
        alphabet.put(")", "-.--.-");
        alphabet.put("&", ".-...");
        alphabet.put(":", "---...");
        alphabet.put(";", "-.-.-.");
        alphabet.put("=", "-...-");
        alphabet.put("+", ".-.-.");
        alphabet.put("-", "-....-");
        alphabet.put("_", "..--.-");
        alphabet.put("$", "...-..-");
        alphabet.put("@", ".--.-.");

        alphabet.put("A", ".-");
        alphabet.put("B", "-...");
        alphabet.put("C", "-.-.");
        alphabet.put("D", "-..");
        alphabet.put("E", ".");
        alphabet.put("F", "..-.");
        alphabet.put("G", "--.");
        alphabet.put("H", "....");
        alphabet.put("I", "..");
        alphabet.put("J", ".---");
        alphabet.put("K", "-.-");
        alphabet.put("L", ".-..");
        alphabet.put("M", "--");
        alphabet.put("N", "-.");
        alphabet.put("O", "---");
        alphabet.put("P", ".---.");
        alphabet.put("Q", "--.-");
        alphabet.put("R", ".-.");
        alphabet.put("S", "...");
        alphabet.put("T", "-");
        alphabet.put("U", "..-");
        alphabet.put("V", "...-");
        alphabet.put("W", ".--");
        alphabet.put("X", "-..-");
        alphabet.put("Y", "-.--");
        alphabet.put("Z", "--..");
        alphabet.put("1", ".----");
        alphabet.put("2", "..---");
        alphabet.put("3", "...--");
        alphabet.put("4", "....-");
        alphabet.put("5", ".....");
        alphabet.put("6", "-....");
        alphabet.put("7", "--...");
        alphabet.put("8", "---..");
        alphabet.put("9", "----.");
        alphabet.put(".", ".-.-.-");
        alphabet.put(",", "--..--");
        alphabet.put("?", "..--..");
        alphabet.put("'", "-----");
        alphabet.put("!", "-.-.--");
        alphabet.put("/", "-..-.");
        alphabet.put("(", "-.--.");
        alphabet.put(")", "-.--.-");
        alphabet.put("&", ".-...");
        alphabet.put(":", "---...");
        alphabet.put(";", "-.-.-.");
        alphabet.put("=", "-...-");
        alphabet.put("+", ".-.-.");
        alphabet.put("-", "-....-");
        alphabet.put("_", "..--.-");
        alphabet.put("$", "...-..-");
        alphabet.put("@", ".--.-.");

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getApplicationContext().getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);
                // Do something with the phone number
                ((EditText) findViewById(R.id.phone_number)).setText(number);
            }

            cursor.close();
        }
    }

    public void onSendClick(View view, String phone_number, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToSendSMS();
        } else {
            // convert the message into morse code before sending
            message = convertToMorse(message);
            Snackbar.make(view, "Message sent!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            smsManager.sendTextMessage(phone_number, null, message, null, null);
        }
    }

    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        int count = 0;
        do {
            String contactName = getContactName(smsInboxCursor.getString(indexAddress).substring(2), this);
            if (contactName.equals(""))
                contactName = "Unknown";
            String str = contactName +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
            count++;
        } while (smsInboxCursor.moveToNext() && count < 20 );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            setContentView(R.layout.settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "getting permission to read sm...", Toast.LENGTH_SHORT).show();

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);
        }
    }

    public void getPermissionToSendSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "getting permission to send sms...", Toast.LENGTH_SHORT).show();

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.SEND_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSIONS_REQUEST);
        }
    }

    public void getPermissionToReceiveSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "getting permission to receive sms...", Toast.LENGTH_SHORT).show();

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.RECEIVE_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},
                    RECEIVE_SMS_PERMISSIONS_REQUEST);
        }
    }

    public void getPermissionToReadContacts() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public String cleanse(String input) {

        String cleansedInput = input.replaceAll("[^A-Za-z0-9.,?!/()&:;=+_$@'$ ]", "");
        return cleansedInput;
    }

    public String convertToMorse(String cleansedString) {

        String morseString = "";

        for (int i = 0; i < cleansedString.length(); i++) {

            String temp = cleansedString.substring(i, i + 1);

            if (temp.compareTo(" ") == 0) {

                morseString = morseString + " / ";
            } else {

                String replace = alphabet.get(temp.toUpperCase());

                morseString = morseString + replace + " ";
            }

        }
        return morseString;


    }


    // this method is called when the user gets a new message.
    public void getLatestMessage() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        // vibrate to the first string
        new Vibrations().execute(smsInboxCursor.getString(indexBody));

        do {
            String str = smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }


    // this class handles the viration notifications. Using AsyncTask because of how long it takes for the message to be received
    private class Vibrations extends AsyncTask<String, Void, String> {
        // Do the long-running work in here
        protected String doInBackground(String... params) {

            for (String s : params) {
                // call the mouseVibrate message for the message
                morseVibrate(s);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        public void morseVibrate(String message) {

            // make the phone vibrate
            final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

            // iterate through each character in the message
            for (char c : message.toCharArray()) {
                // short vibration if the character is a '.'
                if (c == '.') {
                    vibrator.vibrate(VibrationEffect.createOneShot(shortVibrationSpeed, 255));
                    try {
                        // slight pause between characters
                        Thread.sleep(waitVibrationSpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // long vibration if the character is a '-'
                else if (c == '-') {
                    vibrator.vibrate(VibrationEffect.createOneShot(longVibrationSpeed, 255));
                    try {
                        // slight pause between characters
                        Thread.sleep(waitVibrationSpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // pause if there is a space
                else if (c == ' ') {
                    try {
                        // slight pause between characters
                        Thread.sleep(waitVibrationSpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }
}