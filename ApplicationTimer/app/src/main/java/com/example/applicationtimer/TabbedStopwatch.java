package com.example.applicationtimer;

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
import java.io.FileNotFoundException;
import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.lang.reflect.Array;
 import java.net.NetworkInterface;
 import java.nio.channels.InterruptibleChannel;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.List;
 import java.util.Set;
 import java.util.UUID;
 import java.util.logging.LogManager;

 import static java.lang.Math.PI;
 import static java.lang.Math.min;
 import static java.lang.Math.sqrt;


public class TabbedStopwatch extends AppCompatActivity {
    //Данные фрагментов
    static String currentTime;
    static Display display;
    static public class Pair<F, S> {
        public F first;
        public S second;
    }
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
        boolean getHidden() {return isHidden;}
        int getLapsTaken(){ return LapsTaken; }
        String getIDNumber(){ return IDNumber; }
        int[] getarrMinutes(){ return arrMinutes; }
        int[] getarrSeconds(){ return arrSeconds; }
        int[] getArrMilisnd(){ return arrMilisnd; }
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
                timeHidden = 0; isHidden = false;
            }
            return isHidden;
        }
    }

    //Класс настроек
    protected static class SettingsData {
        public boolean formMain = true;
        public boolean smallView = false;
        protected int Dialog_Param1 = 400;
        protected int Dialog_Param2 = 3000;
        protected int Dialog_Param3 = 20;
        protected int mapType = 1; // 1 - ellipse
        protected boolean notification = true;
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

    }

    static int toDP(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Tab2.getResources().getDisplayMetrics());
    }

    static public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
        // Variables to input
        public long default_time;
        public long CurrentLapTime[];
        private DrawThread drawThread;
        Pair<Double, Double> arrCoordinates[];

        //TODO ---------------------------------------------- Horizontal view -------------------------------------------------------
        // public int p1 = 10, p2 = 15, p3 = 316, p4, accur = 400;
        public int p1 = 16, p2 = 15, p3 = 310, p4, accur = 400;

        RectF rectf1, rectf2, rectf3;
        int _A = (p3 - p1)/2, _B;
        int zeroX = _A + p1, zeroY;
        int x1 = p1;
        boolean upper = true;
        public DrawView(Context context, int _p4) {
            super(context);
            default_time = SData.timeUntilSHown * 1000;//(long)((double)(SData.Dialog_Param1) * 3600 / SData.Dialog_Param3);
            p4 = _p4 - 5;
            _B = (p4-p2)/2;
            zeroY = _B + p2;
            rectf1 = new RectF(toDP(p1),toDP(p2),toDP(p1+2*_B),toDP(p4));
            rectf2 = new RectF(toDP(p1+_B),toDP(p2),toDP(p3-_B),toDP(p4));
            rectf3 = new RectF(toDP(p3-2*_B),toDP(p2),toDP(p3),toDP(p4));
            findCoordinates(p3-p1, p4-p2, accur);
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

        public void findCoordinates(int w, int h, int acc)
        {
            double value_distance = 2*PI*sqrt((double)(w*w)/8+(double)(h*h)/8);
            double per_distance = value_distance*1.0085 / (acc);
            double step = Math.min(per_distance / 50, 0.02);
            arrCoordinates = new Pair[acc+1];
            arrCoordinates[0] = new Pair<>();
            arrCoordinates[0].first = (double)-_A;// x coordinate
            arrCoordinates[0].second = 0.0;// y coordinate
            double y_pot;
            for(int i = 1; i < acc; i++)
            {
                arrCoordinates[i] = new Pair<>();
                double x_predict = arrCoordinates[i-1].first;
                double q = per_distance, q_prev;
                do
                {
                    q_prev = q;
                    if(i >= acc/2) {
                        upper = false;
                        x_predict-=step;
                    }
                    else x_predict+=step;
                    if(x_predict > _A) x_predict = arrCoordinates[i-1].first;
                    if(x_predict < -_A) x_predict = arrCoordinates[i-1].first;
                    y_pot = findY(x_predict);
                    q = Math.abs(Math.sqrt(Math.pow(x_predict - arrCoordinates[i-1].first, 2) +
                            Math.pow(y_pot - arrCoordinates[i-1].second, 2)) - per_distance);
                }while(q_prev > q);
                arrCoordinates[i].first = x_predict;
                arrCoordinates[i].second = y_pot;
            }

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
            boolean retry = true;
            drawThread.setRunning(false);
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
                while (running) {
                    canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        if (canvas == null)
                            continue;
                        Paint p;
                        p = new Paint();
                        // Фон
                        canvas.drawColor(getResources().getColor(R.color.brickYellow));
                        // Круг
                        p.setColor(getResources().getColor(R.color.grassGreen));

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
                                    canvas.drawPoint(toDP(arrCoordinates[x1].first.intValue() + zeroX),
                                            toDP(zeroY + arrCoordinates[x1].second.intValue()), p);
                                    p.setTextSize(8 + 22*x1/accur);
                                    canvas.drawText(Tab1.runData[i].IDNumber, toDP(arrCoordinates[x1].first.intValue() + zeroX),
                                            toDP(zeroY + arrCoordinates[x1].second.intValue()-10),p);
                                }
                            }
                        }
                        else
                        {
                            canvas.drawPoint(toDP(arrCoordinates[0].first.intValue() + zeroX),
                                    toDP(zeroY + arrCoordinates[0].second.intValue()), p);
                        }

                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }
    }




    //--------------------------------PictureFragment------------------------------------------------------\\
    public static class pictureFragment extends Fragment {
        public View rootView;
        public DrawView CanvasView;
        protected ConstraintLayout CanvasL;
        protected ImageButton ManagePic, ImButFull;

        @Override

        public void onResume() {
            super.onResume();
            if(SData.smallView) {
                CanvasL.setMaxHeight(toDP(170));
                CanvasView = new DrawView(rootView.getContext(), 160);
                CanvasL.addView(CanvasView);
                ImButFull.getBackground().setColorFilter(Color.rgb(0x90, 0x31, 0x15), PorterDuff.Mode.MULTIPLY);
            }
            else {
                CanvasL.setMaxHeight(toDP(670));
                CanvasView = new DrawView(rootView.getContext(), 660);
                CanvasL.addView(CanvasView);
                ImButFull.getBackground().clearColorFilter();
            }
        }

        public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            rootView = inflater.inflate(R.layout.activity_picture, container, false);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            CanvasL = (ConstraintLayout)rootView .findViewById(R.id.CanvasLayout);
            ManagePic = (ImageButton) rootView.findViewById(R.id.managePicture);
            ImButFull = (ImageButton) rootView.findViewById(R.id.imageButFull);

            CanvasView = new DrawView(rootView.getContext(), 660);
            CanvasL.addView(CanvasView);

            ImButFull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SData.smallView = !SData.smallView;
                    if(SData.smallView) {
                        CanvasL.setMaxHeight(toDP(170));
                        CanvasView = new DrawView(rootView.getContext(), 160);
                        CanvasL.addView(CanvasView);
                        ImButFull.getBackground().setColorFilter(Color.rgb(0x90, 0x31, 0x15), PorterDuff.Mode.MULTIPLY);
                    }
                    else {
                        CanvasL.setMaxHeight(toDP(670));
                        CanvasView = new DrawView(rootView.getContext(), 660);
                        CanvasL.addView(CanvasView);
                        ImButFull.getBackground().clearColorFilter();
                    }

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
    protected static void FilePrint(SettingsData SData) {
        try {
            // отрываем поток для записи

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    CW.openFileOutput("config.cfg", MODE_PRIVATE)));
            // пишем данные
            bw.write("[versionNumber]\n");                                  // Метка 0
            bw.write("Save_ver2\n");                                        // Версия сохранения
            bw.write("[runnersNumber]\n");                                  // Метка 1
            bw.write(String.valueOf(SData.getRunners()) + "\n");            // Количество кнопок
            bw.write("[staticFlag]\n");                                     // Метка 2
            bw.write((SData.getStaticButtons() ? "true" : "false") + "\n"); // Статические кнопки
            bw.write("[lapsNumber]\n");                                     // Метка 3
            bw.write(String.valueOf(SData.getLaps()) + "\n");               // Количество кругов
            bw.write("[hidetimeFLag]\n");                                   // Метка 4
            bw.write(SData.getAccuracyFlag() + "\n");                       // Точность измерений
            bw.write("[hidetimeFLag]\n");                                   // Метка 5
            bw.write(SData.getTimeUntilShown() + "\n");                     // Количество секунд для исчезания кнопок
            bw.write("[slashLapsFlag]\n");                                  // Метка 6
            bw.write((SData.getslashLaps() ? "true" : "false") + "\n");     // Отображение кругов через слэш
            bw.write("[styleInteger]\n");                                   // Метка 7
            bw.write(SData.styleID + "\n");                                 // Стиль приложения
            bw.write("[onVolumeFlag]\n");                                   // Метка 8
            bw.write((SData.OnVolumeStart ? "true" : "false") + "\n");            // Нажатие на громкость включает таймер
            for(int i = 0; i < SData.getRunners(); i++) {
                bw.write("[Runner ID number " + String.valueOf(i) + "]\n");   // Метки для номеров
                bw.write(String.valueOf(arrIDNumber[i]) + "\n");            // Идентификационный номер участника i
            }
            // закрываем поток
            bw.close();
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

            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput("config.cfg")));
            String str = "";
            // читаем содержимое
            int stage = 0;
            while ((str = br.readLine()) != null) {
                Log.d("State", str);
                stage++;
                switch (stage) {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            SData.setRunners(30);
            FilePrint(SData);
            return SData;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        Tab3.rootView.findViewById(R.id.edit_RunnersNames).setEnabled(startTime==0);
                        Tab3.rootView.findViewById(R.id.button_Accept).setEnabled(startTime==0);
                        Tab3.rootView.findViewById(R.id.buttonAdd).setEnabled(startTime==0);
                        Tab3.rootView.findViewById(R.id.button_DeleteText).setEnabled(startTime==0);
                        Tab3.rootView.findViewById(R.id.edit_laps).setEnabled(startTime==0);
                        Tab3.rootView.findViewById(R.id.Button_speech).setEnabled(startTime==0);
                        Tab3.rootView.findViewById(R.id.buttonAdd2).setEnabled(startTime==0);
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
