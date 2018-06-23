package alpha.labgo.models;

import android.content.Context;

/**
 * including context make it possible to use Toast in the AsyncTask classes.
 */
public class TaskParams {
    public String[] strings;
    public Context context;

    public TaskParams(String[] strings, Context context) {
        this.strings = strings;
        this.context = context;
    }
}