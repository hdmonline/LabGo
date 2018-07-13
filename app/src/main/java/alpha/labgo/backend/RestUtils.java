package alpha.labgo.backend;

import android.app.Activity;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import alpha.labgo.SignUpActivity;
import alpha.labgo.UpdateInventoryActivity;
import alpha.labgo.MainActivity;
import alpha.labgo.UpdateItemActivity;
import alpha.labgo.models.Item;
import alpha.labgo.models.BorrowedItem;
import alpha.labgo.models.InventoryItem;
import alpha.labgo.models.ScannedItem;
import alpha.labgo.settings.AccountSettingsActivity;


/**
 * This class contains methods for communication between the app and the database through REST API.
 *
 * @auther  Dongmin Han <dongminhanme@gmail.com>
 * @version 0.1
 * @since   0.1
 */
public class RestUtils {

    private static final String TAG = "RestUtils";

    private static final String REST_BASE_URL = "http://ec2-52-90-6-153.compute-1.amazonaws.com:1880/v1";

    // Keys for inventory items
    private static final String ITEM_NAME = "item_name";
    private static final String ITEM_IMAGE_URL = "item_image_url";
    private static final String ITEM_DESCRIPTION = "item_description";
    private static final String CHECK_OUT_TIME = "checkout_timestamp";
    private static final String CHECK_IN_TIME = "checkin_timestamp";

    // Keys for RFID
    private static final String RFID_TAG = "rfid_tag";

    // Keys for student
    private static final String GTID = "student_id";

    // Parameters for check in/out
    private static final String CHECK_IN = "/checkin";
    private static final String CHECK_OUT = "/checkout";

    // Parameters for borrowed items
    private static final String STUDENT_INVENTORIES = "/studentinventories";

    // Parameters for inventory items
    private static final String INVENTORIES = "/inventories";

    // Parameters for RFID tags
    private static final String TAGS = "/tags";
    private static final String WITH_DEPENDENCIES = "/withdependencies";
    private static final String INCOMING_TAGS = "/incomingrfidtags";
    private static final String OUTGOING_TAGS = "/outgoingrfidtags";

    // Parameters for items
    private static final String ITEMS = "/items";

    // Parameters for images
    private static final String IMAGES = "/images";

    // Parameters for accounts
    public static final String ACCOUNTS = "/accounts";


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    private static JSONArray getResponseFromHttpUrl(URL url) throws IOException {

        Log.d(TAG, "sending GET http request");

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(3000);
        urlConnection.setConnectTimeout(5000);
        String result = "";
        JSONArray ja = null;
        JSONObject jo = null;
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
            try {
                jo = new JSONObject(result);
            } catch (JSONException eo) {
                eo.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        if (jo != null) {
            ja = new JSONArray();
            ja.put(jo);
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
    private static String postResponseFromHttpUrl(URL url, JSONObject postJson) throws IOException {

        Log.d(TAG, "sending POST http request");

        StringBuffer response = new StringBuffer();
        HttpURLConnection urlConnection = null;
        String responseJSON;
        int status = 200;

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

            status = urlConnection.getResponseCode();

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

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

        // json response string
        responseJSON = response.toString();

        return responseJSON;
    }

    /**
     * This method returns the entire result from the DELETE HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    private static String deleteResponseFromHttpUrl(URL url) throws IOException {

        Log.d(TAG, "sending DELETE http request");

        StringBuffer response = new StringBuffer();
        HttpURLConnection urlConnection = null;
        String responseJSON;
        int status = 200;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            status = urlConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        if (status != 200) {
            throw new IOException("Delete failed with error code " + status);
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        // json response string
        responseJSON = response.toString();
        return responseJSON;
    }

    /**
     * This AsyncTask class is called when the student is checking in or out the tools by scanning QR codes
     * on the door. (This is a backup method when the face recognition is not working.)
     */
    public static class StudentCheckInOrOut extends AsyncTask<String, Void, String> {

        private static final String TAG = "StudentCheckInOrOut";

        private int mDirection = -1; // 0 is check in and 1 is check out

        private Context mContext;

        public StudentCheckInOrOut(Context context) {
            this.mContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String gtid = strings[0];
            String qrCode = strings[1];
            String studentCheckInOrOutResult = null;

            String baseUrl = REST_BASE_URL + STUDENT_INVENTORIES;
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

            // Generate JSON body for the request
            JSONObject postJson = new JSONObject();
            try {
                postJson.put(GTID, gtid);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                studentCheckInOrOutResult = postResponseFromHttpUrl(url, postJson);
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
     * This class is called when the TA needs to add an item to the database. A RFID tag would be
     * scanned first, then this list of items will appear when the TA is searching items.
     */
    public static class ListNewTags extends AsyncTask<Void, Void, ArrayList<ScannedItem>> {

        private UpdateInventoryActivity mActivity;


        public ListNewTags(UpdateInventoryActivity activity) {
            this.mActivity = activity;
        }

        @Override
        protected ArrayList<ScannedItem> doInBackground(Void... voids) {
            ArrayList<ScannedItem> scannedItems = getScannedItems();
            return scannedItems;
        }

        @Override
        protected void onPostExecute(ArrayList<ScannedItem> scannedItems) {
            mActivity.refreshUi(scannedItems);
            super.onPostExecute(scannedItems);
        }
    }

    public static class AddItem extends AsyncTask<String, Void, String> {

        private static final String TAG = "AddItem";
        private final Activity mActivity;

        public AddItem(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        protected String doInBackground(String... strings) {
            String itemName = strings[0];
            String itemImage = strings[1];
            String itemDeccription = strings[2];

            String baseUrl = REST_BASE_URL + ITEMS;
            String addItemResult = null;

            URL url = null;
            try {
                Log.d(TAG, baseUrl);
                url = new URL(baseUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "URL:"+ url.toString());

            // Generate JSON body for the request
            JSONObject postJson = new JSONObject();
            try {
                postJson.put(ITEM_NAME, itemName);
                postJson.put(ITEM_DESCRIPTION, itemDeccription);
                postJson.put(ITEM_IMAGE_URL, itemImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                addItemResult = postResponseFromHttpUrl(url, postJson);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return addItemResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                try {
                    UpdateItemActivity activity = (UpdateItemActivity) mActivity;
                    activity.onSuccessFinishing();
                } catch (ClassCastException e) {
                    Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
                }
            } else {
                try {
                    UpdateItemActivity activity = (UpdateItemActivity) mActivity;
                    activity.onFailureFinishing();
                } catch (ClassCastException e) {
                    Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
                }
                Log.e(TAG, "Failed to add inventory, please check the database.");
            }
        }
    }

    /**
     * This class is called when the TA connects an item to a tag to add an inventory item.
     */
    public static class AddInventoryItem extends AsyncTask<String, Void, String> {

        private static final String TAG = "AddInventoryItem";
        private final Activity mActivity;

        public AddInventoryItem(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        protected String doInBackground(String... strings) {

            String itemName = strings[0];
            String baseUrl = REST_BASE_URL + INVENTORIES;
            String addInventoryItemResult = null;

            URL url = null;
            try {
                Log.d(TAG, baseUrl);
                url = new URL(baseUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "URL:"+ url.toString());

            // Generate JSON body for the request
            JSONObject postJson = new JSONObject();
            try {
                postJson.put(ITEM_NAME, itemName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                addInventoryItemResult = postResponseFromHttpUrl(url, postJson);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return addInventoryItemResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                try {
                    UpdateInventoryActivity activity = (UpdateInventoryActivity) mActivity;
                    activity.onSuccessFinishing();
                } catch (ClassCastException e) {
                    Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
                }
            } else {
                try {
                    UpdateInventoryActivity activity = (UpdateInventoryActivity) mActivity;
                    activity.onFailureFinishing();
                } catch (ClassCastException e) {
                    Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
                }
                Log.e(TAG, "Failed to add inventory, please check the database.");
            }
        }
    }

    /**
     * This method is used when the TA needs to delete an inventory item.
     * The tag is scanned first, then the item the TA is deleting will be shown in a dialog.
     * This method is to get the item from the tag.
     */
    public static class GetItemByTag extends AsyncTask<String, Void, Item> {

        private UpdateInventoryActivity mActivity;

        public GetItemByTag(UpdateInventoryActivity activity) {
            this.mActivity = activity;
        }

        @Override
        protected Item doInBackground(String... strings) {
            String tag = strings[0];
            return getItemByTag(tag);
        }

        @Override
        protected void onPostExecute(Item item) {
            if (item != null) {
                mActivity.showDialogAfterGettingItem(item);
            } else {
                mActivity.onDeleteFail();
            }
            super.onPostExecute(item);
        }
    }

    /**
     * This class is called when the TA needs to delete an item from inventory with all its dependencies.
     */
    public static class DeleteInventoryItem extends AsyncTask<String, Void, String> {

        private static final String TAG = "DeleteInventoryItem";
        private final Activity mActivity;

        public DeleteInventoryItem(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        protected String doInBackground(String... strings) {

            String tag = strings[0];

            String userInventoryUrl = REST_BASE_URL +
                    TAGS + "/" + tag + WITH_DEPENDENCIES;
            String deleteTagResult = null;

            URL url = null;
            try {
                Log.d(TAG, userInventoryUrl);
                url = new URL(userInventoryUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "URL:"+ url.toString());

            try {
                deleteTagResult = deleteResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return deleteTagResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                try {
                    UpdateInventoryActivity activity = (UpdateInventoryActivity) mActivity;
                    activity.onSuccessFinishing();
                } catch (ClassCastException e) {
                    Log.e(TAG, "onPostExecute: ClassCastException: " + e.getMessage());
                }
            } else {
                try {
                    UpdateInventoryActivity activity = (UpdateInventoryActivity) mActivity;
                    activity.onFailureFinishing();
                } catch (ClassCastException e) {
                    Log.e(TAG, "onPostExecute: ClassCastException: " + e.getMessage());
                }
                Log.e(TAG, "Failed to delete inventory, please check the database.");
            }
        }
    }

    /**
     * This class is called when more than one tag is scanned and the TA needs to delete
     * those tags and re-scan.
     */
    public static class DeleteIncomingTags extends AsyncTask<Void, Void, String> {

        private static final String TAG = "DeleteIncomingTags";
        private final Activity mActivity;

        public DeleteIncomingTags(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String baseUrl = REST_BASE_URL + INCOMING_TAGS;
            String deleteTagsResult = null;

            URL url = null;
            try {
                Log.d(TAG, baseUrl);
                url = new URL(baseUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "URL:"+ url.toString());

            try {
                deleteTagsResult = deleteResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return deleteTagsResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                Log.d(TAG, "cleared tags");
                UpdateInventoryActivity activity = (UpdateInventoryActivity) mActivity;
                activity.refreshTags();
            }
        }
    }

    /**
     * This class is called when the user signs up the account. After the BuzzCard image is uploaded
     * to S3 bucket, the image id needs to be connected with face id by this class.
     */
    public static class RegisterImage extends AsyncTask<String, Void, JSONObject> {

        private static final String TAG = "RegisterImage";
        private final Activity mActivity;

        public RegisterImage(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String gtid = strings[0];
            String baseUrl = REST_BASE_URL + IMAGES + "/" + gtid;
            JSONArray registerImageResult = null;

            URL url = null;
            try {
                Log.d(TAG, "Base URL: " + baseUrl);
                url = new URL(baseUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "URL:"+ url.toString());

            try {
                registerImageResult = getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject result = null;
            if (registerImageResult != null && registerImageResult.length() != 0) {
                try {
                    result = (JSONObject) registerImageResult.get(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            SignUpActivity activity = (SignUpActivity) mActivity;
            if (jsonObject != null) {
                Log.d(TAG, "registered image");
                activity.onSuccessFinishing();
            } else {
                activity.onFailureFinishing();
            }
        }
    }

    public static class DeleteAccount extends AsyncTask<String, Void, String> {

        private static final String TAG = "DeleteAccount";
        private final Activity mActivity;

        public DeleteAccount(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        protected String doInBackground(String... strings) {
            String gtid = strings[0];

            String baseUrl = REST_BASE_URL + ACCOUNTS + "/" + gtid;
            String deleteAccountResult = null;

            URL url = null;
            try {
                Log.d(TAG, "Base URL: " + baseUrl);
                url = new URL(baseUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "URL: " + url.toString());

            try {
                deleteAccountResult = deleteResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return deleteAccountResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                try {
                    AccountSettingsActivity activity = (AccountSettingsActivity) mActivity;
                    activity.onPostDeleteAccount();
                } catch (ClassCastException e) {
                    Log.e(TAG, "onPostExecute: ClassCastException: " + e.getMessage());
                }
            } else {
                try {
                    AccountSettingsActivity activity = (AccountSettingsActivity) mActivity;
                    activity.onFailDeleteAccount();
                } catch (ClassCastException e) {
                    Log.e(TAG, "onPostExecute: ClassCastException: " + e.getMessage());
                }
                Log.e(TAG, "Failed to delete account information, please check the database.");
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
    public static ArrayList<BorrowedItem> getStudentBorrowedItems(String gtid) {

        String baseUrl = REST_BASE_URL + STUDENT_INVENTORIES;
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
     * This method gets all items first. Then gets all the inventory items and put then into a list.
     *
     * @return list of inventory items
     */
    public static ArrayList<InventoryItem> getInventoryItems() {

        String itemUrl = REST_BASE_URL + ITEMS;
        String inventoryUrl = REST_BASE_URL + INVENTORIES;

        // List of items
        ArrayList<InventoryItem> items = new ArrayList<>();
        ArrayList<String> itemNames = new ArrayList<>();

        URL url = null;

        /* Fetch items from "items" table. */
        try {
            url = new URL(itemUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONArray itemJsonArray = null;
        try {
            itemJsonArray = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Store each item into <items>. Store each item name into <itemNames>.
            for (int i = 0; i < itemJsonArray.length(); i++) {
                JSONObject jo = (JSONObject) itemJsonArray.get(i);
                InventoryItem newItem = new InventoryItem(
                        jo.getString(ITEM_IMAGE_URL),
                        jo.getString(ITEM_NAME),
                        jo.getString(ITEM_DESCRIPTION),
                        0);
                items.add(newItem);
                itemNames.add(jo.getString(ITEM_NAME));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* Fetch inventory items from "inventories" */
        try {
            url = new URL(inventoryUrl);
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
                int index = itemNames.indexOf(jo.getString(ITEM_NAME));
                if (index > -1) {
                    InventoryItem currItem = items.get(index);
                    currItem.setItemQuantity(currItem.getItemQuantity()+1);
                    items.set(index, currItem);
                } else {
                    // TODO: throw an exception here?
                    Log.e(TAG, "getInventoryItems: There is an item in the inventory but not in the item table!");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * This method gets the RFID tags that are being scanned.
     *
     * @return The RFIDs that is being scanned
     */
    public static ArrayList<ScannedItem> getScannedItems() {

        String baseUrl = REST_BASE_URL + INCOMING_TAGS;
        ArrayList<ScannedItem> scannedItems = new ArrayList<>();

        URL url = null;

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
                ScannedItem newItem = new ScannedItem(jo.getString(RFID_TAG));
                scannedItems.add(newItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return scannedItems;
    }

    /**
     * This method gets all kinds of items in database
     * See {@link UpdateInventoryActivity}
     *
     * @return
     */
    public static ArrayList<Item> getItems() {

        String baseUrl = REST_BASE_URL + ITEMS;
        ArrayList<Item> items = new ArrayList<>();

        URL url = null;

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
                Item newItem = new Item(
                        jo.getString(ITEM_IMAGE_URL),
                        jo.getString(ITEM_NAME),
                        jo.getString(ITEM_DESCRIPTION));
                items.add(newItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * This method gets the item by tag.
     * See {@link GetItemByTag}
     *
     * @param tag The RFID tag of the item
     * @return
     */
    public static Item getItemByTag(String tag) {

        String baseUrl = REST_BASE_URL + INVENTORIES + TAGS + "/" + tag;
        Item item = null;

        URL url = null;
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

        if (jaResult != null && jaResult.length() != 0) {
            try {
                JSONObject jo = (JSONObject) jaResult.get(0);
                item = new Item(
                        jo.getString(ITEM_IMAGE_URL),
                        jo.getString(ITEM_NAME),
                        jo.getString(ITEM_DESCRIPTION));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return item;
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
        dbFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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
     * The method returns the {@Date} of yesterday. See {@link #timeHandler(String)} for detail.
     *
     * @return the date of yesterday
     */
    private static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
}
