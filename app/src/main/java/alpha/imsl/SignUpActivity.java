package alpha.imsl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class SignUpActivity extends Activity {

    TextInputEditText mGtid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mGtid = findViewById(R.id.field_scanned_gtid);
        Barcode gtidData = getIntent().getParcelableExtra("qrCode");
        mGtid.setText(gtidData.displayValue);
    }
}
