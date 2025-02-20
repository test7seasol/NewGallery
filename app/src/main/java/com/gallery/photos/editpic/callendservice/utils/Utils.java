package com.gallery.photos.editpic.callendservice.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.format.DateUtils;

import com.gallery.photos.editpic.callendservice.model.ContactCDO;

import java.util.Calendar;

public class Utils {

    public static void openDialerPad(Context context, String str) {
        try {
            Intent intent = new Intent("android.intent.action.DIAL");
            intent.setData(Uri.parse("tel:" + str));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ContactCDO getContact(Context context, String phoneNumber) {
        ContactCDO contactCDO = null;
        Cursor cursor = null;

        try {
            // Build the query URI for the phone lookup
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            // Define the columns to fetch
            String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI, ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI, ContactsContract.PhoneLookup._ID // Contact ID
            };

            // Execute the query
            cursor = context.getContentResolver().query(uri, projection, null, null, null);

            // Check if the cursor returned any results
            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve contact details
                int contactId = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI));
                String photoThumbUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));

                // Create a ContactCDO object with the retrieved details
                contactCDO = new ContactCDO(contactId, displayName, photoUri, photoThumbUri);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        } finally {
            // Always close the cursor to release resources
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return contactCDO; // Return the contact or null if not found
    }


    public static String firstStringer(String str) {
        StringBuilder sb = new StringBuilder();
        if (str.contains(" ")) {
            String[] split = str.split(" ");
            int min = Math.min(split.length, 2);
            if (split.length > 0) {
                for (int i = 0; i < min; i++) {
                    if (!split[i].isEmpty()) {
                        sb.append(split[i].charAt(0));
                    }
                }
            }
        } else if (!str.isEmpty()) {
            sb = new StringBuilder(String.valueOf(str.charAt(0)));
        }
        return sb.toString();
    }

    public static String getPrettyDate(Context context, long j) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(j);
        return calendar2.get(6) == calendar.get(6) ? "Today" : calendar2.get(6) + (-1) == calendar.get(6) ? "Tomorrow" : DateUtils.formatDateTime(context, j, 98458);
    }
}
