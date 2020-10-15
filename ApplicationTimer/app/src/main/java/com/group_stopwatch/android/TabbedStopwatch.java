package com.group_stopwatch.android;

 import android.app.AlertDialog;
 import android.app.NotificationManager;
 import android.app.PendingIntent;
 import android.bluetooth.BluetoothAdapter;
 import android.bluetooth.BluetoothDevice;
 import android.bluetooth.BluetoothServerSocket;
 import android.bluetooth.BluetoothSocket;
 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.ContextWrapper;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.content.IntentFilter;
 import android.content.pm.ActivityInfo;
 import android.content.res.Resources;
 import android.graphics.Canvas;
 import android.graphics.Color;
import android.graphics.Paint;
 import android.graphics.Point;
 import android.graphics.PorterDuff;
 import android.graphics.RectF;
 import android.graphics.Typeface;
 import android.graphics.drawable.GradientDrawable;
 import android.os.Handler;
import android.os.SystemClock;
 import android.provider.SyncStateContract;
 import android.speech.RecognizerIntent;
 import android.support.constraint.ConstraintLayout;
 import android.support.constraint.ConstraintSet;
 import android.support.v4.app.NotificationCompat;
 import android.support.v7.app.AppCompatActivity;
 import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
 import android.text.Layout;
 import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.AbsoluteSizeSpan;
 import android.util.DisplayMetrics;
 import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
 import android.view.KeyEvent;
 import android.view.LayoutInflater;
import android.view.Menu;
 import android.view.MotionEvent;
 import android.view.SurfaceHolder;
 import android.view.SurfaceView;
 import android.view.View;
import android.view.ViewGroup;

 import android.view.WindowManager;
 import android.widget.AdapterView;
 import android.widget.ArrayAdapter;
 import android.widget.Button;
import android.widget.CheckBox;
 import android.widget.CompoundButton;
 import android.widget.EditText;
 import android.widget.HorizontalScrollView;
 import android.widget.ImageButton;
 import android.widget.LinearLayout;
 import android.widget.ListView;
 import android.widget.RadioButton;
import android.widget.RadioGroup;
 import android.widget.ScrollView;
 import android.widget.Space;
 import android.widget.Switch;
 import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


 import java.io.BufferedReader;
import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.lang.reflect.Array;
 import java.net.NetworkInterface;
 import java.nio.channels.InterruptibleChannel;
 import java.security.Key;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.security.interfaces.RSAKey;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.List;
 import java.util.Set;
 import java.util.UUID;
 import java.util.Vector;
 import java.util.logging.LogManager;

 import javax.crypto.Cipher;
 import javax.crypto.KeyGenerator;
 import javax.crypto.NoSuchPaddingException;

 import static java.lang.Math.PI;
 import static java.lang.Math.max;
 import static java.lang.Math.min;
 import static java.lang.Math.sqrt;


public class TabbedStopwatch extends AppCompatActivity {
    //Данные фрагментов
    static public    int activateTimes = 50;
    static String currentTime;
    static public DrawView CanvasView;
    static Display display;
    static public class Pair<F, S> {
        public F first;
        public S second;
    }
    static int ActivatedPromo;
    static protected DisplayMetrics metrics;
    static protected boolean hasCoords1 = false, hasCoords2 = false;
    static protected Pair<Double, Double> arrCoordinates1[], arrCoordinates2[];
    static protected Pair<Integer, Long> arrRestore[];
    static protected float minSize = 80;
    static protected TableRow arrRowData[];
    static protected ContextWrapper CW ;
    static protected TextView arrTextLapsTime[], arrTextData[], arrTimeData[], arrLapsData[];
    static protected Fragment_Settings.SettingsActivity Tab3;
    static protected Fragment_Database.database Tab2;
    static protected Fragment_Main.MainActivity Tab1;
    static protected pictureFragment Tab0;
    static long startTime=0L, timeInMilliseconds=0L, timeSwapBuffer, updateTime=0L, arrTiming[];
    static int secs, mins, milliseconds, nowRestoring;
    static protected boolean dataExist, isCheck = false, isStarted = false, ok_Speech, isTouch = false, BuiltOnce = false, buttonsCreated = false;
    static protected boolean TouchedNumbers[], toUpdate;
    static protected SettingsData SData;
    static protected String arrIDNumber[];
    static protected NotificationManager notificationManager;
    //Класс участника
    protected static class RunnerData{
        protected int LapsTaken = 0;
        protected String IDNumber = "0";
        protected long lapTime[] = new long[101];
        protected int arrMinutes[] = new int[100];
        protected int arrSeconds[] = new int[100];
        protected int arrMilisnd[] = new int[100];
        protected long lastTime = 0;
        protected long afterWaitLeft = 0;
        protected boolean isHidden = false;
        protected long timeHidden = 0;
        protected long startTime1 = 0;
        protected long lastLapTime;
        //setters
        void setHidden() {
            isHidden = true; startTime1 = updateTime; LapsTaken++;
        }
        void setIDNumber(String ID){ IDNumber = ID; }
        void setTime(int index, int min, int sec, int mil){
            arrMinutes[index] = min;
            arrSeconds[index] = sec;
            arrMilisnd[index] = mil;
        }
        void passLap(long _time){
            lastTime = _time;
        }
        //getters
        int getLapsTaken(){ return LapsTaken; }
        String getIDNumber(){ return IDNumber; }
        //Метод для проверки на появление кнопки
        public void reset(){
            startTime1 = timeHidden = 0;
            isHidden = false;
            lastTime = 0;
            LapsTaken = 0;
            lapTime = new long[101];
            arrMinutes = new int[100];
            arrSeconds = new int[100];
            arrMilisnd = new int[100];
        }

        public boolean passSecond() {
            afterWaitLeft = updateTime - startTime1;
            timeHidden = updateTime - startTime1 - SData.afterTime;
            if(timeHidden>=(SData.getTimeUntilShown()*1000)) {
                afterWaitLeft = timeHidden = 0; isHidden = false;
            }
            return isHidden;
        }
    }

    //Класс настроек
    protected static class SettingsData {
        public boolean formMain = true;
        public boolean smallView = false;
        public boolean finishedOrLeft = true;
        protected int Dialog_Param1 = 400;
        protected int Dialog_Param2 = 3000;
        protected int Dialog_Param3 = 20;
        protected int mapType = 1; // 1 - ellipse
        protected int runnerNum = 10;
        protected int laps = 10;
        protected int numX = 3;
        protected int numY = 4;
        protected int afterTime = 1;
        protected int styleID = 0;
        protected boolean OnVolumeStart = false;
        protected boolean staticButtons = true;
        protected boolean accuracyFlag  = true;
        protected int timeUntilSHown = 10;
        protected boolean slashLaps = true;

        SettingsData(){}

        // Setters
        public void setSlashLaps (boolean _slashLaps) {slashLaps = _slashLaps;}
        public void setTimeUntilSHown(int _time) {timeUntilSHown = _time;}
        public void setAccuracyFlag(boolean _accuracyFlag) {accuracyFlag = _accuracyFlag;}
        public void setStaticButtons(boolean _staticButtons) {staticButtons = _staticButtons;}
        public void setLaps(int _laps) {laps = _laps;}
        public void setRunners(int _runnerNumber) {
            runnerNum = _runnerNumber;
            setFieldX((int) (sqrt(runnerNum)) + (runnerNum % (sqrt(runnerNum) > 0 ? 1 : 0)));
        }
        private void setFieldX(int _numX) {
            numX = _numX;
            numY = runnerNum / _numX + ((runnerNum % _numX > 0 ) ? 1 : 0);
        }

        // Getters
        public boolean getslashLaps() {return slashLaps;}
        public int getTimeUntilShown() {return timeUntilSHown;}
        public boolean getAccuracyFlag() {return accuracyFlag;}
        public boolean getStaticButtons() {return staticButtons;}
        public int getLaps() {return laps;}
        public int getRunners() {
            return runnerNum;
        }
        public int getFieldX() {
            return numX;
        }
        public int getFieldY() {
            return numY;
        }
        public boolean getFinishedOrLeft() {
            return finishedOrLeft;
        }

    }

    static int toDP(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Tab2.getResources().getDisplayMetrics());
    }

    static public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
        // Variables to input
        public long default_time;
        public long CurrentLapTime[];
        private DrawThread drawThread;

        //TODO ---------------------------------------------- Horizontal view -------------------------------------------------------
        // public int p1 = 10, p2 = 15, p3 = 316, p4, accur = 400;
        public int p1 = 16, p2 = 15, p3 = 310, p4, accur = 400;

        RectF rectf1, rectf2, rectf3;
        int _A, _B;
        int zeroX, zeroY;
        int x1;
        boolean upper = true;;

        public DrawView(Context context, int _p4) {
            super(context);
            metrics = getContext().getResources().getDisplayMetrics();
            p3 = (int) (metrics.widthPixels/(3.5));
            _A = (p3 - p1)/2;
            zeroX = _A + p1;
            x1 = p1;
            default_time = SData.timeUntilSHown * 1000;//(long)((double)(SData.Dialog_Param1) * 3600 / SData.Dialog_Param3);
            p4 = _p4 - 5;
            _B = (p4-p2)/2;
            zeroY = _B + p2;
            if(SData.smallView) {
                rectf1 = new RectF(toDP(p1),toDP(p2),toDP(p1+2*_B),toDP(p4));
                rectf2 = new RectF(toDP(p1+_B),toDP(p2),toDP(p3-_B),toDP(p4));
                rectf3 = new RectF(toDP(p3-2*_B),toDP(p2),toDP(p3),toDP(p4));
            } else {
                rectf1 = new RectF(toDP(p1),toDP(p2),toDP(_B-p1/2),toDP(p4/2));
                rectf2 = new RectF(toDP(p1),toDP(p2+_B/2),toDP(_B-p1/2),toDP((p4+_B)/2));
                rectf3 = new RectF(toDP(p1),toDP(p2+_B),toDP(_B-p1/2),toDP(p4/2+_B));
            }
            if(!hasCoords1 && _p4 == 160) // Если нужно составить координаты для маленького графика
                findCoordinates1(p3-p1, p4-p2, accur);
            if(!hasCoords2 && _p4 == 660) // Если нужно составить координаты для большого графика
                findCoordinates2(p3-p1, p4-p2, accur);
            CurrentLapTime = new long[101];
            getHolder().addCallback(this);
        }

        private double findY(double ask)
        {
            double res;
            if(ask > -_B) {
                if (ask > _A-_B)
                    res = _B * (Math.pow(Math.abs(1.0 - Math.abs(Math.pow((-ask+_A-_B)/_B, 2))), 1.0/2.0)) * (upper ? -1 : 1);
                else
                    res = _B * (upper ? -1 : 1);
            }
            else
                res = _B * (Math.pow(Math.abs(1.0 - Math.abs(Math.pow((ask+_A-_B)/_B, 2))), 1.0/2.0)) * (upper ? -1 : 1);
            //ask -= _A/2;
            //res = _B * (Math.pow(Math.abs(1.0 - Math.abs(Math.pow((ask-2*_B)/_B, 2))), 1.0/2.0)) * (upper ? -1 : 1);
            //double tempParam = ask*ask/_A/_A;
            //res = (sqrt(Math.abs(_B*_B*(1.0 - tempParam)))) * (upper ? -1 : 1);
            return res;
        }

        private double findY2(double ask, boolean boo)
        {
            return sqrt(Math.pow(_A,2) - Math.pow(ask,2))*(ask > 0 ? 1 : -1) + 170 * (ask>0 || boo?1:-1);
        }


        public void findCoordinates2(int w, int h, int acc)
        {
            double value_distance = 2*PI*sqrt((double)(w*w)/8+(double)(h*h)/8)*1.25;
            double per_distance = value_distance / (acc);
            double step = Math.min(per_distance / 50, 0.02);
            arrCoordinates2 = new Pair[acc+1];
            arrCoordinates2[0] = new Pair<>();
            arrCoordinates2[0].first = 0.0; // x coordinate
            arrCoordinates2[0].second = (double)-_B; // y coordinate
            double y_pot;
            double y_buff = 0.05;
            for(int i = 1; i < acc*95/100; i++)
            {
                arrCoordinates2[i] = new Pair<>();
                double x_predict = arrCoordinates2[i-1].first;
                double q = per_distance, q_prev;
                int stackSaver = 1000000;
                do
                {
                    stackSaver--;
                    q_prev = q;
                    boolean boo = false;
                    if((i > acc*2.35/20 && i < acc*6/20) || (i > acc-acc*80/200 && i < acc-acc*3/18)) {
                        if(i > acc-acc*80/200 && i < acc-acc*3/18 && i > acc/2)
                            boo = true;
                        y_buff+=0.01;
                    }
                    else {
                        y_buff = 0.05;
                        if(i < acc/4 || i >= acc*3/4) {
                            x_predict+=step;
                        }
                        else x_predict-=step;
                    }
                    if(x_predict > _A || x_predict < -_A) {
                        x_predict = arrCoordinates2[i-1].first;
                    }
                    y_pot = findY2(x_predict, boo);
                    if((i < acc*6/20 || (i > acc*8.7/20)) && (i < acc-acc*80/200))
                        y_pot *= -1;
                    if(i > acc/2) {
                        y_pot -= y_buff;
                    } else {
                        y_pot += y_buff;
                    }
                    q = Math.abs(Math.sqrt(Math.pow(x_predict - arrCoordinates2[i-1].first, 2) +
                            Math.pow(y_pot - arrCoordinates2[i-1].second, 2)) - per_distance);
                }while(q_prev > q && stackSaver > 0);
                arrCoordinates2[i].first = x_predict;
                arrCoordinates2[i].second = y_pot;
            }
            for(int i = acc*95/100; i < acc; i++) {
                arrCoordinates2[i] = new Pair<>();
                arrCoordinates2[i].first = 0.0;
                arrCoordinates2[i].second = (double)-_B;
            }
            hasCoords2 = true;
        }

        public void findCoordinates1(int w, int h, int acc)
        {
            double value_distance = 2*PI*sqrt((double)(w*w)/8+(double)(h*h)/8);
            double per_distance = value_distance*1.0085 / (acc);
            double step = Math.min(per_distance / 50, 0.02);
            arrCoordinates1 = new Pair[acc+1];
            arrCoordinates1[0] = new Pair<>();
            arrCoordinates1[0].first = (double)-_A;// x coordinate
            arrCoordinates1[0].second = 0.0;// y coordinate
            double y_pot;
            for(int i = 1; i < acc; i++)
            {
                arrCoordinates1[i] = new Pair<>();
                double x_predict = arrCoordinates1[i-1].first;
                double q = per_distance, q_prev;
                int stackSaver = 1000000;
                do
                {
                    stackSaver--;
                    q_prev = q;
                    if(i >= acc/2) {
                        upper = false;
                        x_predict-=step;
                    }
                    else x_predict+=step;
                    if(x_predict > _A) x_predict = arrCoordinates1[i-1].first;
                    if(x_predict < -_A) x_predict = arrCoordinates1[i-1].first;
                    y_pot = findY(x_predict);
                    q = Math.abs(Math.sqrt(Math.pow(x_predict - arrCoordinates1[i-1].first, 2) +
                            Math.pow(y_pot - arrCoordinates1[i-1].second, 2)) - per_distance);
                }while(q_prev > q && stackSaver > 0);
                arrCoordinates1[i].first = x_predict;
                arrCoordinates1[i].second = y_pot;
            }
            hasCoords1 = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            drawThread = new DrawThread(getHolder());
            drawThread.setRunning(true);
            drawThread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            joinThread();
        }

        public void joinThread() {
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
                        boolean ya = Tab2.isVisible();
                        if(ya) running = false;
                        canvas = null;
                        try {
                            if(!running) return;
                            else canvas = surfaceHolder.lockCanvas(null);
                            if (canvas == null)
                                continue;
                            Paint p;
                            p = new Paint();
                            // Фон
                            if(!running) return;
                            canvas.drawColor(getResources().getColor(R.color.brickYellow));
                            // Круг
                            p.setColor(getResources().getColor(R.color.grassGreen));
                            if(!running) return;
                            canvas.drawOval(rectf1, p);
                            canvas.drawRect(rectf2, p);
                            canvas.drawOval(rectf3, p);
                            // Точки
                            if(isStarted)
                            {
                                for(int i = 0; i < SData.getRunners(); i++) {
                                    if(Tab1.runData[i].LapsTaken - SData.getLaps() < 0) {
                                        CurrentLapTime[i] = updateTime - arrTiming[i];
                                        if(Tab1.runData[i].LapsTaken == 0)
                                            x1 = (int)((double)accur*((double)CurrentLapTime[i]/(double)default_time));
                                            //default_time = (long)((double)(SData.Dialog_Param1) * 3600 / SData.Dialog_Param3);
                                        else {
                                            double averageTime = Tab1.runData[i].lastLapTime;//arrTiming[i] / Tab1.runData[i].LapsTaken;
                                            x1 = (int) ((double) accur * ((double) CurrentLapTime[i] / averageTime));
                                        }
                                        x1 = Math.min(accur-1, x1);
                                        p.setColor(getResources().getColor(R.color.bright_red));
                                        p.setStrokeWidth(10);
                                        if(p4 < 200) canvas.drawPoint(toDP(arrCoordinates1[x1].first.intValue() + zeroX),
                                                toDP(zeroY + arrCoordinates1[x1].second.intValue()), p);
                                        else canvas.drawPoint(toDP(arrCoordinates2[x1].first.intValue() + zeroX),
                                                toDP(zeroY + arrCoordinates2[x1].second.intValue()), p);
                                        p.setTextSize(8 + 22*x1/accur);
                                        if(p4 < 200) canvas.drawText(Tab1.runData[i].IDNumber, toDP(arrCoordinates1[x1].first.intValue() + zeroX),
                                                toDP(zeroY + arrCoordinates1[x1].second.intValue()-10),p);
                                        else canvas.drawText(Tab1.runData[i].IDNumber, toDP(arrCoordinates2[x1].first.intValue() + zeroX),
                                                toDP(zeroY + arrCoordinates2[x1].second.intValue()-10),p);
                                    }
                                }
                            }
                            else
                            {
                                if(!running) return;
                                if(p4 < 200) canvas.drawPoint(toDP(arrCoordinates1[0].first.intValue() + zeroX),
                                        toDP(zeroY + arrCoordinates1[0].second.intValue()), p);
                                else canvas.drawPoint(toDP(arrCoordinates2[0].first.intValue() + zeroX),
                                        toDP(zeroY + arrCoordinates2[0].second.intValue()), p);
                            }

                        }  catch (Exception e) {
                            running = false;
                        }  finally {
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




    //--------------------------------PictureFragment------------------------------------------------------\\
    public static class pictureFragment extends Fragment {
        public View rootView;
        protected ConstraintLayout CanvasL;
        protected ImageButton ManagePic, ImButFull;
        protected boolean agreedOnShow = false;
        @Override public void onResume() {
            super.onResume();
            if(agreedOnShow) setPictureSize(SData.smallView);
        }

        private void setPictureSize(boolean isSmall) {
            if(CanvasView != null) {
                //CanvasView.joinThread();
            }
            if(isSmall) {
                metrics = getContext().getResources().getDisplayMetrics();
                Log.d("TEST", String.valueOf(metrics.widthPixels));
                Log.d("TEST", String.valueOf(CanvasL.getWidth()));
                int heightDesired =  (int)(CanvasL.getWidth()*160/300);
                ViewGroup.LayoutParams t = CanvasL.getLayoutParams();
                t.height = heightDesired;
                CanvasL.setLayoutParams(t);
                CanvasView = new DrawView(rootView.getContext(), 160);
                CanvasL.addView(CanvasView);
                ImButFull.getBackground().setColorFilter(Color.rgb(0x90, 0x31, 0x15), PorterDuff.Mode.MULTIPLY);
            }
            else {
                metrics = getContext().getResources().getDisplayMetrics();
                int heightDesired = (int)min(CanvasL.getWidth()*2.0, metrics.heightPixels*0.9);
                ViewGroup.LayoutParams t = CanvasL.getLayoutParams();
                t.height = heightDesired;
                CanvasL.setLayoutParams(t);
                CanvasView = new DrawView(rootView.getContext(), 660);
                CanvasL.addView(CanvasView);
                ImButFull.getBackground().clearColorFilter();
            }
        }

        public void onDestroy() {
            super.onDestroy();
        }

        public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            rootView = inflater.inflate(R.layout.activity_picture, container, false);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            CanvasL = (ConstraintLayout)rootView .findViewById(R.id.CanvasLayout);
            ManagePic = (ImageButton) rootView.findViewById(R.id.managePicture);
            ImButFull = (ImageButton) rootView.findViewById(R.id.imageButFull);

            ImButFull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agreedOnShow = true;
                    SData.smallView = !SData.smallView;
                    setPictureSize(SData.smallView);
                }
            });

            ManagePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder mbuilder = new AlertDialog.Builder(getActivity());
                    LayoutInflater minflater = getActivity().getLayoutInflater();
                    mbuilder.setView(inflater.inflate(R.layout.activity_settingspicture, null))
                            .setNegativeButton(R.string._OK_str, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog mAD =  mbuilder.create();
                    mAD.show();
                    //first param
                    final EditText eText1 = mAD.findViewById(R.id.dialog1);
                    final EditText eText2 = mAD.findViewById(R.id.dialog2);
                    mAD.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            SData.Dialog_Param1 = Integer.parseInt(eText1.getText().toString());
                            SData.Dialog_Param2 = Integer.parseInt(eText2.getText().toString());

                            CanvasView.default_time = SData.timeUntilSHown;//(long)((double)(SData.Dialog_Param1) * 3.6 / SData.Dialog_Param3) * 1000;
                        }
                    });
                    eText1.setText(String.valueOf(SData.Dialog_Param1));
                    eText2.setText(String.valueOf(SData.Dialog_Param2));
                }
            });
            return rootView;
        }

        //Заполнение таблицы информацией
    }

    //-----------------------------------------------------------------------------------------------------\\

    static protected void showTable(){
        class sortTemp
        {
            sortTemp() {
                col1 = new String();
            }
            String col1;
            long col2;
            int col3;
            long col4[];
        }
        sortTemp []S = new sortTemp[SData.getRunners()];
        for(int i = 0; i < SData.getRunners(); i++) {
            S[i] = new sortTemp();
            S[i].col1 = arrIDNumber[i];
            S[i].col3 = Tab1.runData[i].LapsTaken;
            if(Tab2.ShownCanvas) {
                S[i].col4 = Tab1.runData[i].lapTime;
            }
            else {
                S[i].col2 = arrTiming[i];
            }
        }
        class ComparSort implements Comparator<sortTemp> {
            public int compare(sortTemp first, sortTemp second) {
                if((Tab2.int_Column1 != 0) || (first.col2 == second.col2 && first.col3 == second.col3) || Tab2.ShownCanvas){
                    boolean resultBool;
                    boolean firstNum = true, secondNum = true;
                    for(int i = 0; i < first.col1.length(); i++) {
                        if(first.col1.charAt(i) != '0' && first.col1.charAt(i) != '1' &&
                                first.col1.charAt(i) != '2' && first.col1.charAt(i) != '3' &&
                                first.col1.charAt(i) != '4' && first.col1.charAt(i) != '5' &&
                                first.col1.charAt(i) != '6' && first.col1.charAt(i) != '7' &&
                                first.col1.charAt(i) != '8' && first.col1.charAt(i) != '9'
                        ) firstNum = false;
                    }
                    for(int i = 0; i < second.col1.length(); i++) {
                        if(second.col1.charAt(i) != '0' && second.col1.charAt(i) != '1' &&
                                second.col1.charAt(i) != '2' && second.col1.charAt(i) != '3' &&
                                second.col1.charAt(i) != '4' && second.col1.charAt(i) != '5' &&
                                second.col1.charAt(i) != '6' && second.col1.charAt(i) != '7' &&
                                second.col1.charAt(i) != '8' && second.col1.charAt(i) != '9'
                        ) secondNum = false;
                    }
                    if(firstNum) {
                        if(secondNum) {
                            int firstInt = Integer.parseInt(first.col1);
                            int secondInt = Integer.parseInt(second.col1);
                            if(firstInt > secondInt) resultBool = true;
                            else resultBool = false;
                        }
                        else {
                            resultBool = false;
                        }
                    }
                    else {
                        if(secondNum) {
                            resultBool = true;
                        }
                        else {
                            resultBool = first.col1.compareTo(second.col1) > 0;
                        }
                    }
                    if(Tab2.int_Column1 == 2) resultBool = !resultBool;
                    return resultBool ? 1 : -1;
                }
                else if((first.col2 != second.col2) && ((Tab2.int_Column2 != 0) || (Tab2.int_Column3 !=0 && first.col3 == second.col3))) {
                    boolean resultBool =  first.col2 < second.col2;
                    if(Tab2.int_Column2 == 2) resultBool = !resultBool;
                    return resultBool ? 1 : -1;
                }
                else {
                    boolean resultBool =  first.col3 < second.col3;
                    if(Tab2.int_Column3 == 2) resultBool = !resultBool;
                    return resultBool ? 1 : -1;
                }
            }
        }
        Arrays.sort(S, new ComparSort());
        Tab2.tableManage.removeAllViews();
        for(int i = 0; i < SData.getRunners(); i++) {
            arrRowData[i] = new TableRow(Tab2.getActivity());
            arrRowData[i].setX((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Tab2.getResources().getDisplayMetrics()));
            Tab2.tableManage.addView(arrRowData[i]);
            arrTextData[i] = new TextView(Tab2.getActivity());
            arrTextData[i].setText(S[i].col1);
            arrTextData[i].setPadding(0,0,0,0);
            arrTextData[i].setTextSize(22);
            arrTextData[i].setLayoutParams(new TableRow.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, Tab2.getResources().getDisplayMetrics()),
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            arrTextData[i].setTextColor(Tab2.getResources().getColor(android.R.color.black));
            arrRowData[i].addView(arrTextData[i]);
            if(Tab2.ShownCanvas)
            {
                String lapsTime = "";
                if(S[i].col3 < 1) lapsTime = "-";
                for(int j = 0; j < S[i].col3; j++) {
                    lapsTime += String.valueOf(S[i].col4[j] / 1000);
                    if(j+1 < S[i].col3) lapsTime += ";";
                }
                arrTextLapsTime[i] = new TextView(Tab2.getActivity());
                arrTextLapsTime[i].setText(lapsTime);
                arrTextLapsTime[i].setPadding(toDP(40),0,0,0);
                arrTextLapsTime[i].setTextSize(22);
                arrTextLapsTime[i].setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                HorizontalScrollView hSV = new HorizontalScrollView(Tab2.getActivity());
                arrTextLapsTime[i].setTextColor(Tab2.getResources().getColor(android.R.color.black));
                ConstraintLayout LL = new ConstraintLayout(Tab2.getActivity());
                LL.addView(arrTextLapsTime[i]);
                hSV.addView(LL);
                arrRowData[i].addView(hSV);

                arrTextLapsTime[i].setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });

            }
            else {
                long RunnerTime = S[i].col2;
                long secs = ((int)RunnerTime/1000);
                long mins = secs/60;
                secs %= 60;
                long milliseconds = (int)(RunnerTime%1000);
                arrTimeData[i] = new TextView(Tab2.getActivity());
                arrTimeData[i].setLayoutParams(new TableRow.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105, Tab2.getResources().getDisplayMetrics()),
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                arrTimeData[i].setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                        Tab2.getResources().getDisplayMetrics()),0,0,00);
                if(SData.getAccuracyFlag())
                    arrTimeData[i].setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs);
                else {
                    arrTimeData[i].setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs +"."+
                            ((milliseconds/10) < 10 ? "0" : "") + (milliseconds/10));
                }
                arrTimeData[i].setTextSize(22);
                arrTimeData[i].setTextColor(Tab2.getResources().getColor(android.R.color.black));
                arrRowData[i].addView(arrTimeData[i]);
                //Круги
                arrLapsData[i] = new TextView(Tab2.getActivity());
                arrLapsData[i].setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,
                        Tab2.getResources().getDisplayMetrics()),0,0,0);
                arrLapsData[i].setText(S[i].col3 + "/" + SData.laps);
                arrLapsData[i].setTextSize(22);
                arrLapsData[i].setTextColor(Tab2.getResources().getColor(android.R.color.black));
                arrRowData[i].addView(arrLapsData[i]);
                TableRow.MarginLayoutParams marginParams1 = new TableRow.MarginLayoutParams(arrRowData[i].getLayoutParams());
                marginParams1.setMargins(0, 0, 0, 0);
                TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(marginParams1);
                arrRowData[i].setLayoutParams(layoutParams1);
            }
        }
    }

    //Сохранение настроек
    protected static void FilePrintLicence(int value) {
        try {
            // отрываем поток для записи

            File PrintFile = new File(CW.getFilesDir(), "binary.dat");
            if(!PrintFile.createNewFile()) {

            }

            FileWriter FW = new FileWriter(PrintFile);
            String str = String.valueOf(value) + "\n";                    // Метка 0
            byte[] data = String.valueOf(value).getBytes();
            try {
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                sha.update(data);
                data = sha.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            for(int i = 0 ; i < data.length; i++) str += (String.valueOf(data[i]));
            str += ("\n");
            str += String.valueOf(ActivatedPromo);
            str += ("\n");
            FW.write(str);
            FW.close();

            PrintFile = new File(CW.getObbDir(), "binary.dat");
            FW = new FileWriter(PrintFile);
            FW.write(str);
            FW.close();

            /*
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    CW.openFileOutput("binary.dat", MODE_PRIVATE)));
            // пишем данные
            bw.write(String.valueOf(value) + "\n");
            byte[] data = String.valueOf(value).getBytes();
            try {
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                sha.update(data);
                data = sha.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            //Log.d("CRYCRY", String.valueOf(data));
            for(int i = 0 ; i < data.length; i++) bw.write(String.valueOf(data[i]));
            bw.write("\n");
            bw.close();*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected int FileReadLicence() {
        int value = 0;
        try {

            File ReadFile = new File(CW.getFilesDir(), "binary.dat");
            if(!ReadFile.exists()) {
                ReadFile = new File(CW.getObbDir(), "binary.dat");
            }
            if(ReadFile.exists()) {
                FileReader FR = new FileReader(ReadFile);

                char[] CBUF = new char[256];
                FR.read(CBUF);
                String str = String.valueOf(CBUF).substring(0,String.valueOf(CBUF).indexOf("\n"));
                try {
                    value = Integer.parseInt(str);
                } catch (NumberFormatException Nfe) {
                    return 0;
                }
                if(value == 0) {
                    activateTimes = 0;
                    FilePrintLicence(activateTimes);
                    return  activateTimes;
                }
                byte[] data = String.valueOf(value).getBytes();
                try {
                    MessageDigest sha = MessageDigest.getInstance("SHA-1");
                    sha.update(data);
                    data = sha.digest();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                str = String.valueOf(CBUF).substring(String.valueOf(CBUF).indexOf("\n")+1);
                str = str.substring(0, str.indexOf("\n"));
                boolean ok = true;
                int i1 = 0;
                while (!str.isEmpty()) {
                    String subs = String.valueOf(data[i1]);
                    for (int i = 0; i < subs.length(); i++) {
                        if (str.charAt(i) != subs.charAt(i)) ok = false;
                    }
                    if (ok) str = str.substring(subs.length());
                    i1++;
                }
                if (ok && str.isEmpty() && i1 == data.length)
                    activateTimes = value;
                else activateTimes = 0;
                str = String.valueOf(CBUF).substring(String.valueOf(CBUF).indexOf("\n")+1);
                str = str.substring(String.valueOf(str).indexOf("\n")+1);
                str = str.substring(0, str.indexOf("\n"));
                try {
                    value = Integer.parseInt(str);
                    ActivatedPromo = value;
                } catch (NumberFormatException Nfe) {
                    Nfe.printStackTrace();
                }
                FR.close();
            }
            else {
                if(activateTimes == -2) activateTimes = 50;
                else activateTimes = 0;
                FilePrintLicence(activateTimes);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(activateTimes == -2) activateTimes = 50;
            else activateTimes = 0;
            FilePrintLicence(activateTimes);
        } catch (IOException e) {
            e.printStackTrace();
            if(activateTimes == -2) activateTimes = 50;
            else activateTimes = 0;
            FilePrintLicence(activateTimes);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            if(activateTimes == -2) activateTimes = 15;
            else activateTimes = 0;
            FilePrintLicence(activateTimes);
        }
        return activateTimes;
    }

    protected static void FilePrint(SettingsData SData) {
        try {
            Log.d("CRYCRY", CW.getFilesDir().toString());
            File PrintFile = new File(CW.getObbDir(), "config.cfg");
            if(!PrintFile.createNewFile()) {

            }

            FileWriter FW = new FileWriter(PrintFile);
            String str = ("[versionNumber](licenceTaken)\n");                    // Метка 0
            str += ("Save_ver2\n");                                        // Версия сохранения
            str += ("[runnersNumber]\n");                                  // Метка 1
            str += (String.valueOf(SData.getRunners()) + "\n");            // Количество кнопок
            str += ("[staticFlag]\n");                                     // Метка 2
            str += ((SData.getStaticButtons() ? "true" : "false") + "\n"); // Статические кнопки
            str += ("[lapsNumber]\n");                                     // Метка 3
            str += (String.valueOf(SData.getLaps()) + "\n");               // Количество кругов
            str += ("[hidetimeFLag]\n");                                   // Метка 4
            str += (SData.getAccuracyFlag() + "\n");                       // Точность измерений
            str += ("[hidetimeFLag]\n");                                   // Метка 5
            str += (SData.getTimeUntilShown() + "\n");                     // Количество секунд для исчезания кнопок
            str += ("[slashLapsFlag]\n");                                  // Метка 6
            str += ((SData.getslashLaps() ? "true" : "false") + "\n");     // Отображение кругов через слэш
            str += ("[styleInteger]\n");                                   // Метка 7
            str += (SData.styleID + "\n");                                 // Стиль приложения
            str += ("[onVolumeFlag]\n");                                   // Метка 8
            str += ((SData.OnVolumeStart ? "true" : "false") + "\n");            // Нажатие на громкость включает таймер
            for(int i = 0; i < SData.getRunners(); i++) {
                str += ("[R.ID " + String.valueOf(i) + "]\n");   // Метки для номеров
                str += (String.valueOf(arrIDNumber[i]) + "\n");            // Идентификационный номер участника i
            }


            int lenCBUF = str.length(), lenNum = String.valueOf(lenCBUF).length();
            String lenSTR = (lenNum < 4 ? "0" : "") + (lenNum < 3 ? "0" : "") + (lenNum < 2 ? "0" : "") + lenCBUF;
            FW.write(lenSTR);
            FW.write(str);
            FW.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Загрузка настроек
    protected SettingsData FileRead() {
        SettingsData SData = new SettingsData();
        try {
            File PrintFile = new File(CW.getObbDir(), "config.cfg");
            if(PrintFile.exists()) {
                FileReader FR = new FileReader(PrintFile);
                char[] CBUF = new char[4];
                FR.read(CBUF);
                int lenCBUG;
                try {
                    lenCBUG = Integer.parseInt(String.valueOf(CBUF));
                    Log.d("CRYCRY", CBUF.toString());
                    CBUF = new char[lenCBUG + 512];
                    FR.read(CBUF, 0, lenCBUG);

                    // читаем содержимое
                    String str = "";
                    int stage = 0;
                    String[] BufToStr = String.valueOf(CBUF).split("\n");
                    while (BufToStr.length > stage) {
                        str = BufToStr[stage];
                        Log.d("CRYCRY", str);
                        stage++;
                        switch (stage) {
                            case 1:
                                if(!str.equals("[versionNumber](licenceTaken)")) activateTimes = -2;
                                break;
                            case 2:
                                if(!str.equals("Save_ver2")) return SData;
                                break;
                            case 4:
                                if(Integer.parseInt(str) > 0 && Integer.parseInt(str) < 101) SData.setRunners(Integer.parseInt(str));
                                else return SData;
                                break;
                            case 6:
                                if(str.equals("false")) SData.setStaticButtons(false);
                                else               SData.setStaticButtons(true);
                                break;
                            case 8:
                                if(Integer.parseInt(str) > 0 && Integer.parseInt(str) < 100) SData.setLaps(Integer.parseInt(str));
                                break;
                            case 10:
                                if(str.equals("false")) SData.setAccuracyFlag(false);
                                else               SData.setAccuracyFlag(true);
                                break;
                            case 12:
                                if(Integer.parseInt(str) > 0 && Integer.parseInt(str) < 100) SData.setTimeUntilSHown(Integer.parseInt(str));
                                break;
                            case 14:
                                if(str.equals("false")) SData.setSlashLaps(false);
                                else               SData.setSlashLaps(true);
                                break;
                            case 16:
                                SData.styleID = Integer.parseInt(str);
                                if(SData.styleID != 0 && SData.styleID != 1 && SData.styleID != 2) SData.styleID = 0;
                                break;
                            case 18:
                                if(str.equals("false")) SData.OnVolumeStart = false;
                                else               SData.OnVolumeStart = true;
                                break;
                        }
                        if(stage > 19 && stage % 2 == 0) {
                            arrIDNumber[(stage / 2) - 10] = str;
                        }
                    }
                    if(stage < 1) activateTimes = -2;
                    FR.close();
                    return SData;
                }
                catch (NumberFormatException e) {
                    //e.printStackTrace();
                }
                catch (IOException e) {
                    //e.printStackTrace();
                }
                SData.setRunners(30);
                FilePrint(SData);
                return SData;
            }
            else {
                activateTimes = -2;
                SData.setRunners(30);
                FilePrint(SData);
                return SData;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        activateTimes = -2;
        SData.setRunners(30);
        FilePrint(SData);
        return SData;
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tabbed_stopwatch);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        display = getWindowManager().getDefaultDisplay();
        CW = new ContextWrapper(getBaseContext());
        arrIDNumber = new String[101];
        SData = FileRead();
        activateTimes = 50;//FileReadLicence();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        Tab0 = new pictureFragment();
        Tab3 = new Fragment_Settings.SettingsActivity();
        Tab2 = new Fragment_Database.database();
        Tab1 = new Fragment_Main.MainActivity();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        setTheme(R.style.AppTheme);
    }

    public void onBackPressed() {
        //super.onBackPressed();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(buttonsCreated && !isStarted && SData.OnVolumeStart) {
                    Tab1.buttonStart.callOnClick();
                    if(Tab3 != null)
                    if(Tab3.isVisible()) {
                        Fragment_Settings.SettingsActivity.editLaps.setEnabled(startTime==0);
                        Fragment_Settings.SettingsActivity.Param1Edit.setEnabled(startTime==0);
                        Fragment_Settings.SettingsActivity.Param2Edit.setEnabled(startTime==0);
                    }
                    return true;
                }
                if(SData.OnVolumeStart) return true;
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(SData.OnVolumeStart) return true;
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_stopwatch, menu);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return Tab0;
                case 1:
                    return Tab1;
                case 2:
                    return Tab2;
                default:
                    return Tab3;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }
}
