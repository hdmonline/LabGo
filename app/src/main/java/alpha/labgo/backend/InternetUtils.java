package alpha.labgo.backend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import alpha.labgo.UpdateItemActivity;

public class InternetUtils {

    private static final String TAG = "InternetUtils";

    public static class GetImageByUrl extends AsyncTask<String, Void, Bitmap> {

        private UpdateItemActivity mActivity;

        public GetImageByUrl(UpdateItemActivity activity) {
            this.mActivity = activity;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            String imageUrl = strings[0];

            URL url = null;
            try {
                url = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "Invalid image URL!", Toast.LENGTH_SHORT).show();
                return null;
            }

            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "Cannot load image!", Toast.LENGTH_SHORT).show();
                return null;
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                mActivity.showImage(bitmap);
            }
            super.onPostExecute(bitmap);
        }
    }
}
