package ee.hansa.android;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.Contacts.People.Phones;

@SuppressWarnings({"deprecation"})
public class ContactAccessorOldApi extends ContactAccessor {
	@Override
	public Intent getContactPickerIntent() {
		return new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
	}
	
	@Override
	public ContactInfo getContactInfo(Activity activity, Intent data) {
		Uri contactData = data.getData();
		Cursor c = activity.managedQuery(contactData, new String[]{People.DISPLAY_NAME, People.NUMBER}, null, null, Phones.ISPRIMARY + " DESC");
		if (c.moveToFirst()) {
			return new ContactInfo(c.getString(0)).addPhoneNumber(c.getString(1));
		}
		
		return null;
	}

}
