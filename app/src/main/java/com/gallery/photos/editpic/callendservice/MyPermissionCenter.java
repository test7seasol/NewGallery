package com.gallery.photos.editpic.callendservice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MyPermissionCenter {

    public static boolean isOverlayPermissionEnabled(Context context) {
        return Settings.canDrawOverlays(context);
    }

    public interface OnPermissionChecked {
        void onAllowedPermission();
    }

    public void reqPermissions(Activity context, OnPermissionChecked onPermissionChecked) {
        Dexter.withContext(context).withPermissions(
            Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                callNextPermission(context, report, onPermissionChecked);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    public void callNextPermission(Activity context, MultiplePermissionsReport report, OnPermissionChecked onPermissionChecked) {

        if (report.isAnyPermissionPermanentlyDenied()) {
            showSettingsDialog(context);
        }
        if (report.areAllPermissionsGranted()) {

            onPermissionChecked.onAllowedPermission();
        }
    }

    public final void showSettingsDialog(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setCancelable(false);
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                context.startActivityForResult(intent, 1006);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        builder.show();
    }
}
