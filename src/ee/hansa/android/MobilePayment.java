package ee.hansa.android;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.Contacts.People.Phones;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MobilePayment extends Activity {
	private final int PICK_CONTACT = 0;
	private final int MAKE_CALL = 1;
	
	private final int DIALOG_INVALID_AMOUNT = 0;
	private final int DIALOG_NO_PHONE_NUMBER = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_layout);
		startActivityForResult(new Intent(Intent.ACTION_PICK, People.CONTENT_URI), PICK_CONTACT);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_NO_PHONE_NUMBER) {
			return new AlertDialog.Builder(this).setTitle("Error").setMessage("Phone number not found!").setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							startActivityForResult(new Intent(Intent.ACTION_PICK, People.CONTENT_URI), PICK_CONTACT);
						}
					}).create();
		} else if (id == DIALOG_INVALID_AMOUNT){
			return new AlertDialog.Builder(this).setTitle("Error").setMessage("Invalid amount!").setPositiveButton("OK", null).create();
		}
		else {
			return null;
		}
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case PICK_CONTACT:
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, new String[] { People.DISPLAY_NAME, Phones.NUMBER }, null, null, Phones.ISPRIMARY + " DESC");
				if (c.moveToFirst()) {
					showPaymentForm(c.getString(0), c.getString(1));
				}
			} else {
				finish();
			}
			break;

		case MAKE_CALL:
			finish();
			break;
		}
	}

	private void showPaymentForm(String beneficiaryName, String number) {
		try {
			if (number.startsWith("+372")) {
				number = number.substring(4);
			}

			((TextView) findViewById(R.id.beneficiary)).setText(beneficiaryName);
			((TextView) findViewById(R.id.number)).setText(number);

			final String finalNumber = number;
			((Button) findViewById(R.id.PayButton)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					try {
						EditText amountEdit = (EditText) findViewById(R.id.amount);
						double amount = Double.valueOf(amountEdit.getText().toString());
						makePayment(finalNumber, amount);
					} catch (Exception e) {
						showDialog(DIALOG_INVALID_AMOUNT);
					}
				}
			});
		} catch (Exception e) {
			showDialog(DIALOG_NO_PHONE_NUMBER);
		}
	}

	private void makePayment(String number, double amount) {
		String amountStr = new DecimalFormat("0.00").format(amount).replace(".", "*");
		startActivityForResult(new Intent(Intent.ACTION_CALL, Uri.parse("tel:1214*" + number + "*" + amountStr + "w1234")), MAKE_CALL);
	}
}