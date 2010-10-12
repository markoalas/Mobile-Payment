package ee.hansa.android;

import static android.net.Uri.parse;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Contacts.People;
import android.provider.Contacts.People.Phones;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MobilePayment extends Activity {
	private final int PICK_CONTACT = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_layout);
		initCallLogDeleter();
		pickBeneficiary();
	}

	private void initCallLogDeleter() {
		PhoneStateListener deleter = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				getContentResolver().delete(CallLog.Calls.CONTENT_URI, "number like '1214*%'", null);
				getContentResolver().notifyChange(CallLog.Calls.CONTENT_URI, null);
			}
		};
		
		TelephonyManager tm = ((TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE));
		tm.listen(deleter, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		((TextView)findViewById(R.id.pin)).setText("");
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case PICK_CONTACT:
			if (resultCode == Activity.RESULT_OK) {
				try {
					Uri contactData = data.getData();
					Cursor c = managedQuery(contactData, new String[]{People.DISPLAY_NAME, Phones.NUMBER}, null, null, Phones.ISPRIMARY + " DESC");
					if (c.moveToFirst()) {
						showPaymentForm(c.getString(0), c.getString(1));
					}
				}
				catch (Exception e) {
					alert(e.getClass().getSimpleName() + ": " + e.getMessage());
				}
			}
			break;
		}
	}

	private void showPaymentForm(final String beneficiaryName, final String number) {
		((TextView)findViewById(R.id.beneficiary)).setText(beneficiaryName);
		((TextView)findViewById(R.id.number)).setText(number);
		
		((Button)findViewById(R.id.PayButton)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					String pin = ((EditText)findViewById(R.id.pin)).getText().toString();
					makePayment(removeCountryPrefix(number), evaluateAmount(), pin);
				}
				catch (Exception e) {
					alert(e.getClass().getSimpleName() + ": " + e.getMessage());
				}
			}

		});
		
		((Button)findViewById(R.id.BackButton)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pickBeneficiary();
			}
		});
	}

	private String removeCountryPrefix(String number) {
		if (number.startsWith("+372")) {
			number = number.substring(4);
		}
		return number;
	}
	
	private void pickBeneficiary() {
		startActivityForResult(new Intent(Intent.ACTION_PICK, People.CONTENT_URI), PICK_CONTACT);
	}
	
	private BigDecimal evaluateAmount() {
		EditText amountEdit = (EditText)findViewById(R.id.amount);
		return new Calculator().evaluate(amountEdit.getText().toString());
	}
	
	private void makePayment(String number, BigDecimal amount, String pin) {
		String uri = "tel:1214*" + number + "*" + amount.toString().replace(".", "*");
		if (pin != null && pin.length() > 0) {
			uri +=  "w" + pin;
		}
		
		startActivity(new Intent(Intent.ACTION_CALL, parse(uri)));		
	}
	
	private AlertDialog alert(String message) {
		return new AlertDialog.Builder(MobilePayment.this)
		  .setTitle("Error")
		  .setMessage(message)
		  .setPositiveButton("OK", null)
		  .show();
	}
}
