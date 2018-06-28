package alpha.labgo.database;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import alpha.labgo.MainActivity;
import alpha.labgo.models.BorrowedItem;


/**
 * This class contains methods for communication between the app and the database through REST API.
 *
 * @auther  Dongmin Han
 * @since   2018-6-27
 */
public class RestUtils {
    private static final String TAG = "RestUtils";
    private static final String REST_BASE_URL = "http://ec2-52-90-6-153.compute-1.amazonaws.com:1880/v1";
    private static final String REST_TAG = "/studentinventories";
    private static final String GTID = "student_id";
    private static final String CHECK_IN = "/checkin";
    private static final String CHECK_OUT = "/checkout";

    // tags for borrowed items
    private static final String ITEM_NAME = "item_name";
    private static final String ITEM_IMAGE_URL = "item_image_url";
    private static final String ITEM_DESCRIPTION = "item_description";
    private static final String CHECK_OUT_TIME = "checkout_timestamp";
    private static final String CHECK_IN_TIME = "checkin_timestamp";
    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static JSONArray getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String result = "";
        JSONArray ja = null;
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while (line != null) {
                line = buffer.readLine();
                result += line;
            }
            ja = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return ja;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String postResponseFromHttpUrl(URL url, String gtid) throws IOException {
        // TODO: figure out post request!
        StringBuffer response = new StringBuffer();
        HttpURLConnection urlConnection = null;
        String responseJSON = null;

        JSONObject postJson = new JSONObject();

        try {
            postJson.put(GTID, gtid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            // send json string
            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            os.writeBytes(postJson.toString());
            os.flush();
            os.close();

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

    /**
     * This method is called when the student is checking in or out the tools by scanning QR codes
     * on the door. (This is a backup method when the face recognition is not working.)
     */
    public static class StudentCheckInOrOut extends AsyncTask<String, Void, String> {

        private static final String TAG = "StudentCheckInOrOut";

        private int mDirection = -1; // 0 is check in and 1 is check out

        private Context mContext;
        private String mGtid;

        public StudentCheckInOrOut(Context context) {
            this.mContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String mGtid = strings[0];
            String qrCode = strings[1];
            String studentCheckInOrOutResult = null;

            String baseUrl = REST_BASE_URL + REST_TAG;
            //String checkTime;
            if (qrCode.equals("checkIn")) {
                baseUrl += CHECK_IN;
                mDirection = 0;
            } else if (qrCode.equals("checkOut")) {
                baseUrl += CHECK_OUT;
                mDirection = 1;
            } else {
                Log.w(TAG, "Wrong QR code!");
                Toast.makeText(mContext, "Wrong QR code!",
                        Toast.LENGTH_LONG).show();
                return null;
            }

            URL url = null;
            try {
                //Log.d(TAG, buildUri.toString());
                Log.d(TAG, baseUrl);
                url = new URL(baseUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "URL:"+ url.toString());

            try {
                studentCheckInOrOutResult = postResponseFromHttpUrl(url, mGtid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return studentCheckInOrOutResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                if (mDirection == 0) {
                    Log.d(TAG, "check in successfully");
                    Toast.makeText(mContext, "Check in successfully!",
                            Toast.LENGTH_LONG).show();
                } else if (mDirection == 1) {
                    Log.d(TAG, "check out successfully");
                    Toast.makeText(mContext, "Check out successfully!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "Wrong QR code!");
                    return;
                }
                mContext.startActivity(new Intent(mContext, MainActivity.class));
            }
        }
    }

    /**
     * This method lodas the items/tools that the student has borrowed.
     * Then convert the received data objects to {@link BorrowedItem} objects.
     *
     * @param gtid Student GTID
     * @return ArrayList of {@link BorrowedItem}. Will be fed into {@link alpha.labgo.adapters.BorrowedItemAdapter}.
     */
    public static ArrayList<BorrowedItem> studentBorrowedItems(String gtid) {

        String baseUrl = REST_BASE_URL + REST_TAG;
        baseUrl += "/" + gtid;
        URL url = null;

        ArrayList<BorrowedItem> borrowedItems = new ArrayList<>();

        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONArray jaResult = null;
        try {
            jaResult = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < jaResult.length(); i++) {
                JSONObject jo = (JSONObject) jaResult.get(i);
                if (jo.getString(CHECK_IN_TIME) == null || jo.getString(CHECK_IN_TIME).equals("null")) {
                    borrowedItems.add(new BorrowedItem(jo.getString(ITEM_IMAGE_URL),
                            jo.getString(ITEM_NAME),
                            jo.getString(ITEM_DESCRIPTION),
                            timeHandler(jo.getString(CHECK_OUT_TIME)))
                    );
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return borrowedItems;
    }

    /**
     * This method handles the time string from the received data.
     *
     * @param date Time string from the received data
     * @return New string that could contain specific time tags, i.e., "Today" and "Yesterday"
     */
    private static String timeHandler(String date) {
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dayFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String today = dayFormat.format(new Date());
        String day = "";
        String time = "";
        try {
            time = timeFormat.format(dbFormat.parse(date));
            day = dayFormat.format(dbFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (day.equals(today)) {
            return "Today " + time;
        } else if (day.equals(dayFormat.format(yesterday()))) {
            return "Yesterday " + time;
        } else {
            return day + " " + time;
        }
    }

    /**
     * The method returns the {@Date} of yesterday. It will be used in timeHandler()
     *
     * @return
     */
    private static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

}
