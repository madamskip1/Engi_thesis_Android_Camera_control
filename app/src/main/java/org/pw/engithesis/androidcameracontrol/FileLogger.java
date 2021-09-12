package org.pw.engithesis.androidcameracontrol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class FileLogger {
    private static final String LOG_DIR_NAME = "AndroidCameraControlLogs";
    private static final String DEFAULT_FILE_NAME = "AndroidCameraControlLog.txt";
    private static final Boolean DEFAULT_APPEND = true;
    private static final Boolean DEAFULT_WRITE_DATE_AND_TIME = true;


    private String _fileName;
    private Boolean _append;
    private Boolean _writeDateAndTime;

    private File logDir;
    private File file;
    private FileWriter fileWriter;


    public FileLogger() {
        _fileName = DEFAULT_FILE_NAME;
        _append = DEFAULT_APPEND;
        writeDateAndTime(DEAFULT_WRITE_DATE_AND_TIME);

        openFile();
    }

    public FileLogger(String fileName) {
        _fileName = fileName;
        _append = DEFAULT_APPEND;
        writeDateAndTime(DEAFULT_WRITE_DATE_AND_TIME);

        openFile();
    }

    public FileLogger(String fileName, Boolean append) {
        _fileName = fileName;
        _append = append;
        writeDateAndTime(DEAFULT_WRITE_DATE_AND_TIME);

        openFile();
    }

    public void writeDateAndTime(Boolean writeDT) {
        _writeDateAndTime = writeDT;
    }

    public void write(String text) {
        if (_writeDateAndTime) {
            text = getDateTimeString() + text;
        }

        text += '\n';

        try {
            fileWriter.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openFile() {
        try {
            String path = App.getContext().getExternalFilesDir(null).getAbsolutePath();
            logDir = new File(path, LOG_DIR_NAME);

            if (!logDir.exists()) {
                logDir.mkdir();
            }

            file = new File(logDir, _fileName);

            fileWriter = new FileWriter(file, _append);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDateTimeString() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("'['HH:mm:ss, dd-MM-yyyy']'");
        String formatedDateTime = dateFormat.format(date);

        return formatedDateTime;
    }
}
