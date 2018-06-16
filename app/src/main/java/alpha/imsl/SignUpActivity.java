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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode qrCode =  data.getParcelableExtra("qrCode");
                    mGtid.setText("QR code value: " + qrCode.displayValue);
                } else {
                    mGtid.setText("No GTID found!");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
