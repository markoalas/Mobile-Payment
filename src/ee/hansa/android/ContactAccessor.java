package ee.hansa.android;

import android.app.Activity;
import android.content.Intent;


/**
 * Old API is used for Android 1.x, new API for Android 2.x
 */
public abstract class ContactAccessor {
	private static ContactAccessor sInstance;
	
	public abstract Intent getContactPickerIntent();
	public abstract String[] getNameAndNumber(Activity activity, Intent data);
	
	public static ContactAccessor getInstance() {
        if (sInstance == null) {
        	/*
            String className;
            int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
            if (sdkVersion <= Build.VERSION_CODES.DONUT) {
                className = "ContactAccessorOldApi";
            } else {
                className = "ContactAccessorNewApi";
            }
            try {
                Class<? extends ContactAccessor> clazz =
                        Class.forName(ContactAccessor.class.getPackage() + "." + className)
                                .asSubclass(ContactAccessor.class);
                sInstance = clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            */
        	sInstance = new ContactAccessorNewApi();
        }
        return sInstance;
    }
}
