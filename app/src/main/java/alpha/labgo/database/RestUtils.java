package alpha.labgo.database;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import alpha.labgo.R;
import alpha.labgo.models.TaskParams;

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
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        OutputStream outputStream = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

        urlConnection.connect();


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

    public class AddStudetInventory extends AsyncTask<TaskParams, Void, String> {
        private static final String TAG = "AddStudentInventory";
        private static final String REST_TAG = "/studentinventories";
        private static final String GTID = "student_id";
        private static final String CHECK_IN_TIME = "checkin_timestamp";
        private static final String CHECK_OUT_TIME = "checkout_timestamp";

        // for temporary use
        private static final String RFID_TAG = "1234";

        @Override
        protected String doInBackground(TaskParams... params) {
            String gtid = params[0].strings[0];
            String direction = params[0].strings[1];
            Context context = params[0].context;
            String addStudentInventoryResult = null;

            String urlBase = REST_BASE_URL + REST_TAG;
            String checkTime;
            if (direction == "checkIn") {
                checkTime = CHECK_IN_TIME;
            } else if (direction == "checkOut") {
                checkTime = CHECK_OUT_TIME;
            } else {
                Log.e(TAG, "Wrong QR code!");
                Toast.makeText(context, "Wrong QR code!",
                        Toast.LENGTH_LONG).show();
                return null;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd' 'HH:mm:ss.SSS'Z'");
            String timestamp = format.format(new Date());
            Uri buildUri = Uri.parse(urlBase).buildUpon()
                    .appendQueryParameter(GTID, gtid)
                    .appendQueryParameter(checkTime, timestamp)
                    .build();

            URL url = null;
            try {
                url = new URL(buildUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "URL:"+ url.toString());

//            try {
//                addStudentInventoryResult =
//            }
            return null;
        }
    }
}
