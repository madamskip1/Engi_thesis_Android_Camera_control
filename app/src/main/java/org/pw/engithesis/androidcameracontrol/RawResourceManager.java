package org.pw.engithesis.androidcameracontrol;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RawResourceManager {
    private int _resourceID = -1;
    private String _resourceName;
    private File resourceFile;

    public RawResourceManager(int resourceID, String resourceName)
    {
        _resourceID = resourceID;
        _resourceName = resourceName;
    }

    private RawResourceManager() {}

    public File getFile() {
        if (resourceFile != null) {
            return resourceFile;
        }

        try
        {
            Context context = App.getContext();
            InputStream is = context.getResources().openRawResource(_resourceID);
            File cascadeDir = context.getDir("tempDir", Context.MODE_PRIVATE);
            resourceFile = new File(cascadeDir, _resourceName);
            FileOutputStream os = new FileOutputStream(resourceFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            cascadeDir.delete();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return resourceFile;
    }

    public String getPath()
    {
        return getFile().getAbsolutePath();
    }
}
