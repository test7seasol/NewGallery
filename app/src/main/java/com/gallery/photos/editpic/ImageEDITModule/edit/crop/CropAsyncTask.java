package com.gallery.photos.editpic.ImageEDITModule.edit.crop;//package com.gallery.photos.editphotovideo.ImageEDITModule.edit.crop;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.view.View;
//import android.widget.ProgressBar;
//import com.gallery.photos.editphotovideo.ImageEDITModule.edit.constants.StoreManager;
//import com.gallery.photos.editphotovideo.ImageEDITModule.edit.utils.MotionUtils;
//import com.gallery.photos.editphotovideo.ImageEDITModule.edit.views.SupportedClass;
//
///* loaded from: classes.dex */
//public class CropAsyncTask extends AsyncTask<Void, Void, Void> {
//    Activity activity;
//    Bitmap croppedBitmap;
//    int left;
//    Bitmap maskBitmap;
//    CropTaskCompleted onTaskCompleted;
//    ProgressBar progressBar;
//    int top;
//
//    private void show(final boolean z) {
//        Activity activity = this.activity;
//        if (activity != null) {
//            activity.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.crop.CropAsyncTask.1
//                @Override // java.lang.Runnable
//                public void run() {
//                    if (z) {
//                        CropAsyncTask.this.progressBar.setVisibility(View.VISIBLE);
//                    } else {
//                        CropAsyncTask.this.progressBar.setVisibility(View.GONE);
//                    }
//                }
//            });
//        }
//    }
//
//    public CropAsyncTask(CropTaskCompleted cropTaskCompleted, Activity activity, ProgressBar progressBar) {
//        this.onTaskCompleted = cropTaskCompleted;
//        this.activity = activity;
//        this.progressBar = progressBar;
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
//        this.croppedBitmap = StoreManager.getCurrentCroppedBitmap(this.activity);
//        this.left = StoreManager.croppedLeft;
//        this.top = StoreManager.croppedTop;
//        Bitmap currentCroppedMaskBitmap = StoreManager.getCurrentCroppedMaskBitmap(this.activity);
//        this.maskBitmap = currentCroppedMaskBitmap;
//        if (this.croppedBitmap == null && currentCroppedMaskBitmap == null) {
//            DeeplabMobile deeplabMobile = new DeeplabMobile();
//            deeplabMobile.initialize(this.activity.getApplicationContext());
//            Bitmap loadInBackground = loadInBackground(StoreManager.getCurrentOriginalBitmap(this.activity), deeplabMobile);
//            this.croppedBitmap = loadInBackground;
//            StoreManager.setCurrentCroppedBitmap(this.activity, loadInBackground);
//        }
//        return null;
//    }
//
//    @Override // android.os.AsyncTask
//    public void onPostExecute(Void r5) {
//        show(false);
//        this.onTaskCompleted.onTaskCompleted(this.croppedBitmap, this.maskBitmap, this.left, this.top);
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
//        this.maskBitmap = segment;
//        if (segment == null) {
//            return null;
//        }
//        Bitmap createClippedBitmap = BitmapUtils.createClippedBitmap(segment, (segment.getWidth() - round) / 2, (this.maskBitmap.getHeight() - round2) / 2, round, round2);
//        this.maskBitmap = createClippedBitmap;
//        Bitmap scaleBitmap = BitmapUtils.scaleBitmap(createClippedBitmap, width, height);
//        this.maskBitmap = scaleBitmap;
//        this.left = (scaleBitmap.getWidth() - width) / 2;
//        this.top = (this.maskBitmap.getHeight() - height) / 2;
//        StoreManager.croppedLeft = this.left;
//        int i = this.top;
//        StoreManager.croppedTop = i;
//        this.top = i;
//        StoreManager.setCurrentCroppedMaskBitmap(this.activity, this.maskBitmap);
//        return MotionUtils.cropBitmapWithMask(bitmap, this.maskBitmap, 0, 0);
//    }
//}
