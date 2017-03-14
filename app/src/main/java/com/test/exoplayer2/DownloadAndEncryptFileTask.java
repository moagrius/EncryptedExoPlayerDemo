package com.test.exoplayer2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

/**
 * Created by michaeldunn on 3/13/17.
 */

public class DownloadAndEncryptFileTask extends AsyncTask<Void, Void, Void> {

  private String mUrl;
  private File mFile;
  private Cipher mCipher;

  public DownloadAndEncryptFileTask(String url, File file, Cipher cipher) {
    if (url == null || url.isEmpty()) {
      throw new IllegalArgumentException("You need to supply a url to a clear MP4 file to download and encrypt, or modify the code to use a local encrypted mp4");
    }
    mUrl = url;
    mFile = file;
    mCipher = cipher;
  }

  private void downloadAndEncrypt() throws Exception {

    URL url = new URL(mUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.connect();

    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("server error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
    }

    InputStream inputStream = connection.getInputStream();
    FileOutputStream fileOutputStream = new FileOutputStream(mFile);
    CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, mCipher);

    byte buffer[] = new byte[1024 * 1024];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      Log.d(getClass().getCanonicalName(), "reading from http...");
      cipherOutputStream.write(buffer, 0, bytesRead);
    }

    inputStream.close();
    cipherOutputStream.close();
    connection.disconnect();
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      downloadAndEncrypt();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    Log.d(getClass().getCanonicalName(), "done");
  }
}
