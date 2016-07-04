package com.sundeep.buttonoverlay;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class CaptureSwipeActivity extends AppCompatActivity {

    private String tempDir;
    private File mPath;
    private Button clearBtn,cancelBtn,saveBtn;
    private LinearLayout swipeAreaLL;
    private SwipeView swipeView;
    private Bitmap mBitmap;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_swipe);

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        swipeAreaLL = (LinearLayout) findViewById(R.id.swipeArea);

        tempDir = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/";

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(getResources().getString(R.string.external_dir), Context.MODE_PRIVATE);

        prepareDirectory();
        mPath = new File(directory, getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random() + ".png");

        swipeAreaLL = (LinearLayout) findViewById(R.id.swipeArea);
        swipeView = new SwipeView(this);
        swipeView.setBackgroundColor(Color.WHITE);
        swipeAreaLL.addView(swipeView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        clearBtn = (Button)findViewById(R.id.clearBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);
        saveBtn = (Button)findViewById(R.id.saveBtn);
        saveBtn.setEnabled(false);
        mView = swipeAreaLL;

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CaptureSwipeActivity", "Clearing canvas");
                swipeView.clear();
                saveBtn.setEnabled(true);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CaptureSwipeActivity","Saving canvas");
                mView.setDrawingCacheEnabled(true);
                swipeView.save(mView);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CaptureSwipeActivity", "Cancelling canvas");
                finish();
            }
        });
    }

    private boolean prepareDirectory(){
        try{
            return makedirs();
        } catch (Exception e){
            Toast.makeText(this, "Could not initiate File System.. Is Sdcard mounted properly?", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    private boolean makedirs(){
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();

        if (tempdir.isDirectory()){
            File[] files = tempdir.listFiles();
            for (File file : files){
                if (!file.delete()){
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }

    private String getTodaysDate() {
        final Calendar c = Calendar.getInstance();
        int todaysDate = (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:", String.valueOf(todaysDate));
        return (String.valueOf(todaysDate));
    }

    private String getCurrentTime() {
        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }


    public class SwipeView extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

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

                String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
                Log.d("CaptureSwipeActivity","URL:"+url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void clear(){
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path,paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            saveBtn.setEnabled(true);

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX,eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;
                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++)
                    {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    Log.d("CaptureSwipeActivity","Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left){
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY)
        {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

}
