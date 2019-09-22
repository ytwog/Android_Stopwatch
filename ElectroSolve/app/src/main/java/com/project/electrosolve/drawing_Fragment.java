package com.project.electrosolve;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 */
public class drawing_Fragment extends Fragment implements View.OnTouchListener
    {


    public class ElectroElement {
        private int type = -1;
        private float coordsX = 0;
        private float coordsY = 0;

        ElectroElement(int _type, float _x, float _y){
            type = _type;
            coordsX = _x;
            coordsY = _y;
        }

        ElectroElement(int _type){
            type = _type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public float getCoordsX() {
            return coordsX;
        }

        public float getCoordsY() {
            return coordsY;
        }
    }

    static Vector<ElectroElement> elements;
    static Vector<Point> points;
    DrawView CanvasView = null;
    int ChosenType = -1;
    ConstraintLayout DrawL;
    BroadcastReceiver broadcastReceiver;


    public drawing_Fragment() {
        // Required empty public constructor
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getApplicationContext().unregisterReceiver(this.broadcastReceiver);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CanvasView = new DrawView(getContext());
        DrawL = getView().findViewById(R.id._DrawLayout);
        DrawL.setOnTouchListener(this);
        DrawL.addView(CanvasView);
    }

        @Override
        public void onPause() {
            super.onPause();
            CanvasView = null;
        }

        @Override
    public void onResume() {
        super.onResume();
        if(CanvasView == null) {
            CanvasView = new DrawView(getContext());
            DrawL = getView().findViewById(R.id._DrawLayout);
            DrawL.addView(CanvasView);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        points = new Vector<>();
        elements = new Vector<>();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ChosenType = intent.getIntExtra("itemChosen", -1);
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_SENDTO);
        getActivity().getApplicationContext().registerReceiver(broadcastReceiver, filter);

        return inflater.inflate(R.layout.fragment_drawing, container, false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(ChosenType == -1) return false;
        if(ChosenType == -2) {
            elements.clear();
            return false;
        }
        Point point1 = new Point((int)event.getX(),(int)event.getY());
        points.add(point1);

        int type;
        ElectroElement EE = new ElectroElement(ChosenType, event.getX(), event.getY());
        elements.add(EE);
        return false;
    }

        static public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
            private DrawThread drawThread;

            int Xdisplay = 1200;
            int Ydisplay = 1800;
            int Xsize = 22;
            int Ysize = 36;
            public DrawView(Context context) {
                super(context);
                getHolder().addCallback(this);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(drawThread == null) {
                    drawThread = new DrawThread(getHolder());
                    drawThread.setRunning(true);
                    drawThread.start();
                }
                else {
                    drawThread.setRunning(true);
                }


            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                drawThread.setRunning(false);
                boolean retry = true;
                while (retry) {
                    try {
                        drawThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }

            class DrawThread extends Thread {

                private boolean running = false;
                private SurfaceHolder surfaceHolder;

                public DrawThread(SurfaceHolder surfaceHolder) {
                    this.surfaceHolder = surfaceHolder;
                }

                public void setRunning(boolean running) {
                    this.running = running;
                }

                @Override
                public void run() {
                    Canvas canvas;
                    do {
                        if (running) {
                            canvas = null;
                            try {
                                if(!running) return;
                                else canvas = surfaceHolder.lockCanvas(null);
                                if (canvas == null)
                                    continue;
                                Paint p;
                                p = new Paint();
                                // Фон
                                canvas.drawColor(getResources().getColor(R.color.colorBrightGray));
                                p.setColor(getResources().getColor(R.color.colorDarkGray));
                                for (int i = 0; i < Xsize; i++) {
                                    canvas.drawLine(i * (Xdisplay/Xsize), 0,
                                            i * (Xdisplay/Xsize), 1800, p);
                                }
                                for (int i = 0; i < Ysize; i++) {
                                    canvas.drawLine(0, i * (Ydisplay/Ysize),
                                            1200, i * (Ydisplay/Ysize), p);
                                }
                                Vector<ElectroElement> elements_ = (Vector<ElectroElement>)elements.clone();
                                for (ElectroElement EE : elements_) {
                                    Bitmap bitmap = null;
                                    switch (EE.getType())
                                    {
                                        case 0:
                                            bitmap = ((BitmapDrawable)getResources().getDrawable(R.mipmap._item_line).getCurrent()).getBitmap();
                                            break;
                                        case 1:
                                            bitmap = ((BitmapDrawable)getResources().getDrawable(R.mipmap._item_resistor).getCurrent()).getBitmap();
                                            break;
                                        default:

                                    }
                                    if(bitmap == null) continue;
                                    canvas.drawBitmap(bitmap, EE.getCoordsX(), EE.getCoordsY(), p);
                                    Log.d("mLOG", String.valueOf(EE.getCoordsX()) + ", " + String.valueOf(EE.getCoordsY()));
                                }

                                /*for (Point point : points_) {
                                    switch () {

                                    }
                                    Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.mipmap._item_line).getCurrent()).getBitmap();
                                    canvas.drawBitmap(bitmap, point.x, point.y, p);
                                    Log.d("mLOG", String.valueOf(point.x) + ", " + String.valueOf(point.y));
                                }*/

                            } finally {
                                if(!running) return;
                                if (canvas != null) {
                                    surfaceHolder.unlockCanvasAndPost(canvas);
                                }
                            }
                        }
                        else {
                            break;
                        }
                    } while(true);
                }
            }
        }

}
