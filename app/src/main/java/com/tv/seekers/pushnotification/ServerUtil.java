package com.tv.seekers.pushnotification;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ServerUtil {

    private static final String TAG = "PushNotifTest";

    public static final int RETRY_COUNT = 8;
    public static final long RETRY_INTERVAL = 2000;

    public static boolean sendPost(String url, String body, String userId, String secretCode) {
        Log.d(TAG, "Sending Post URL: " + url);

        int retryCount = 0;
        boolean success = false;

        while (!success) {
            OutputStreamWriter writer = null;
            try {
                URI uri = new URI(url);
                URL urlObj = uri.toURL();

                HttpURLConnection connection = (HttpURLConnection)urlObj.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                String authToken = generateAuthToken(userId, secretCode);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", userId + "||" + authToken);

                if (body != null) {
                    writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(body);
                    writer.flush();
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // SUCCESS
                    success = true;
                } else {
                    Log.e(TAG, "ERROR: HTTP error sending post request: " + responseCode + "\n" +
                            getStrFromInputStream(connection.getInputStream()));
                    return false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR: Malformed URL - " + e.toString());
                return false;
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR: URISyntaxException - " + e.toString());
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                if (retryCount < RETRY_COUNT) {
                    Log.w(TAG, "IOException - (" + e.toString() + ") - Retrying soon...");
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException ex) {}
                } else {
                    Log.e(TAG, "IOException - All retries failed.");
                    return false;
                }
                retryCount++;
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {}
                }
            }
        }

        if (!success) {
            Log.e(TAG, "ERROR: Request failed");
            return false;
        }

        return success;
    }

    public static String getStrFromInputStream(InputStream inputStream) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder responseStr = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                responseStr.append(line + "\n");
            }
            return responseStr.toString();
        } catch (Exception e) {
            Log.e(TAG, "Could not extract str from input stream: " + e.toString());
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {}
            }
        }
    }

    public static String generateAuthToken(String accId, String secretCode) {
        int[] primes = {31, 37, 41, 43, 47, 53, 59, 61, 67, 73};
        char[] base = (accId + secretCode).toCharArray();
        long hash = 0;
        for (int i=0; i < base.length; i++) {
            int k = base[i];
            hash += (k * primes[k % primes.length]);
        }
        String extSecretCode = hash + secretCode;
        String result = "";
        for (int i = 0; i < extSecretCode.length()/2; i++) {
            result += (int)extSecretCode.charAt(i);
            result += (int)extSecretCode.charAt(extSecretCode.length() - i - 1);
        }

        return result;
    }
}
