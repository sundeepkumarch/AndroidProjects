package com.sundeep.buttonoverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;

public class SwipeView extends View {

    private static final float STROKE_WIDTH = 5f;
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    private Paint paint = new Paint();
    private Path path = new Path();

    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();

    private Bitmap mBitmap;
    private LinearLayout swipeAreaLL;
    private File mPath;


    public SwipeView(Context context) {
        super(context);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    public void save(View v){
        if(mBitmap == null){
            mBitmap = Bitmap.createBitmap(swipeAreaLL.getWidth(), swipeAreaLL.getHeight(), Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(mBitmap);

        try {
            FileOutputStream mFileOutputStream = new FileOutputStream(mPath);

            v.draw(canvas);

            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutputStream);
            mFileOutputStream.flush();
            mFileOutputStream.close();

//            String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
