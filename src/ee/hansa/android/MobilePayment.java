package ee.hansa.android;

import static android.net.Uri.parse;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class MobilePayment extends Activity {
	private final int PICK_CONTACT = 0;
  private final ContactAccessor contactAccessor = ContactAccessor.getInstance();
  private TextView numberField;
  private TextView nameField;

  @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_layout);
    numberField = (TextView)findViewById(R.id.number);
    nameField = (TextView)findViewById(R.id.beneficiary);
    initCallLogDeleter();
		pickBeneficiary();
	}

	private void initCallLogDeleter() {
		PhoneStateListener deleter = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
				}
				getContentResolver().delete(CallLog.Calls.CONTENT_URI, "number like '1214*%'", null);
				getContentResolver().notifyChange(CallLog.Calls.CONTENT_URI, null);
			}
		};

		TelephonyManager tm = ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE));
		tm.listen(deleter, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		((TextView) findViewById(R.id.pin)).setText("");
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case PICK_CONTACT:
			if (resultCode == Activity.RESULT_OK) {
				try {
					ContactInfo result = contactAccessor.getContactInfo(this, data);
					showPaymentForm(result);
				} catch (Exception e) {
					alert(e.getClass().getSimpleName() + ": " + e.getMessage());
				}
			}
			break;
		}
	}

	private void showPaymentForm(ContactInfo contactInfo) {
    nameField.setText(contactInfo.getName());
    final String[] phoneNumbers = contactInfo.getPhoneNumbers();

    if (phoneNumbers.length > 1) {
      new AlertDialog.Builder(this)
        .setTitle(R.string.choose_number)
        .setSingleChoiceItems(phoneNumbers, -1, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int item) {
            numberField.setText(phoneNumbers[item]);
            dialog.dismiss();
          }
        })
        .create()
        .show();
    }
    else if (phoneNumbers.length == 0) {
      nameField.setText("");
      numberField.setText("");
      alert("No phone numbers");
    }
    else {
      numberField.setText(phoneNumbers[0]);
    }

		findViewById(R.id.PayButton).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        try {
          String pin = ((EditText)findViewById(R.id.pin)).getText().toString();
          makePayment(numberField.getText().toString().trim(), evaluateAmount(), pin);
        }
        catch (Exception e) {
          alert(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
      }

    });

		findViewById(R.id.BackButton).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        pickBeneficiary();
      }
    });
	}

	private void pickBeneficiary() {
		startActivityForResult(contactAccessor.getContactPickerIntent(), PICK_CONTACT);
	}

	private BigDecimal evaluateAmount() {
		EditText amountEdit = (EditText) findViewById(R.id.amount);
		return new Calculator().evaluate(amountEdit.getText().toString());
	}

	private void makePayment(String number, BigDecimal amount, String pin) {
		String uri = "tel:1214*" + number + "*" + amount.toString().replace(".", "*");
		if (pin != null && pin.length() > 0) {
			uri += "w" + pin;
		}

		startActivity(new Intent(Intent.ACTION_CALL, parse(uri)));
	}

	private AlertDialog alert(String message) {
		return new AlertDialog.Builder(this).setTitle("Error").setMessage(message).setPositiveButton("OK", null).show();
	}
}
