package ee.hansa.android;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

import static android.provider.ContactsContract.CommonDataKinds.Phone;
import static android.provider.ContactsContract.Contacts;

public class ContactAccessorNewApi extends ContactAccessor {
  @Override
  public Intent getContactPickerIntent() {
    return new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
  }

  @Override
  public ContactInfo getContactInfo(Activity activity, Intent intent) {
    Cursor cursor = activity.managedQuery(intent.getData(), null, null, null, null);
    ContactInfo contactInfo = null;

    while (cursor.moveToNext()) {
      String contactId = cursor.getString(cursor.getColumnIndex(Contacts._ID));
      contactInfo = new ContactInfo(cursor.getString(cursor.getColumnIndexOrThrow(Contacts.DISPLAY_NAME)));

      String hasPhone = cursor.getString(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER));

      if (hasPhone.equalsIgnoreCase("1")) {
        Cursor phones = activity.getContentResolver().query(Phone.CONTENT_URI, new String[]{Phone.NUMBER}, Phone.CONTACT_ID + " = " + contactId, null, Phone.IS_PRIMARY + " DESC");
        while (phones.moveToNext()) {
          contactInfo.addPhoneNumber(phones.getString(0));
        }
        phones.close();
      }

    }
    cursor.close();
    return contactInfo;
  }
}
