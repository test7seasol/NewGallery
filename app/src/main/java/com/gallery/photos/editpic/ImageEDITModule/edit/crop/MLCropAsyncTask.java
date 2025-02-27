package com.gallery.photos.editpic.ImageEDITModule.edit.crop;//package com.gallery.photos.editphotovideo.ImageEDITModule.edit.crop;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.view.View;
//import android.widget.ProgressBar;
//import com.gallery.photos.editphotovideo.ImageEDITModule.edit.constants.StoreManager;
//import com.gallery.photos.editphotovideo.ImageEDITModule.edit.views.SupportedClass;
//
///* loaded from: classes.dex */
//public class MLCropAsyncTask extends AsyncTask<Void, Void, Void> {
//    Activity activity;
//    ProgressBar crop_progress_bar;
//    Bitmap cropped;
//    int left;
//    Bitmap mask;
//    MLOnCropTaskCompleted onTaskCompleted;
//    int top;
//
//    private void show(final boolean z) {
//        Activity activity = this.activity;
//        if (activity != null) {
//            activity.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.crop.MLCropAsyncTask.1
//                @Override // java.lang.Runnable
//                public void run() {
//                    if (z) {
//                        MLCropAsyncTask.this.crop_progress_bar.setVisibility(View.VISIBLE);
//                    } else {
//                        MLCropAsyncTask.this.crop_progress_bar.setVisibility(View.GONE);
//                    }
//                }
//            });
//        }
//    }
//
//    public MLCropAsyncTask(MLOnCropTaskCompleted mLOnCropTaskCompleted, Activity activity, ProgressBar progressBar) {
//        this.onTaskCompleted = mLOnCropTaskCompleted;
//        this.activity = activity;
//        this.crop_progress_bar = progressBar;
//    }
//
//    @Override // android.os.AsyncTask
//    public void onPreExecute() {
//        super.onPreExecute();
//        show(true);
//    }
//
//    @Override // android.os.AsyncTask
//    public Void doInBackground(Void... voidArr) {
//        this.cropped = StoreManager.getCurrentCroppedBitmap(this.activity);
//        this.left = StoreManager.croppedLeft;
//        this.top = StoreManager.croppedTop;
//        Bitmap currentCroppedMaskBitmap = StoreManager.getCurrentCroppedMaskBitmap(this.activity);
//        this.mask = currentCroppedMaskBitmap;
//        if (this.cropped == null && currentCroppedMaskBitmap == null) {
//            DeeplabMobile deeplabMobile = new DeeplabMobile();
//            deeplabMobile.initialize(this.activity.getApplicationContext());
//            Bitmap loadInBackground = loadInBackground(StoreManager.getCurrentOriginalBitmap(this.activity), deeplabMobile);
//            this.cropped = loadInBackground;
//            StoreManager.setCurrentCroppedBitmap(this.activity, loadInBackground);
//        }
//        return null;
//    }
//
//    @Override // android.os.AsyncTask
//    public void onPostExecute(Void r5) {
//        show(false);
//        this.onTaskCompleted.onTaskCompleted(this.cropped, this.mask, this.left, this.top);
//    }
//
//    public Bitmap loadInBackground(Bitmap bitmap, DeeplabMobile deeplabMobile) {
//        if (bitmap == null) {
//            return null;
//        }
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        float max = 513.0f / Math.max(bitmap.getWidth(), bitmap.getHeight());
//        int round = Math.round(width * max);
//        int round2 = Math.round(height * max);
//        Bitmap segment = deeplabMobile.segment(SupportedClass.tfResizeBilinear(bitmap, round, round2));
//        this.mask = segment;
//        if (segment == null) {
//            return null;
//        }
//        Bitmap createClippedBitmap = BitmapUtils.createClippedBitmap(segment, (segment.getWidth() - round) / 2, (this.mask.getHeight() - round2) / 2, round, round2);
//        this.mask = createClippedBitmap;
//
//        Log.e("TAGed", "loadInBackground:width  "+width );
//        Log.e("TAGed", "loadInBackground:height  "+height );
//
//
//        Bitmap scaleBitmap = BitmapUtils.scaleBitmap(createClippedBitmap, width, height);
//        this.mask = scaleBitmap;
//        this.left = (scaleBitmap.getWidth() - width) / 2;
//        this.top = (this.mask.getHeight() - height) / 2;
//        StoreManager.croppedLeft = this.left;
//        int i = this.top;
//        StoreManager.croppedTop = i;
//        this.top = i;
//        StoreManager.setCurrentCroppedMaskBitmap(this.activity, this.mask);
//        return SupportedClass.cropBitmapWithMask(bitmap, this.mask, 0, 0);
//    }
//}
