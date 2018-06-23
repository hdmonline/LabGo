package alpha.labgo.models;

import android.content.Context;

public class TaskParams {
    public String[] strings;
    public Context context;

    TaskParams(String[] strings, Context context) {
        this.strings = strings;
        this.context = context;
    }
}