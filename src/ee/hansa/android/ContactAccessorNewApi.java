package ee.hansa.android;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactAccessorNewApi extends ContactAccessor {
	@Override
	public Intent getContactPickerIntent() {
		return new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
	}

	@Override
	public String[] getNameAndNumber(Activity activity, Intent intent) {
		Cursor cursor = activity.managedQuery(intent.getData(), null, null, null, null);
		String name = "", phoneNumber = "";

		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

			String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (hasPhone.equalsIgnoreCase("1")) {
				Cursor phones = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
				while (phones.moveToNext()) {
					phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				phones.close();
			}

		}
		cursor.close();
		return new String[] { name, phoneNumber };
	}
}
