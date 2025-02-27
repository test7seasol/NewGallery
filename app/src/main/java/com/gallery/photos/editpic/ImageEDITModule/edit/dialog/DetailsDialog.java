package com.gallery.photos.editpic.ImageEDITModule.edit.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class DetailsDialog {
    public static void showDetailsDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GO SETTINGS", new DialogInterface.OnClickListener() { // from class: com.gallery.photos.editphotovideo.dialog.DetailsDialog$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                DetailsDialog.lambda$showDetailsDialog$0(activity, dialogInterface, i);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.gallery.photos.editphotovideo.dialog.DetailsDialog$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    static   void lambda$showDetailsDialog$0(Activity activity, DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
        openDetails(activity);
    }

    public static void openDetails(Activity activity) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", "com.gallery.photos.editphotovideo", null));
        activity.startActivityForResult(intent, 101);
    }
}
