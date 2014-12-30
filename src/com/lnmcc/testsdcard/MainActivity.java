package com.lnmcc.testsdcard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = "TestSDCard";

    /*
     * The path where 3rd APP can write on at the secondary external storage
     */
    private String SDCard1 = null;

    private Button listBtn;
    private Button testWriteBtn;
    private TextView txt;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * On Android 4.4, write secondary external storage is very different
         * please visit: https://source.android.com/devices/storage/ for more
         * detail.
         */
        File[] files = getExternalFilesDirs(null);
        for (File file : files) {
            Log.d(TAG, file.getAbsolutePath());
            // sdcard1 is the external storage(SD card) on platform
            String path = file.getAbsolutePath();
            if (path.contains("/storage/sdcard1")) {
                // found it
                SDCard1 = path;
                Log.d(TAG, SDCard1);
                break;
            }
        }

        sb = new StringBuilder();

        listBtn = (Button) findViewById(R.id.listBtn);
        listBtn.setOnClickListener(this);

        testWriteBtn = (Button) findViewById(R.id.testWriteBtn);
        testWriteBtn.setOnClickListener(this);

        txt = (TextView) findViewById(R.id.txt);
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        switch (btnId) {
        case R.id.listBtn:
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    listBtn.setEnabled(false);
                }

                @Override
                protected void onPostExecute(Void result) {
                    txt.setText(sb.toString());
                    listBtn.setEnabled(true);
                }

                @Override
                protected Void doInBackground(Void... params) {
                    sb.delete(0, sb.length());
                    readFile(SDCard1);
                    return null;
                }
            }.execute();
            break;

        case R.id.testWriteBtn:

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    testWriteBtn.setEnabled(false);
                }

                @Override
                protected void onPostExecute(Void result) {
                    sb.delete(0, sb.length());
                    readFile(SDCard1);
                    txt.setText(sb.toString());
                    testWriteBtn.setEnabled(true);
                }

                @Override
                protected Void doInBackground(Void... params) {
                    newFile(SDCard1);
                    return null;
                }
            }.execute();
            break;

        default:
            break;

        }
    }

    /*
     * Create a temp file on SD card for testing WRITE
     */
    private void newFile(String path) {
        File file = null;

        try {
            file = File.createTempFile("TestSDCard", ".txt", new File(path));
            Log.d(TAG, file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            String[] subFiles = file.list();

            sb.append("Directory: ");
            sb.append(file.getName());
            sb.append("\n");

            for (String subFile : subFiles) {
                String subPath = file.getAbsolutePath() + "/" + subFile;
                Log.d(TAG, subPath);
                readFile(subPath);
            }
        } else {
            sb.append("file: ");
            sb.append(file.getName());
            sb.append("\n");
        }
    }
}
