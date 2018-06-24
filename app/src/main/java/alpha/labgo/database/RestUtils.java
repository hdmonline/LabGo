package alpha.labgo.database;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import alpha.labgo.MainActivity;

public class RestUtils {
    private static final String TAG = "RestUtils";
    private static final String REST_BASE_URL = "http://ec2-52-90-6-153.compute-1.amazonaws.com:1880/v1";

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String postResponseFromHttpUrl(URL url) throws IOException {
        // TODO: figure out post request!
        StringBuffer response = new StringBuffer();
        HttpURLConnection urlConnection = null;
        String responseJSON = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            int status = urlConnection.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            // json response string
            responseJSON = response.toString();
        }
        return responseJSON;
    }

    public static class StudentCheckInOrOut extends AsyncTask<String, Void, String> {
        private static final String TAG = "StudentCheckInOrOut";
        private static final String REST_TAG = "/studentinventories";
        private static final String GTID = "student_id";
        private static final String CHECK_IN = "/checkin";
        private static final String CHECK_OUT = "/checkout";
        private int direction = -1; // 0 is check in and 1 is check out

        private Context context;

        public StudentCheckInOrOut(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String gtid = strings[0];
            String qrCode = strings[1];
            String studentCheckInOrOutResult = null;

            String urlBase = REST_BASE_URL + REST_TAG;
            //String checkTime;
            if (qrCode.equals("checkIn")) {
                urlBase += CHECK_IN;
                direction = 0;
            } else if (qrCode.equals("checkOut")) {
                urlBase += CHECK_OUT;
                direction = 1;
            } else {
                Log.w(TAG, "Wrong QR code!");
                Toast.makeText(context, "Wrong QR code!",
                        Toast.LENGTH_LONG).show();
                return null;
            }
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd' 'HH:mm:ss.SSS'Z'");
            //String timestamp = format.format(new Date());
            Uri buildUri = Uri.parse(urlBase).buildUpon()
                    .appendQueryParameter(GTID, gtid)
                    .build();

            URL url = null;
            try {
                url = new URL(buildUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "URL:"+ url.toString());

            try {
                studentCheckInOrOutResult = postResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return studentCheckInOrOutResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                if (direction == 0) {
                    Log.d(TAG, "check in successfully");
                    Toast.makeText(context, "Check in successfully!",
                            Toast.LENGTH_LONG).show();
                } else if (direction == 1) {
                    Log.d(TAG, "check out successfully");
                    Toast.makeText(context, "Check in successfully!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "Wrong QR code!");
                    return;
                }
                context.startActivity(new Intent(context, MainActivity.class));
            }
        }
    }
}
