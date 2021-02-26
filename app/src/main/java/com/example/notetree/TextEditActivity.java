package com.example.notetree;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class TextEditActivity extends AppCompatActivity {

    public TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);

        text = findViewById(R.id.text);

        ((TextView) findViewById(R.id.locationText)).setText(MainActivity.currentFile.getName());

        readFromFile();
    }

    @Override
    protected void onPause() {
        writeToFile(text.getText().toString());
        super.onPause();
    }

    public void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(MainActivity.currentFile));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(MainActivity.LOGTAG, e.toString());
        }
    }

    public void readFromFile() {

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(MainActivity.currentFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            Log.e(MainActivity.LOGTAG, e.toString());
        }

        //Find the view by its id
        TextView tv = findViewById(R.id.text);

        //Set the text
        tv.setText(text.toString());
    }
}
