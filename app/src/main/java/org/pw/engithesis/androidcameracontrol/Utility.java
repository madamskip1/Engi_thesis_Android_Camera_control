package org.pw.engithesis.androidcameracontrol;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Utility {

    public static File readResourceFile(int resourceID, String resourceName)
    {
        if (resourceID == -1 || resourceName == null) {
            return null;
        }

        try
        {
            Context context = App.getContext();
            InputStream is = context.getResources().openRawResource(resourceID);
            File cascadeDir = context.getDir("tempDir", Context.MODE_PRIVATE);
            File tempFile = new File(cascadeDir, resourceName);
            FileOutputStream os = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            cascadeDir.delete();

            return tempFile;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
