package smorse.com.smorse;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReceiveSMS();
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


        // floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "sending message...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                onSendClick(view, phoneNumber.getText().toString(), message.getText().toString());
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


    public void onSendClick(View view,String phone_number, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToSendSMS();
        } else {
            // convert the message into morse code before sending
            message = convertToMorse(message);
            Snackbar.make(view, "Message sent!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            smsManager.sendTextMessage(phone_number, null, message, null, null);
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
        }
    }




    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String str = smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
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


    public String cleanse(String input){

        String cleansedInput = input.replaceAll("[^A-Za-z0-9.,?!/()&:;=+_$@'$ ]", "");
        return cleansedInput;
    }

    public String convertToMorse(String cleansedString){

        String morseString = "";

        for(int i = 0; i < cleansedString.length(); i++){

            String temp = cleansedString.substring(i, i+1);

            if(temp.compareTo(" ") == 0){

                morseString = morseString + " / ";
            } else {

                String replace = alphabet.get(temp.toUpperCase());

                morseString = morseString + replace + " ";
            }

        }
        return morseString;


    }






}
