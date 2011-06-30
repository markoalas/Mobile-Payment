package ee.hansa.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

/**
 * Old API is used for Android 1.x, new API for Android 2.x
 */
public abstract class ContactAccessor {
	private static ContactAccessor sInstance;

	public abstract Intent getContactPickerIntent();

	public abstract ContactInfo getContactInfo(Activity activity, Intent data);

	public static ContactAccessor getInstance() {
		if (sInstance == null) {
			try {
				Class<? extends ContactAccessor> clazz = Class.forName(ContactAccessor.class.getPackage().getName() + "." + getClassName())
						.asSubclass(ContactAccessor.class);
				sInstance = clazz.newInstance();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return sInstance;
	}

	private static String getClassName() {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR ? "ContactAccessorOldApi" : "ContactAccessorNewApi";
	}
}
