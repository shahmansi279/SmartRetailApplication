package com.project;

/**
 * Created by jessiedeot on 10/11/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CallActivity extends Activity {

    private Button button;
    private EditText number;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        button = (Button) findViewById(R.id.buttonCall);
        number = (EditText) findViewById(R.id.editText1);
        // add button listener
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String phnum = number.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phnum));
                startActivity(callIntent);
            }
        });
    }

}
