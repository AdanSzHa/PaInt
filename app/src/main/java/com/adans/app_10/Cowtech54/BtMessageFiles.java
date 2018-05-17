package com.adans.app_10.Cowtech54;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

/**
 * Created by Wily on 13/04/2018.
 */

public class BtMessageFiles {
    private final String TAG = BtMessageFiles.class.getSimpleName();

    private String folder = "";
    private String prefix = "";
    private String parentDirStr = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String filePathStr = parentDirStr + File.separator + prefix;
    private File parentDir;
    private File f;
    private FileWriter fileWriter;
    private CSVWriter csvWriter;

    private String dateString; //format yyMMdd_hhmm_ss_SSS
    private String timeString; //
    private int timeGenerator; //in minutes

    private Timer timer;

    private Context context;
    private TextView filePathText;

    public BtMessageFiles() {
    }

    public BtMessageFiles(String prefix) {
        this.prefix = prefix;
        this.folder = "";
        this.timeGenerator = 5; //default time 5 min
    }

    public BtMessageFiles(String prefix, String folder) {
        this.prefix = prefix;
        this.folder = folder;
        this.timeGenerator = 5; //default time 5 min
    }

    public BtMessageFiles(String prefix, String folder, int timeGenerator) {
        this.prefix = prefix;
        this.folder = folder;
        this.timeGenerator = timeGenerator;
    }

    //methods
    public void setParentDirStr(String parentDirStr) {
        this.parentDirStr = parentDirStr;
    }

    public String getParentDirStr() {
        return parentDirStr;
    }

    public void setTimeGenerator(int timeGenerator) {
        this.timeGenerator = timeGenerator;
        initTimerGenerator(timeGenerator);
    }

    public void setFilePathTextView(TextView filePathText) {
        this.filePathText = filePathText;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFolder() {
        return folder;
    }

    public String getPrefix() {
        return prefix;
    }

    public void stopFiles(){
        timer.cancel();
    }

    public String getDateString() {
        long tsLong;
        tsLong = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmm_ss_SSS");//"MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(tsLong);
        String rsltDate = sdf.format(resultdate);
        dateString = rsltDate;
        return dateString;
    }

    public void generateFilePath(){
        String fileName = prefix + "_"+ getDateString() + ".csv";
        String parentDirStr2 = parentDirStr + File.separator + folder;
        filePathStr = parentDirStr2 + File.separator + fileName;
        f = new File(filePathStr);

        parentDir = new File(parentDirStr2);

        // If the parent dir doesn't exist, create it
        if (!parentDir.exists()) {
            if (parentDir.mkdirs()) {
                Log.d(TAG, "Successfully created the parent dir: " + parentDir.getName());

                Log.d(TAG,"File path generated: " + filePathStr);
            } else {
                Log.d(TAG, "Failed to create the parent dir: " + parentDir.getName());
            }
        }else{
            Log.d(TAG,"New file path generated: " + filePathStr);
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // assert filePathText != null; //verify not null
                if(filePathText!=null)
                    filePathText.setText(filePathStr);
            }
        });
    }



    public String getFilePathStr() {
        return filePathStr;
    }

    public int getTimeGenerator() {
        return timeGenerator;
    }

    public Timer getTimer() {
        return timer;
    }

    public void writeInFile(String message){
        FileWriter mFileWriter;

        try {
            // File exist
            if(f.exists() && !f.isDirectory()){

                mFileWriter = new FileWriter(filePathStr, true); //append is to write below thelast line
                csvWriter = new CSVWriter(mFileWriter,'\t', '\0', '\0'); // \0 to supress the "" in strings data

            }
            else {
                //writer = new CSVWriter(new FileWriter(f))
                Writer writer2 = new FileWriter(filePathStr);
                csvWriter = new CSVWriter(writer2,'\t', '\0', '\0' );
            }
            String[] data = {message};
            csvWriter.writeNext(data);
            csvWriter.close();

           // Log.d(TAG, "Successfully data written in CSV");
            //Toast.makeText(getApplicationContext(),"Data written in CSV",Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            //Probably filepath is wrong or cannot be accesed
            Log.d(TAG, "Failed to write in CSV at: " + filePathStr);
            e.printStackTrace();

            //Change parent dir and regernerate file
            parentDirStr = Environment.getExternalStorageDirectory().getAbsolutePath();
            generateFilePath();

            Log.d(TAG,"New file path: " + filePathStr);
        }
    }

    public void initTimerGenerator(int timerPeriodMinutes){
        timeGenerator = timerPeriodMinutes; //In minutes
        int timeGeneratorInSec = timeGenerator*60;
        if(timer!=null)
            timer.cancel(); //We must cancel previous timer to avoid Overlaps

        timer = new Timer();
        TimerTask tasknew = new timerTask();
        timer.scheduleAtFixedRate(tasknew, 500, timeGeneratorInSec*1000);
    }


    public void main(String[] args) {

        // creating timer task, timer
        TimerTask tasknew = new timerTask();
        Timer timer = new Timer();

        // scheduling the task at fixed rate delay
        timer.scheduleAtFixedRate(tasknew,100,1000);
    }

    class timerTask extends TimerTask {



        @Override
        public void run() {
            // Generating new file Path
            generateFilePath();


            //System.out.println("working at fixed rate delay");
        }
    }


}


