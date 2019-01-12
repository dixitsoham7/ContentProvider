package com.soham.dixitinfotek.contentprovider;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText nameEditText, numberEditText;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEditText = findViewById(R.id.etName);
        numberEditText = findViewById(R.id.etNumber);
        saveBtn = findViewById(R.id.btnSave);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMyPermissions();
                // after permission granted insert the contact.
                insertData();
            }
        });

    }

    // check for the permissions if the sdk version is above 23
    private void checkMyPermissions() {
        if (Build.VERSION.SDK_INT > 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CONTACTS}, 0);
            }
        }
    }

    // insert data method used to insert contact details
    private void insertData() {
        String name = nameEditText.getText().toString();
        String number = numberEditText.getText().toString();
        ArrayList<ContentProviderOperation> list = new ArrayList<>();

        // inserting a raw contact in the arraylist using the RawContact.CONTENT_URI with null values
        list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        if (!name.isEmpty() && !number.isEmpty()) {
            // insert the name of the contact using the Data.CONTENT_URI with the name entered in the editText.
            list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());

            list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
            try {
                getApplicationContext().getContentResolver().
                        applyBatch(ContactsContract.AUTHORITY, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // displaying toast if contact gets saved
            Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show();
        }
        // displaying toast if fields are empty
        else Toast.makeText(this, "Fields Empty!!", Toast.LENGTH_SHORT).show();


// clearing fields
        numberEditText.setText("");
        nameEditText.setText("");
    }
}
