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
        private F first;
        private S second;
    }
    static protected Pair<Integer, Long> arrRestore[];
    static protected float minSize = 80;
    static protected TableRow arrRowData[];
    static protected ContextWrapper CW ;
    static protected TextView arrTextLapsTime[], arrTextData[], arrTimeData[], arrLapsData[];
    static protected SettingsActivity Tab3;
    static protected database Tab2;
    static protected MainActivity Tab1;
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

    //-------------------------------------------------MAIN FRAGMENT------------------------------------------------\\

    public static class MainActivity extends Fragment {
        public static final int VOICE_RECOGNITION_REQUEST_CODE = 4300;
        protected float sp1, sp2;
        protected LayoutInflater LI;
        public    View rootView;
        protected Button buttonStart, buttonReset, buttonEachLap;
        protected TextView textTimer, textSpoken, textUpc[] = new TextView[101];
        protected TableLayout tableLayout;
        protected Button arrButtons[][];
        protected String arrIDNumberSettings[], UpcomText[] = new String[101];
        protected TableRow arrRows[];
        protected LinearLayout LinearUp;
        protected ImageButton MainDelete;
        protected int OverallNumber, DefUpcoming[] = new int[101];

        RunnerData runData[] = new RunnerData[101];

        //При обновлении секундомера
        Runnable updateTimeThread = new Runnable() {
            @Override
            public void run() {
                timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
                updateTime = timeSwapBuffer+timeInMilliseconds;
                secs = ((int)updateTime/1000);
                mins = secs/60;
                secs %= 60;
                milliseconds = (int)(updateTime%1000);
                if(SData.getAccuracyFlag())
                    textTimer.setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs);
                else {
                    textTimer.setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs +"."+
                            ((milliseconds/10) < 10 ? "0" : "") + (milliseconds/10) );
                }
                //Проход по всем кнопкам
                if(isStarted)
                    for(int i = 0; i < SData.getRunners(); i++){
                        //Проверка для появления кнопки
                        if(!runData[i].isHidden) continue;
                        if(runData[i].afterWaitLeft <=(SData.afterTime*1000)) {
                        }
                        else {
                            arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].setVisibility(View.INVISIBLE);
                            //arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setEnabled(true);
                        }
                        if(arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].getVisibility() == View.INVISIBLE ||
                                runData[i].afterWaitLeft <= (SData.afterTime*1000)) {
                            if (runData[i].passSecond() == false) {
                                if (SData.getLaps() - runData[i].getLapsTaken() == 2) {
                                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].getBackground().setColorFilter(Color.rgb(0xFB, 0xA0, 0x15), PorterDuff.Mode.MULTIPLY);

                                } else if (SData.getLaps() - runData[i].getLapsTaken() == 1) {
                                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].getBackground().setColorFilter(Color.rgb(0xFB, 0x10, 0x15), PorterDuff.Mode.MULTIPLY);
                                } else if (SData.getLaps() - runData[i].getLapsTaken() == 0) {
                                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].getBackground().setColorFilter(Color.rgb(0xFF, 0xFF, 0xFF), PorterDuff.Mode.MULTIPLY);
                                }

                                if (runData[i].isHidden == false) {
                                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setVisibility(View.VISIBLE);
                                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setEnabled(true);
                                }
                            }
                        }
                    }
                customHandler.postDelayed(this, 0);
            }
        };
        Handler customHandler = new Handler();

        public void updateStyle() {
            GradientDrawable gD;
            if(SData.styleID == 1)
                gD = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[] {
                    getResources().getColor(R.color.yellowish_Brown), getResources().getColor(R.color.Brown)});
            else if(SData.styleID == 2)
                gD = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[] {
                    getResources().getColor(R.color.Whitey_pink), getResources().getColor(R.color.Pink)});
            else {
                Tab1.rootView.findViewById(R.id.activity1Layout).setBackgroundColor(getResources().getColor(R.color.design_default_colorE));
                if(Tab3.isResumed()) Tab3.rootView.findViewById(R.id.activity3layout).setBackgroundColor(getResources().getColor(R.color.design_default_colorE));
                if(Tab2.isResumed()) Tab2.rootView.findViewById(R.id.activity2layout).setBackgroundColor(getResources().getColor(R.color.design_default_colorE));
                return;
            }
            Tab1.rootView.findViewById(R.id.activity1Layout).setBackgroundDrawable(gD);
            if(Tab2.isResumed()) Tab2.rootView.findViewById(R.id.activity2layout).setBackgroundDrawable(gD);
            if(Tab3.isResumed()) Tab3.rootView.findViewById(R.id.activity3layout).setBackgroundDrawable(gD);

        }


        public void onPause() {
            super.onPause();
            currentTime = textTimer.getText().toString();
        }


        @Override
        public void onResume() {
            super.onResume();
            updateStyle();
            LinearUp.setVisibility(SData.formMain ? View.VISIBLE : View.INVISIBLE);
            MainDelete.setVisibility(View.INVISIBLE);
            isCheck = false;
            isTouch = false;
            TouchedNumbers = new boolean[101];
            if(BuiltOnce) {
                SData.Dialog_Param1 = Integer.parseInt(Tab3.Param1Edit.getText().toString());
                SData.Dialog_Param2 = Integer.parseInt(Tab3.Param2Edit.getText().toString());
                if(Tab3.radioGroup1.getCheckedRadioButtonId() != (SData.getAccuracyFlag() ? R.id.radio_1 : R.id.radio_2)) {
                    if(Tab3.radioGroup1.getCheckedRadioButtonId() == R.id.radio_1) {
                        currentTime = currentTime.substring(0, 5);
                    }
                    else {
                        timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
                        updateTime = timeSwapBuffer+timeInMilliseconds;
                        secs = ((int)updateTime/1000);
                        mins = secs/60;
                        secs %= 60;
                        milliseconds = (int)(updateTime%1000);
                        currentTime += "."+((milliseconds/10) < 10 ? "0" : "") + (milliseconds/10);
                    }
                }

                textTimer.setText(currentTime);
                SData.setAccuracyFlag(Tab3.radioGroup1.getCheckedRadioButtonId() == R.id.radio_1 ? true : false);
                showTable();

            }
            if(isStarted) buttonReset.setText(getResources().getString(R.string._cancel_str));
            else {
                if(dataExist || isStarted || updateTime>0) buttonReset.setText(getResources().getString(R.string.Reset_str));
                else buttonReset.setText(getResources().getString(R.string._str_Check));
            }
            setChronology();
            //Обновление кнопок
            deleteButtons();
            createButtons();
            FilePrint(SData);
        }

        public void UpcomingSet(int value) {
            int prev = DefUpcoming[value];
            for(int i = 0; i < SData.getRunners(); i++) {
                if(i == value) {
                    DefUpcoming[i] = 0;
                }
                else if(DefUpcoming[i] < prev) {
                    DefUpcoming[i]++;
                }
                textUpc[i].setText(UpcomText[DefUpcoming[i]]);
            }
            Log.d("WHAT", "THAT");
        }

        public void UpcomingReset() {
            for(int i = 0; i < SData.getRunners(); i++) {
                UpcomText[i] = runData[i].IDNumber;
                DefUpcoming[i] = i;
            }
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LI = inflater;
            rootView = inflater.inflate(R.layout.activity_main, container, false);
            //Сохранение элементов окна
            buttonStart = (Button) rootView.findViewById(R.id.Button_Start);
            buttonReset = (Button) rootView.findViewById(R.id.button_Reset);
            textTimer = (TextView)rootView.findViewById(R.id.text_Timer);
            tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
            MainDelete = (ImageButton) rootView.findViewById(R.id.deleteMain);
            LinearUp = (LinearLayout) rootView.findViewById(R.id.LinearUpcoming);
            //Предустановка переменных
            if(!BuiltOnce) {

                arrRestore = new Pair[5];
                for(int i = 0; i < 5; i++){
                    arrRestore[i] = new Pair();
                }
                toUpdate = false;
                nowRestoring = 0;
                arrTiming = new long[101];
                textTimer.setText(SData.getAccuracyFlag() ? "00:00" : "00:00.00");
                timeSwapBuffer=0L;
                dataExist = false;
                isStarted = false;
                int toName = 1;
                boolean hasName = false;
                for (int i = 0; i < SData.getRunners(); i++) {
                    if(arrIDNumber[i] == null) {
                        do {
                            hasName = false;
                            for (int j = 0; j < SData.getRunners() && hasName == false; j++) {
                                if(arrIDNumber[j] != null)
                                    if (arrIDNumber[j].equals(String.valueOf(toName))) hasName = true;
                            }
                            if(hasName) toName++;
                        } while(hasName);
                        arrIDNumber[i] = String.valueOf(toName);
                        toName++;
                    }
                }
                for (int i = 0; i < SData.getRunners(); i++)
                    runData[i] = new RunnerData();
                ResetFunc();
            }

            if(isStarted) {
                buttonStart.getBackground().setColorFilter(Color.rgb(0xE5, 0x21, 0x3F), PorterDuff.Mode.MULTIPLY);
                buttonStart.setText(R.string._stop_str);
            }
            else {
                buttonStart.getBackground().setColorFilter(Color.rgb(0x3F, 0x51, 0xB5), PorterDuff.Mode.MULTIPLY);
                buttonStart.setText(R.string._start_str);
            }

            //Загрузка настроек
            setChronology();
            //Создание кнопок
            createButtons();
            //*************************Слоты для кнопок*****************
            View.OnClickListener Signal_Delete = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainDelete.setVisibility(View.INVISIBLE);
                    textTimer.setAlpha(1);
                    int newNum = 0, cur = 0;
                    for(int i = 0; i < SData.getRunners(); i++) {
                        if(TouchedNumbers[i] == false) newNum++;
                    }
                    if(newNum == 0) {
                        newNum = 1;
                        TouchedNumbers[0] = false;
                    }

                    String temp[] = new String[newNum];
                    for(int i = 0; i < SData.getRunners(); i++) {
                        if(TouchedNumbers[i] == false) {
                            temp[cur] = arrIDNumber[i];
                            cur++;
                        }
                    }
                    arrIDNumber = temp;
                    SData.setRunners(newNum);
                    class ComparString implements Comparator<String> {
                        public int compare(String first, String second) {
                            boolean resultBool;
                            boolean firstNum = true, secondNum = true;
                            for(int i = 0; i < first.length(); i++) {
                                if(first.charAt(i) != '0' && first.charAt(i) != '1' &&
                                        first.charAt(i) != '2' && first.charAt(i) != '3' &&
                                        first.charAt(i) != '4' && first.charAt(i) != '5' &&
                                        first.charAt(i) != '6' && first.charAt(i) != '7' &&
                                        first.charAt(i) != '8' && first.charAt(i) != '9'
                                ) firstNum = false;
                            }
                            for(int i = 0; i < second.length(); i++) {
                                if(second.charAt(i) != '0' && second.charAt(i) != '1' &&
                                        second.charAt(i) != '2' && second.charAt(i) != '3' &&
                                        second.charAt(i) != '4' && second.charAt(i) != '5' &&
                                        second.charAt(i) != '6' && second.charAt(i) != '7' &&
                                        second.charAt(i) != '8' && second.charAt(i) != '9'
                                ) secondNum = false;
                            }
                            if(firstNum) {
                                if(secondNum) {
                                    int firstInt = Integer.parseInt(first);
                                    int secondInt = Integer.parseInt(second);
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
                                    resultBool = first.compareTo(second) > 0;
                                }
                            }
                            return resultBool ? 1 : -1;
                        }
                    }
                    Arrays.sort(arrIDNumber, new ComparString());
                    FilePrint(SData);
                    isTouch = false;
                    if(newNum == 1) {
                        isCheck = false;
                    }
                    setChronology();
                    deleteButtons();
                    createButtons();
                    showTable();
                    toUpdate = true;
                    TouchedNumbers = new boolean[101];
                }
            };
            //Кнопка Старт
            View.OnClickListener Signal_Start = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isCheck = false;
                    if(isStarted) {
                        //Когда секундомер останавливаем
                        Tab0.rootView.findViewById(R.id.managePicture).setEnabled(true);
                        buttonReset.setText(getResources().getString(R.string.Reset_str));
                        buttonStart.setText(R.string._start_str);
                        //Закрыть уведомление
                        notificationManager.cancel(102);
                        //----------------
                        buttonStart.getBackground().setColorFilter(Color.rgb(0x3F, 0x51, 0xB5), PorterDuff.Mode.MULTIPLY);
                        timeSwapBuffer += timeInMilliseconds;
                        customHandler.removeCallbacks(updateTimeThread);
                    }
                    else {
                        //Когда стартует секундомер
                        if (dataExist || updateTime == 0) {
                            ResetFunc();
                        }
                        Tab0.rootView.findViewById(R.id.managePicture).setEnabled(false);
                        buttonReset.setText(getResources().getString(R.string._cancel_str));
                        Tab0.ManagePic.setEnabled(false);

                        buttonStart.getBackground().setColorFilter(Color.rgb(0xE5, 0x21, 0x3F), PorterDuff.Mode.MULTIPLY);
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimeThread, 0);
                        buttonStart.setText(R.string._stop_str);

                        String s = getResources().getString(R.string._eachLap);
                        SpannableString sStr = new SpannableString(s);
                        sStr.setSpan(new AbsoluteSizeSpan((int)sp2, true), 0,
                                        getString(R.string._eachLap).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        buttonEachLap.setText(sStr);
                    }


                    isStarted = !isStarted;
                }
            };

            //Кнопка Сброса
            View.OnClickListener Signal_Reset = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isStarted)
                    {
                        if(dataExist || isStarted || updateTime>0) {
                            ResetFunc();
                        }
                        else {
                            TouchedNumbers = new boolean[101];
                            isCheck = !isCheck;
                            isTouch = false;
                            MainDelete.setVisibility(View.INVISIBLE);
                            MainDelete.setClickable(false);
                            textTimer.setAlpha(1);
                            if(isCheck) ResetFunc();
                            if(isCheck) buttonReset.setText(getResources().getString(R.string._str_CheckEnd));
                            else buttonReset.setText(getResources().getString(R.string._str_Check));
                            for(int i = 0; i < SData.getFieldY(); i++) {
                                for(int j = 0; j < SData.getFieldX(); j++) {
                                    if((i + 1 == SData.getFieldY()) && (SData.runnerNum < i*(SData.getFieldX()) + j+1)) break;
                                    if(!isCheck) arrButtons[i][j].getBackground().clearColorFilter();
                                    arrButtons[i][j].setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        String s = getResources().getString(R.string._inputNum);
                        SpannableString sStr = new SpannableString(s);
                        sStr.setSpan(new AbsoluteSizeSpan((int)sp1, true), 0,
                                getString(R.string._inputNum).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        buttonEachLap.setText(sStr);
                        Tab0.ManagePic.setEnabled(true);
                    }
                    else {
                        if(nowRestoring > 0 && isStarted) {
                            nowRestoring--;
                            arrTiming[arrRestore[nowRestoring].first] = arrRestore[nowRestoring].second;
                            runData[arrRestore[nowRestoring].first].LapsTaken--;
                            runData[arrRestore[nowRestoring].first].isHidden = false;
                            arrButtons[arrRestore[nowRestoring].first / (SData.getFieldX())][arrRestore[nowRestoring].first
                                    % (SData.getFieldX())].setVisibility(View.VISIBLE);
                            if(runData[arrRestore[nowRestoring].first].LapsTaken < 0) runData[arrRestore[nowRestoring].first].LapsTaken = 0;
                            deleteButtons();
                            createButtons();
                        }
                    }
                }
            };

            buttonStart.setOnClickListener(Signal_Start);
            buttonReset.setOnClickListener(Signal_Reset);
            MainDelete.setOnClickListener(Signal_Delete);
            //**********************************************
            return rootView;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data){

            if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
                //-texterr.setText("");
                ok_Speech = true;
                ArrayList commandList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String SpeechSaid = commandList.get(0).toString();
                SpeechSaid = SpeechSaid.replace(" ", ",");
                SpeechSaid = SpeechSaid.replace(getResources().getString(R.string._replaceAnd), ",");
                SpeechSaid = SpeechSaid.replace(getResources().getString(R.string._replaceNumber), ",");
                while(SpeechSaid.contains(",,")){
                    SpeechSaid = SpeechSaid.replace(",,", ",");
                }
                if(SpeechSaid.startsWith(",")) SpeechSaid = SpeechSaid.substring(1);
                if(SpeechSaid.endsWith(",")) SpeechSaid = SpeechSaid.substring(0, SpeechSaid.length() - 1);
                String SpeechTemp = SpeechSaid;
                while(SpeechTemp.indexOf(',') != -1) {
                    int ComaIndex = SpeechTemp.indexOf(',');
                    if (SpeechTemp.substring(0, ComaIndex).length() > 9) {
                        ok_Speech = false;
                        break;
                    }
                    SpeechTemp = SpeechTemp.substring(SpeechTemp.indexOf(','), SpeechTemp.length()-1);
                }
                if(SpeechTemp.length() > 9) ok_Speech = false;
                //-if(!ok_Speech) texterr.setText(getResources().getString(R.string._err_Length));
                textSpoken.setText(SpeechSaid);
            }
            super.onActivityResult(requestCode, resultCode, data);

        }

        boolean getNames(final EditText editRunnersNames) {
            int result = 0;
            String str = editRunnersNames.getText().toString();
            OverallNumber = 1;
            String acceptable = "0123456789,";
            if(str.length() == 0) result = 1; //Ошибка: пусто
            str = str.replace(' ', ',');
            for(int i = 0; i < str.length(); i++) {
                if(str.length() > i+1)
                    if(str.charAt(i) == ',' && str.charAt(i+1) != ',') OverallNumber++;
            }
            OverallNumber -= (str.startsWith(",")) ? 1 : 0;
            OverallNumber = OverallNumber > 100 ? 100 : OverallNumber;
            String tempArr[] = new String[OverallNumber];
            String got = "";
            int count1 = 0, len = 0;
            boolean fineTemp = false;
            for(int i = 0; i < str.length() && count1 < OverallNumber; i++) {
                //if(str.charAt(i) == ',' && len == 0) result = 3;// Ошибка: ожидалось число, получен иной символ
                if(str.charAt(i) == ',') {
                    if(got != null && got != "" && fineTemp) {
                        tempArr[count1] = got;
                        count1++;
                    }
                    fineTemp = false;
                    got = "";
                    len = 0;
                }
                else {
                    len++;
                    if(str.charAt(i) != ' ') fineTemp = true;
                    /*
                    else {
                        if(len == 1) continue;
                        if(i > 0)
                            if(str.charAt(i - 1) == ' ') continue;
                    }
                    */
                    got += str.charAt(i);
                }
                if(len > 9) result = 4;// Ошибка: слишком длинный номер участника (>=10^10)
            }
            if(got != null && got != "" && fineTemp && count1 < OverallNumber)
                tempArr[count1] = got;
            else count1--;
            count1 = OverallNumber;
            //result = (str.charAt(str.length() - 1) == ',' ? 3 : result);
            arrIDNumberSettings = tempArr;

            //if(OverallNumber < Runners) for(int i = OverallNumber; i < Runners; i++) arrIDNumber[i] = i+1;
            if(result == 0) {
                class ComparString implements Comparator<String> {
                    public int compare(String first, String second) {
                        boolean resultBool;
                        boolean firstNum = true, secondNum = true;
                        for(int i = 0; i < first.length(); i++) {
                            if(first.charAt(i) != '0' && first.charAt(i) != '1' &&
                                    first.charAt(i) != '2' && first.charAt(i) != '3' &&
                                    first.charAt(i) != '4' && first.charAt(i) != '5' &&
                                    first.charAt(i) != '6' && first.charAt(i) != '7' &&
                                    first.charAt(i) != '8' && first.charAt(i) != '9'
                            ) firstNum = false;
                        }
                        for(int i = 0; i < second.length(); i++) {
                            if(second.charAt(i) != '0' && second.charAt(i) != '1' &&
                                    second.charAt(i) != '2' && second.charAt(i) != '3' &&
                                    second.charAt(i) != '4' && second.charAt(i) != '5' &&
                                    second.charAt(i) != '6' && second.charAt(i) != '7' &&
                                    second.charAt(i) != '8' && second.charAt(i) != '9'
                            ) secondNum = false;
                        }
                        if(firstNum) {
                            if(secondNum) {
                                int firstInt = Integer.parseInt(first);
                                int secondInt = Integer.parseInt(second);
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
                                resultBool = first.compareTo(second) > 0;
                            }
                        }
                        return resultBool ? 1 : -1;
                    }
                }
                Arrays.sort(arrIDNumberSettings, new ComparString());
                for(int i = 1; i < OverallNumber; i++) {
                    if(arrIDNumberSettings[i].equals(arrIDNumberSettings[i - 1])) {
                        arrIDNumberSettings[i - 1] = "0";
                    }
                }
                Arrays.sort(arrIDNumberSettings, new ComparString());
                int tempNumber = OverallNumber;
                String rewriteText = "";
                result = 0;
                for(int i = 0; i < tempNumber; i++) {
                    if(arrIDNumberSettings[i].equals("0")) {
                        OverallNumber--;
                        continue;
                    }
                    if(result != 0) rewriteText += ",";
                    result++;
                    rewriteText += arrIDNumberSettings[i];
                }
                if(result == 0) {
                    //-texterr.setText(getResources().getString(R.string._err_Empty));
                    return false;
                }
                result = 0;
                if(tempNumber - OverallNumber != 0) {
                    tempArr = new String[OverallNumber];
                    for(int i = 0; i < tempNumber; i++) {
                        if(!arrIDNumberSettings[i].equals("0")) {
                            tempArr[result] = arrIDNumberSettings[i];
                            result++;
                        }
                    }
                    arrIDNumberSettings = tempArr;
                }
                //-editRunners.setText(String.valueOf(OverallNumber));
                editRunnersNames.setText(rewriteText);
                //-texterr.setText("");
                return true;
            }
            else {

                //-if(result == 1) texterr.setText(getResources().getString(R.string._err_Empty));
                //else if(result == 2) texterr.setText("Принимаются без пробелов только номера, разделенные запятыми");
                //else if(result == 3) texterr.setText("Вместо числа был обнаружен иной символ");
                //-if(result == 4) texterr.setText(getResources().getString(R.string._err_Length));
                //-else                 texterr.setText(getResources().getString(R.string._err_Default));

                return false;
            }
        }

        View.OnClickListener Signal_EachLap = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateTime != 0 || isStarted) {
                    for(int i = 0; i < SData.getRunners(); i++) {
                        arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].callOnClick();
                    }
                }
                else {
                    final AlertDialog.Builder mbuilder = new AlertDialog.Builder(getActivity());
                    LayoutInflater minflater = getActivity().getLayoutInflater();
                    mbuilder.setView(LI.inflate(R.layout.activity_settingsrunners, null));
                    AlertDialog mAD =  mbuilder.create();
                    mAD.show();
                    //first param
                    final EditText RunParam1 = mAD.findViewById(R.id.dialog1);
                    final ImageButton RunButton1 = mAD.findViewById(R.id.buttonAcceptDialog);
                    final ImageButton RunButton2 = mAD.findViewById(R.id.buttonCancelDialog);
                    final ImageButton RunButton3 = mAD.findViewById(R.id.buttonAddDialog);
                    final ImageButton RunButton4 = mAD.findViewById(R.id.buttonSpeechInput);
                    final ImageButton RunButton5 = mAD.findViewById(R.id.buttonAdd2Dialog);
                    final TextView RunNumberText = mAD.findViewById(R.id.RunNumberText);
                    final TextView RunNumberParam = mAD.findViewById(R.id.SpeechTextParam);
                    textSpoken = RunNumberParam;
                    RunNumberText.setText(String.valueOf(SData.getRunners()));
                    String rewriteText = "";
                    for(int i = 0; i < SData.getRunners(); i++) {
                        rewriteText += runData[i].IDNumber;
                        if(i + 1 != SData.getRunners()) rewriteText += ",";
                    }
                    RunParam1.setText(rewriteText);

                    RunButton1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getNames(RunParam1)) {
                                int Runners = OverallNumber;//Integer.parseInt(RunParam1.getText().toString());
                                if (Runners != 0) {
                                    for (int i = Runners; i < SData.getRunners(); i++) {
                                        arrTiming[i] = 0;
                                    }
                                    SData.setRunners(Runners);
                                    RunNumberText.setText(String.valueOf(OverallNumber));
                                    arrIDNumber = arrIDNumberSettings;
                                }
                            }
                            setChronology();
                            deleteButtons();
                            createButtons();
                            FilePrint(SData);
                        }
                    });

                    RunButton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RunParam1.setText("");
                        }
                    });

                    RunButton3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RunButton1.callOnClick();
                            //-texterr.setText("");
                            String[] arrIDTemp = new String[101];
                            int toName = 1;
                            if(!RunParam1.getText().toString().isEmpty()) {
                                try
                                {
                                    for(int i = 0; i < SData.getRunners(); i++) {
                                        int probToName = Integer.parseInt(runData[i].IDNumber);
                                        if(toName < probToName)
                                            toName = probToName;
                                    }
                                }
                                catch (Exception e) {}
                            }


                            if(RunParam1.getText().length() == 0)
                            {
                                OverallNumber = 1;
                                arrIDTemp[0] = "1";

                                SData.setRunners(OverallNumber);
                            }
                            else
                            {
                                if(OverallNumber == 100) return;
                                OverallNumber++;
                                SData.setRunners(OverallNumber);
                                for(int i = 0; i < OverallNumber-1; i++) arrIDTemp[i] = arrIDNumber[i];

                                boolean hasName = false;
                                do {
                                    hasName = false;
                                    for (int j = 0; j < SData.getRunners() && hasName == false; j++) {
                                        if(j < OverallNumber-1)
                                            if (arrIDNumber[j].equals(String.valueOf(toName))) hasName = true;
                                    }
                                    if(hasName) toName++;
                                } while(hasName);
                            }

                            arrIDTemp[OverallNumber-1] = String.valueOf(toName);
                            arrIDNumber = arrIDNumberSettings = arrIDTemp;
                            String rewriteText = "";
                            for(int i = 0; i < OverallNumber; i++) {
                                rewriteText += arrIDNumberSettings[i];
                                if(i + 1 != OverallNumber) rewriteText += ",";
                            }
                            RunParam1.setText(rewriteText);
                            getNames(RunParam1);
                            RunParam1.append("");
                            RunButton1.callOnClick();
                            RunParam1.setSelection(RunParam1.length());
                            //-texterr.setText("");
                        }
                    });

                    RunButton4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ok_Speech = false;
                            startSpeak();
                        }
                    });

                    RunButton5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(ok_Speech) {
                                RunParam1.append("," + textSpoken.getText());
                                RunButton1.callOnClick();
                            }
                        }
                    });

                    mAD.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            /*
                            SData.Dialog_Param1 = Integer.parseInt(eText1.getText().toString());
                            SData.Dialog_Param2 = Integer.parseInt(eText2.getText().toString());
                            SData.Dialog_Param3 = Integer.parseInt(eText3.getText().toString());

                            CanvasView.default_time = (long)((double)(SData.Dialog_Param1) * 3.6 / SData.Dialog_Param3) * 1000;
                            */
                        }
                    });
                    /*
                    eText1.setText(String.valueOf(SData.Dialog_Param1));
                    eText2.setText(String.valueOf(SData.Dialog_Param2));
                    eText3.setText(String.valueOf(SData.Dialog_Param3));
                    */
                }
            }
        };

        public void startSpeak() {
            Intent intent =  new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // Intent для вызова формы обработки речи (ОР)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); // сюда он слушает и запоминает
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string._SpeechAsk_str));
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE); // вызываем активность ОР

        }

        void ResetFunc() {
            for(int i = 0; i < 100; i++) {
                arrTiming[i] = 0;
            }
            textTimer.setAlpha(1);
            MainDelete.setVisibility(View.INVISIBLE);
            MainDelete.setClickable(false);
            nowRestoring = 0;
            buttonStart.setEnabled(true);
            dataExist = isStarted = isTouch = false;
            buttonStart.getBackground().setColorFilter(Color.rgb(0x3F, 0x51, 0xB5), PorterDuff.Mode.MULTIPLY);
            buttonStart.setText(R.string._start_str);
            timeSwapBuffer += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimeThread);
            startTime = timeInMilliseconds = timeSwapBuffer = updateTime = 0L;
            textTimer.setText(SData.getAccuracyFlag() ? "00:00" : "00:00.00");
            for(int i = 0; i < SData.getRunners(); i++) {
                runData[i].reset();
            }

            for(int i = 0; i < SData.getFieldX(); i++) {
                for(int j = 0; j < SData.getFieldY(); j++) {
                    if((i + 1 == SData.getFieldY()) && (SData.runnerNum < i*(SData.getFieldX()) + j+1)) break;
                    //arrButtons[i][j].setEnabled(false);
                }
            }
            UpcomingReset();
            deleteButtons();
            createButtons();
            buttonReset.setText(getResources().getString(R.string._str_Check));
        }

        final View.OnLongClickListener Signal_LongNumeral = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isCheck)
                {
                    MainDelete.setClickable(true);
                    MainDelete.setVisibility(View.VISIBLE);
                    isTouch = true;
                    textTimer.setAlpha(0);
                    int i = v.getId();
                    TouchedNumbers[i] = true;
                    v.getBackground().setColorFilter(Color.rgb(0x45, 0x40, 0xFB), PorterDuff.Mode.MULTIPLY);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        };

        final View.OnClickListener Signal_NumeralButton = new View.OnClickListener() {
            public void onClick(View v) {
                //Если запущен секундомер
                if (isCheck)
                {
                    if(isTouch) {
                        boolean TouchedLeft = false;
                        int i = v.getId();
                        TouchedNumbers[i] = !TouchedNumbers[i];
                        if(TouchedNumbers[i]) v.getBackground().setColorFilter(Color.rgb(0x45, 0x40, 0xFB), PorterDuff.Mode.MULTIPLY);
                        else v.getBackground().clearColorFilter();
                        for(int j = 0; j < 100; j++) {
                            if(TouchedNumbers[j]) {
                                TouchedLeft = true;
                                break;
                            }
                        }
                        if(!TouchedLeft) {
                            isTouch = false;
                            MainDelete.setClickable(false);
                            MainDelete.setVisibility(View.INVISIBLE);
                            textTimer.setAlpha(1);
                        }
                    }
                    else {
                        int i = v.getId();
                        arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setVisibility(View.INVISIBLE);
                        return;
                    }
                }
                if (isStarted && v.getVisibility() == View.VISIBLE && v.isEnabled()) {
                    //Сделать кнопку невидимой - только при динамичных кнопках
                    int idNum = v.getId();
                    UpcomingSet(idNum);
                    //Сохранение нажатой кнопки
                    Integer IDAdd = idNum;
                    Long longAdd = arrTiming[idNum];
                    if(nowRestoring == 5) {
                        for(int i = 1; i < 5; i++) {
                            arrRestore[i-1].first = arrRestore[i].first;
                            arrRestore[i-1].second = arrRestore[i].second;
                        }
                        nowRestoring--;
                    }
                    arrRestore[nowRestoring].first = IDAdd;
                    arrRestore[nowRestoring].second = longAdd;
                    nowRestoring++;
                    runData[idNum].lastLapTime = updateTime - arrTiming[idNum];
                    runData[idNum].lapTime[runData[idNum].getLapsTaken()] = runData[idNum].lastLapTime;
                    arrTiming[idNum] = updateTime;
                    if (SData.getLaps() - runData[idNum].getLapsTaken() == 3) {
                        v.getBackground().setColorFilter(Color.rgb(0xFB, 0xA0, 0x15), PorterDuff.Mode.MULTIPLY);

                    } else if (SData.getLaps() - runData[idNum].getLapsTaken() == 2) {
                        v.getBackground().setColorFilter(Color.rgb(0xFB, 0x10, 0x15), PorterDuff.Mode.MULTIPLY);
                    } else if (SData.getLaps() - runData[idNum].getLapsTaken() == 1) {
                        runData[idNum].passLap(updateTime);
                        int temp = SData.getRunners();
                        for (int i = 0; i < SData.getRunners(); i++) {
                            temp -= runData[i].LapsTaken < SData.getLaps() ? 0 : 1;
                        }
                        if (temp == 1) {
                            buttonEachLap.setText(getResources().getString(R.string._str_CheckEnd));
                            buttonStart.callOnClick();
                            buttonStart.setEnabled(true);
                            dataExist = true;
                            ViewPager _viewPager = getActivity().findViewById(R.id.container);
                            _viewPager.setCurrentItem(2, true);
                        }
                        v.getBackground().setColorFilter(Color.rgb(0xFF, 0xFF, 0xFF), PorterDuff.Mode.MULTIPLY);
                        v.setEnabled(false);
                        runData[idNum].LapsTaken++;
                        if(SData.getslashLaps())
                        {
                            String s = "" + (runData[(idNum / (SData.getFieldX()))*(SData.getFieldX()) +
                                    idNum % (SData.getFieldX())].getIDNumber()) + "/" +
                                    String.valueOf(runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken());
                            SpannableString sStr =  new SpannableString(s);
                            sStr.setSpan(new AbsoluteSizeSpan((int)minSize,true), 0,
                                    s.length()-1-String.valueOf(runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken()).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            sStr.setSpan(new AbsoluteSizeSpan((int)minSize/2,true),
                                    s.length()-1-String.valueOf(runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken()).length(),
                                    s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                            arrButtons[idNum / (SData.getFieldX())][idNum % (SData.getFieldX())].setText(sStr);
                        }
                        if(temp == 1) showTable();
                        return;
                    }
                    v.setEnabled(false);
                    //v.setVisibility(View.INVISIBLE);
                    runData[idNum].setHidden();
                    if(SData.getslashLaps())
                    {
                        String s = "" + (runData[(idNum / (SData.getFieldX()))*(SData.getFieldX()) +
                                idNum % (SData.getFieldX())].getIDNumber()) + "/" +
                                String.valueOf(runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken());
                        SpannableString sStr =  new SpannableString(s);
                        sStr.setSpan(new AbsoluteSizeSpan((int)minSize,true), 0,
                                s.length()-1-String.valueOf(runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken()).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        sStr.setSpan(new AbsoluteSizeSpan((int)minSize/2,true),
                                s.length()-1-String.valueOf(runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken()).length(),
                                s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                        arrButtons[idNum / (SData.getFieldX())][idNum % (SData.getFieldX())].setText(sStr);
                    }
                    runData[idNum].setTime(idNum, mins, secs, milliseconds);

                }
            }
        };

        //Установка массива номеров бегущих
        protected void setChronology(){
            int num = SData.getRunners();
            for(int i = 0; i < num; i++){
                if(arrIDNumber[i] == null) {
                    boolean hasName;
                    int toName = 1;
                    do {
                        hasName = false;
                        for (int j = 0; j < SData.getRunners() && hasName == false; j++) {
                            if(arrIDNumber[j] != null)
                                if (arrIDNumber[j].equals(String.valueOf(toName))) hasName = true;
                        }
                        if(hasName) toName++;
                    } while(hasName);
                    arrIDNumber[i] = String.valueOf(toName);
                }
                if(runData[i] == null) runData[i] = new RunnerData();
                runData[i].setIDNumber(arrIDNumber[i]);
            }
        }

        //Создание кнопок
        protected void createButtons(){
            minSize = 80;
            int numX = SData.getFieldX();
            int numY = SData.getFieldY();
            boolean eachToOther = (numY*numX == SData.getRunners());
            numY++;
            Point sizeM = new Point();
            display.getSize(sizeM);
            int maxWidth = sizeM.x
                    - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
            maxWidth -= (SData.formMain ? toDP(120) : 0);
            int maxHeight = sizeM.y
                        - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    165, getResources().getDisplayMetrics());
            int width= maxWidth / numX;
            int height  = (maxHeight / numY) > 370 ? 370 : (maxHeight / numY);

            if(buttonsCreated) return;
            arrRows = new TableRow[numY + 2];
            arrButtons = new Button[numY][numX];
            buttonsCreated = true;
            for(int i = 0; i < numY; i++) {
                arrRows[i] = new TableRow(getActivity());
                tableLayout.addView(arrRows[i]);
                for(int j = 0; j < numX; j++){
                    if((i + 1 == numY) || (SData.runnerNum < i*(numX) + j+1)) break;
                    arrButtons[i][j] = new Button(getActivity());
                    arrButtons[i][j].setPadding(5,5,5,5);
                    arrButtons[i][j].setId(i*(numX) + j);
                    Typeface font_style = Typeface.create("sans-serif", Typeface.NORMAL);
                    arrButtons[i][j].setAllCaps(false);
                    arrButtons[i][j].setTypeface(font_style);
                    arrButtons[i][j].setOnClickListener(Signal_NumeralButton);
                    arrButtons[i][j].setOnLongClickListener(Signal_LongNumeral);
                    String s = "" + (runData[i*(numX) + j].getIDNumber()) + "/" + String.valueOf(runData[i*(numX) + j].getLapsTaken());

                    arrButtons[i][j].setLayoutParams(new TableRow.LayoutParams(
                            width,
                            height));

                    Paint testPaint = new Paint();
                    testPaint.set(arrButtons[i][j].getPaint());
                    int targetWidth = width - arrButtons[i][j].getPaddingLeft() - arrButtons[i][j].getPaddingRight();
                    float hi = 80;
                    float lo = 2;
                    final float threshold = 0.5f; // How close we have to be

                    testPaint.set(arrButtons[i][j].getPaint());
                    while((hi - lo) > threshold) {
                        float size = (hi+lo)/2;
                        testPaint.setTextSize(size);
                        float temp = testPaint.measureText(s);
                        if((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, testPaint.measureText(s), getResources().getDisplayMetrics()) >= targetWidth)
                            hi = size; // too big
                        else
                            lo = size; // too small
                    }
                    if(minSize > lo) minSize = lo;
                    arrRows[i].addView(arrButtons[i][j]);
                }
            }

            float minSizeLap = 80;
            buttonEachLap = new Button(getActivity());

            Paint testPaint = new Paint();
            buttonEachLap.setPadding(5, 5, 5, 5);
            float hi = 80;
            float lo = 2;
            arrRows[numY - 2 + (eachToOther ? 1 : 0)].addView(buttonEachLap);
            if(eachToOther) buttonEachLap.setLayoutParams(new TableRow.LayoutParams(width, height, (float) 105));
            else buttonEachLap.setLayoutParams(new TableRow.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT, (float) 105));
            if(SData.formMain) {
                ConstraintLayout.LayoutParams LP = (ConstraintLayout.LayoutParams) tableLayout.getLayoutParams();
                LP.width = maxWidth;
                tableLayout.setLayoutParams(LP);
            }
            //-------------------------------------------------------------------------------------------START CHECK

            int targetWidth = width - buttonEachLap.getPaddingLeft() - buttonEachLap.getPaddingRight();
            //int targetWidth = maxWidth - width*(numX - numY*numX + SData.getRunners());
            final float threshold = 0.5f; // How close we have to be
            testPaint.set(buttonEachLap.getPaint());
            while((hi - lo) > threshold) {
                float size = (hi+lo)/2;
                testPaint.setTextSize(size);

                if((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, testPaint.measureText(
                        getResources().getString(R.string._inputNum)),
                        getResources().getDisplayMetrics()) >= targetWidth)
                    hi = size; // too big
                else
                    lo = size; // too small
            }
            sp1 = lo;
            //-------------------------------------------------------------------------------------------START CHECK LAP

            hi = 80;
            lo = 2;
            targetWidth = width - buttonEachLap.getPaddingLeft() - buttonEachLap.getPaddingRight();
            String s = ((updateTime != 0 || isStarted) ? getString(R.string._eachLap) : getString(R.string._inputNum));
            //
            testPaint.set(buttonEachLap.getPaint());
            while((hi - lo) > threshold) {
                float size = (hi+lo)/2;
                testPaint.setTextSize(size);

                if((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, testPaint.measureText(
                                getResources().getString(R.string._eachLap) + getResources().getString(R.string._eachLap)),
                        getResources().getDisplayMetrics()) >= targetWidth)
                    hi = size; // too big
                else
                    lo = size; // too small
            }
            //-------------------------------- END CHECK
            sp2 = lo;

            if(minSizeLap > lo) minSizeLap = (updateTime != 0 || isStarted) ? sp2 : sp1;
            if(minSizeLap > minSize) minSizeLap = minSize;

            SpannableString sStr = new SpannableString(s);
            sStr.setSpan(new AbsoluteSizeSpan((int)minSizeLap, true), 0,
                    ((updateTime != 0 || isStarted) ?
                    getString(R.string._eachLap).length() : getString(R.string._inputNum).length()), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            buttonEachLap.setText(sStr);
            buttonEachLap.setTextSize(minSizeLap);
            buttonEachLap.setOnClickListener(Signal_EachLap);
            for(int i = 0; i < SData.getRunners(); i++) {
                if(SData.getslashLaps()) s = "" + (runData[i / (SData.getFieldX()) * (numX) + i % (SData.getFieldX())].getIDNumber()) + "/" +
                        String.valueOf(runData[i / (SData.getFieldX()) * (numX) + i % (SData.getFieldX())].getLapsTaken());
                else s = "" + (runData[i / (SData.getFieldX()) * (numX) + i % (SData.getFieldX())].getIDNumber());
                arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setTextSize(minSize);
                sStr = new SpannableString(s);
                if(SData.getslashLaps()) {
                    sStr.setSpan(new AbsoluteSizeSpan((int)minSize, true), 0,
                            s.length() - 1 - String.valueOf(runData[i / (SData.getFieldX()) * (numX) +
                                    i % (SData.getFieldX())].getLapsTaken()).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    sStr.setSpan(new AbsoluteSizeSpan((int)minSize / 2, true),
                            s.length() - 1 - String.valueOf(runData[i / (SData.getFieldX()) * (numX) +
                                    i % (SData.getFieldX())].getLapsTaken()).length(), s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }
                else {
                    sStr.setSpan(new AbsoluteSizeSpan((int)minSize, true), 0, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }
                arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setText(sStr);
                if (runData[i].isHidden) {
                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setVisibility(View.INVISIBLE);
                } else {
                    if (SData.getLaps() - runData[i].getLapsTaken() == 2) {
                        arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].getBackground().setColorFilter(Color.rgb(0xFB, 0xA0, 0x15), PorterDuff.Mode.MULTIPLY);

                    } else if (SData.getLaps() - runData[i].getLapsTaken() == 1) {
                        arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].getBackground().setColorFilter(Color.rgb(0xFB, 0x10, 0x15), PorterDuff.Mode.MULTIPLY);
                    } else if (SData.getLaps() - runData[i].getLapsTaken() == 0) {
                        arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].getBackground().setColorFilter(Color.rgb(0xFF, 0xFF, 0xFF), PorterDuff.Mode.MULTIPLY);
                        arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setEnabled(false);
                    }
                }
            }

            LinearUp.removeAllViewsInLayout();
            for(int i = 0; i < Math.min(SData.getRunners(), 20); i++) {
                textUpc[i] = new TextView(getActivity());
                textUpc[i].setTextSize(13);
                textUpc[i].setTextColor(getResources().getColor(android.R.color.black));
                textUpc[i].setText(UpcomText[i]);
                LinearUp.addView(textUpc[i]);
            }
        }

        //Удаление кнопок
        protected void deleteButtons(){
            buttonsCreated = false;
            arrButtons = null;
            arrRows = null;
            tableLayout.removeAllViewsInLayout();
        }
    }
    //------------------------------------------------------------------------------------------------------------------------\\
    //------------------------------------------------Settings fragment-------------------------------------------------------\\

    public static class SettingsActivity extends Fragment {
        public static UUID uuid;
        public static final int VOICE_RECOGNITION_REQUEST_CODE = 4300;
        final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        protected Thread threadOld;
        public View rootView;
        protected ListView listView;
        protected ImageButton butAdd2, buttonAdd, buttonAccept, SpeechBut, buttonDeleteText, Button_BluetoothConnect;
        protected Button ButtonStyle1, ButtonStyle2, ButtonStyle3, SendButton, slash1Radio, slash2Radio;
        protected TextView textSpoken, texterr, BTAddress1, BTname1, editRunners, BluetoothStatus_text;
        protected EditText editTimeUntil, editWaitAfter, Param1Edit, Param2Edit, editLaps;
        //Radio 1
        protected RadioGroup radioGroup1;
        protected RadioButton radio1;
        //Radio Up
        protected RadioGroup radioGroupUp1;
        protected RadioButton radioUp1;

        protected EditText editRunnersNames;
        protected CheckBox checkVolumeStart;

        protected int OverallNumber;
        protected String arrIDNumberSettings[], tempArr[];

        public static class AcceptThread extends Thread{
            private final BluetoothServerSocket mmServerSocket;
            OutputStream mmOutStream;
            InputStream mmInStream;
            private boolean running;
            public void manageConnectedSocket(BluetoothSocket socket) {

            }

            public void setRunning(boolean running) {
                this.running = running;
            }

            public AcceptThread(BluetoothAdapter bluetooth){
                BluetoothServerSocket tmp = null;
                setRunning(false);
                try{
                    tmp = bluetooth.listenUsingRfcommWithServiceRecord("BluetoothTimer", uuid);
                } catch(IOException e){e.printStackTrace();}
                mmServerSocket = tmp;
            }

            public void run(){
                BluetoothSocket socket;
                while(running){
                    try{
                        socket = mmServerSocket.accept();
                    } catch(IOException e){
                        break;
                    }
                    // если соединение было подтверждено
                    if(socket!=null){
                        // управляем соединением
                        try {
                            mmServerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        manageConnectedSocket(socket);
                        InputStream tmpIn=null;
                        OutputStream tmpOut=null;
                        try{
                            tmpIn= socket.getInputStream();
                            tmpOut= socket.getOutputStream();
                        } catch(IOException e){}
                        mmInStream = tmpIn;
                        mmOutStream = tmpOut;
                        byte bytes_t = 9;
                        try{
                            mmOutStream.write(bytes_t);
                        } catch(IOException e){
                            e.printStackTrace();
                        }

                        try {
                            mmServerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
            public void cancel(){
                try{
                    setRunning(false);
                    mmServerSocket.close();
                } catch(IOException e){e.printStackTrace();}
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            showTable();
            isCheck = false;
            BuiltOnce = true;
            String s = arrIDNumber[0];
            Button TempB = (Button)rootView.findViewById(R.id.button_woSlash);
            TempB.setText(s);
            s += "/" + Tab1.runData[0].LapsTaken;
            SpannableString sStr = new SpannableString(s);
            sStr.setSpan(new AbsoluteSizeSpan((int)8, true),
                    s.length() - arrIDNumber[0].length() - 1, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            TempB = (Button)rootView.findViewById(R.id.button_withSlash);
            TempB.setText(sStr);
            radioGroupUp1.check(SData.formMain ? R.id.radio_up1 : R.id.radio_up2);
            SendButton.setVisibility(View.INVISIBLE);
            editRunnersNames.setEnabled(startTime==0);
            buttonAccept.setEnabled(startTime==0);
            buttonAdd.setEnabled(startTime==0);
            buttonDeleteText.setEnabled(startTime==0);
            SpeechBut.setEnabled(startTime==0);
            editLaps.setEnabled(startTime==0);
            Param1Edit.setEnabled(startTime==0);
            Param2Edit.setEnabled(startTime==0);
            buttonAdd.setEnabled(startTime==0);
            if(toUpdate){
                String rewriteText = "";
                for(int i = 0; i < OverallNumber; i++) {
                    rewriteText += arrIDNumberSettings[i];
                    if(i + 1 != OverallNumber) rewriteText += ",";
                }
                editRunnersNames.setText(rewriteText);
                toUpdate = false;
            }
            Tab1.updateStyle();
        }

        public void startSpeak() {
            Intent intent =  new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // Intent для вызова формы обработки речи (ОР)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); // сюда он слушает и запоминает
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string._SpeechAsk_str));
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE); // вызываем активность ОР

        }


        public void onActivityResult(int requestCode, int resultCode, Intent data){

            if (requestCode == 435 && resultCode == RESULT_OK) {
                Button_BluetoothConnect.callOnClick();
            }
            if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
                texterr.setText("");
                ok_Speech = true;
                ArrayList commandList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String SpeechSaid = commandList.get(0).toString();
                SpeechSaid = SpeechSaid.replace(" ", ",");
                SpeechSaid = SpeechSaid.replace(getResources().getString(R.string._replaceAnd), ",");
                SpeechSaid = SpeechSaid.replace(getResources().getString(R.string._replaceNumber), ",");
                while(SpeechSaid.contains(",,")){
                    SpeechSaid = SpeechSaid.replace(",,", ",");
                }
                if(SpeechSaid.startsWith(",")) SpeechSaid = SpeechSaid.substring(1);
                if(SpeechSaid.endsWith(",")) SpeechSaid = SpeechSaid.substring(0, SpeechSaid.length() - 1);
                String SpeechTemp = SpeechSaid;
                while(SpeechTemp.indexOf(',') != -1) {
                    int ComaIndex = SpeechTemp.indexOf(',');
                    if (SpeechTemp.substring(0, ComaIndex).length() > 9) {
                        ok_Speech = false;
                        break;
                    }
                    SpeechTemp = SpeechTemp.substring(SpeechTemp.indexOf(','), SpeechTemp.length()-1);
                }
                if(SpeechTemp.length() > 9) ok_Speech = false;
                if(!ok_Speech) texterr.setText(getResources().getString(R.string._err_Length));
                textSpoken.setText(SpeechSaid);
            }
            super.onActivityResult(requestCode, resultCode, data);

        }

        public void onDestroy() {

            super.onDestroy();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            rootView = inflater.inflate(R.layout.layoutsettings, container, false);
            //Сохранение элементов окна
            listView = (ListView)rootView.findViewById(R.id.listViewDeices);
            SendButton = (Button) rootView.findViewById(R.id.button_send);
            Button_BluetoothConnect = (ImageButton) rootView.findViewById(R.id.blueToothConnect);
            BluetoothStatus_text = (TextView) rootView.findViewById(R.id.bluetoothStatus);
            butAdd2 = (ImageButton) rootView.findViewById(R.id.buttonAdd2);
            SpeechBut = (ImageButton) rootView.findViewById(R.id.Button_speech);
            buttonAdd = (ImageButton) rootView.findViewById(R.id.buttonAdd);
            buttonAccept = (ImageButton)rootView.findViewById(R.id.button_Accept);
            ButtonStyle1 = (Button)rootView.findViewById(R.id.buttonStyle1);
            ButtonStyle2 = (Button)rootView.findViewById(R.id.buttonStyle2);
            ButtonStyle3 = (Button)rootView.findViewById(R.id.buttonStyle3);
            textSpoken = rootView.findViewById(R.id.SpokenText);
            texterr = (TextView) rootView.findViewById(R.id.text_Err);
            BTAddress1 = (TextView) rootView.findViewById(R.id.AddressBT_1);
            BTname1 = (TextView) rootView.findViewById(R.id.NameBT_1);
            editRunners = (TextView) rootView.findViewById(R.id.edit_runners);
            editLaps = (EditText) rootView.findViewById(R.id.edit_laps);
            editWaitAfter = (EditText) rootView.findViewById(R.id.edit_WaitAfter);
            Param1Edit = (EditText) rootView.findViewById(R.id.edit_Param1Edit);
            Param2Edit = (EditText) rootView.findViewById(R.id.edit_Param2Edit);
            editRunnersNames = (EditText) rootView.findViewById(R.id.edit_RunnersNames);
            editTimeUntil = (EditText) rootView.findViewById(R.id.edit_TimeUntil);
            buttonDeleteText = (ImageButton) rootView.findViewById(R.id.button_DeleteText);
            radio1 = (RadioButton) rootView.findViewById(R.id.radio_1);
            radioGroup1 = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
            slash1Radio = (Button) rootView.findViewById(R.id.button_withSlash);
            slash2Radio = (Button) rootView.findViewById(R.id.button_woSlash);


            radioGroupUp1 = (RadioGroup) rootView.findViewById(R.id.radioGroupUpcoming);
            radioUp1 = (RadioButton) rootView.findViewById(R.id.radio_up1);
            checkVolumeStart = (CheckBox) rootView.findViewById(R.id.check_volumeStart);
            //Получение значений из главного меню
            OverallNumber = SData.getRunners();
            checkVolumeStart.setChecked(SData.OnVolumeStart);

            ButtonStyle1.getBackground().setColorFilter(Color.rgb(221, 221, 221), PorterDuff.Mode.MULTIPLY);
            ButtonStyle2.getBackground().setColorFilter(Color.rgb(65, 63, 183), PorterDuff.Mode.MULTIPLY);
            ButtonStyle3.getBackground().setColorFilter(Color.rgb(237, 103, 63), PorterDuff.Mode.MULTIPLY);

            if(SData.styleID == 0) {
                ButtonStyle1.setAlpha(1);
                ButtonStyle2.setAlpha(0.25f);
                ButtonStyle3.setAlpha(0.25f);
            }
            else if(SData.styleID == 1){
                ButtonStyle1.setAlpha(0.25f);
                ButtonStyle2.setAlpha(1);
                ButtonStyle3.setAlpha(0.25f);
            }
            else {
                ButtonStyle1.setAlpha(0.25f);
                ButtonStyle2.setAlpha(0.25f);
                ButtonStyle3.setAlpha(1);
            }

            editWaitAfter.setText(String.valueOf(SData.afterTime));


            ButtonStyle1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SData.styleID = 0;
                    ButtonStyle1.setAlpha(1);
                    ButtonStyle2.setAlpha(0.25f);
                    ButtonStyle3.setAlpha(0.25f);
                    Tab1.updateStyle();
                }
            });

            ButtonStyle2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SData.styleID = 1;
                    ButtonStyle1.setAlpha(0.25f);
                    ButtonStyle2.setAlpha(1);
                    ButtonStyle3.setAlpha(0.25f);
                    Tab1.updateStyle();
                }
            });

            ButtonStyle3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SData.styleID = 2;
                    ButtonStyle1.setAlpha(0.25f);
                    ButtonStyle2.setAlpha(0.25f);
                    ButtonStyle3.setAlpha(1);
                    Tab1.updateStyle();
                }
            });

            if(SData.getslashLaps()) {
                slash1Radio.setAlpha(1);
                slash2Radio.setAlpha(0.5f);
            }
            else {
                slash2Radio.setAlpha(1);
                slash1Radio.setAlpha(0.5f);
            }

            slash1Radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SData.slashLaps = true;
                    slash1Radio.setAlpha(1);
                    slash2Radio.setAlpha(0.5f);
                }
            });

            slash2Radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SData.slashLaps = false;
                    slash2Radio.setAlpha(1);
                    slash1Radio.setAlpha(0.5f);
                }
            });

            radioGroupUp1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    SData.formMain = checkedId==R.id.radio_up2 ? false : true;
                }
            });

            editRunners.setText(String.valueOf(OverallNumber));
            radioGroup1.check(SData.getAccuracyFlag() ? R.id.radio_1 : R.id.radio_2);
            arrIDNumberSettings = arrIDNumber;
            editLaps.setText(String.valueOf(SData.getLaps()));
            Param1Edit.setText(String.valueOf(SData.Dialog_Param1));
            Param2Edit.setText(String.valueOf(SData.Dialog_Param2));
            editTimeUntil.setText(String.valueOf(SData.getTimeUntilShown()));

            uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            ok_Speech = false;

            editWaitAfter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().isEmpty() || s.toString().contains("."))
                    {
                        editWaitAfter.setError(getResources().getString(R.string._strinappropriate));
                    }
                    else if(s.toString().length() > 2 || Integer.parseInt(s.toString()) > SData.timeUntilSHown) {
                        editWaitAfter.setError(getResources().getString(R.string._strNeedLessRun));
                    }
                    else
                    {
                        SData.afterTime = Integer.parseInt(s.toString());
                        Log.d("TEST", String.valueOf(SData.afterTime));
                    }
                }
            });


            Param1Edit.addTextChangedListener(new TextWatcher() {
                private String before;
                private boolean mFormating;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {before = editLaps.getText().toString();}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if(!mFormating) {
                        String temp = editLaps.getText().toString();
                        String beforeTemp = before;

                        if(s.toString().isEmpty() || Integer.parseInt(s.toString()) == 0)
                        {
                            Param1Edit.setError(getResources().getString(R.string._strinappropriate));
                        }
                        else if(Integer.parseInt(s.toString()) < 30) {
                            Param1Edit.setError(getResources().getString(R.string._strNeedAbove29));
                        }
                        else
                        {
                            SData.Dialog_Param1 = Integer.parseInt(s.toString());
                            SData.setLaps(SData.Dialog_Param2 / SData.Dialog_Param1);
                            editLaps.setText(String.valueOf(SData.getLaps()));
                        }


                        mFormating = true;

                    }
                    mFormating = false;



                }
            });

            checkVolumeStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkVolumeStart.isChecked()) SData.OnVolumeStart = true;
                    else SData.OnVolumeStart = false;
                }
            });

            editLaps.addTextChangedListener(new TextWatcher() {
                private String before;
                private boolean mFormating;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    before = editLaps.getText().toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int position = editLaps.getSelectionEnd();

                    if(editLaps.getText().toString().length() == 0) {
                        editLaps.setText("0");
                        editLaps.setSelection(1);
                    }
                    else if(!mFormating) {
                        String temp = editLaps.getText().toString();
                        String beforeTemp = before;

                        if((temp.charAt(0) == '0') && (temp.length() == 2) && (before.equals("0"))){
                            before = "" + temp.charAt(1);
                            beforeTemp = before;
                        }
                        else if ((temp.equals("100")) || (((temp.length() == 1) || (((temp.charAt(0) <= '9') &&
                                (temp.charAt(0) > 0)) && (temp.length() == 2))) && !((temp.charAt(0) == '0') && (temp.length() >= 2)))){
                            before = temp;
                        }
                        mFormating = true;
                        if(before == beforeTemp) {
                            editLaps.setText(before);
                            editLaps.setSelection(position > before.length()-1 ? before.length()-1 : position-1);
                        }

                        if(temp.equals("00"))
                            editLaps.setText("0");

                        if(temp.equals("0"))
                            editLaps.setError(getResources().getString(R.string._strinappropriate));
                        else
                            SData.setLaps(Integer.parseInt(temp));
                    }
                    mFormating = false;
                }
            });
            //Установка ограничений на ввод минимального времени круга

            editTimeUntil.addTextChangedListener(new TextWatcher() {
                private String before;
                private boolean mFormating;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    before = editTimeUntil.getText().toString();
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    int position = editTimeUntil.getSelectionEnd();

                    if(editTimeUntil.getText().toString().length() == 0) {
                        editTimeUntil.setText("0");
                        editTimeUntil.setSelection(1);
                    }
                    else if(!mFormating) {


                        String temp = editTimeUntil.getText().toString();
                        String beforeTemp = before;

                        if((temp.charAt(0) == '0') && (temp.length() == 2) && (before.equals("0"))){
                            before = "" + temp.charAt(1);
                            beforeTemp = before;
                        }
                        else if (((temp.length() == 1) || (((temp.charAt(0) <= '9') && (temp.charAt(0) > 0)) && (temp.length() == 2))) && !((temp.charAt(0) == '0') && (temp.length() >= 2))) {
                            before = temp;
                        }
                        mFormating = true;
                        if(before == beforeTemp) {
                            editTimeUntil.setText(before);
                            editTimeUntil.setSelection(position > before.length()-1 ? before.length()-1 : position-1);
                        }
                        SData.setTimeUntilSHown(Integer.parseInt(s.toString()));

                        if(SData.timeUntilSHown < Integer.parseInt(editWaitAfter.getText().toString())) {
                            SData.afterTime = 0;
                            editWaitAfter.setError(getResources().getString(R.string._strNeedLessRun));
                        }
                        else {
                            SData.afterTime = Integer.parseInt(editWaitAfter.getText().toString());
                            if(Tab2.CanvasView != null) Tab2.CanvasView.default_time = SData.timeUntilSHown * 1000;
                            Tab0.CanvasView.default_time = SData.timeUntilSHown * 1000;
                            editWaitAfter.setError(null);
                    }

                    }
                    mFormating = false;
                }
            });
            //Установка ограничений на ввод целых десятичных чисел
            editLaps.setKeyListener(DigitsKeyListener.getInstance(false, false));
            editTimeUntil.setKeyListener(DigitsKeyListener.getInstance(false, false));

            String rewriteText = "";
            for(int i = 0; i < OverallNumber; i++) {
                rewriteText += arrIDNumberSettings[i];
                if(i + 1 != OverallNumber) rewriteText += ",";
            }
            editRunnersNames.setText(rewriteText);

            //***********-Установка слотов на кнопки***************
            View.OnClickListener Signal_add2 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ok_Speech) {
                        editRunnersNames.append("," + textSpoken.getText());
                        buttonAccept.callOnClick();
                    }
                }
            };


            View.OnClickListener Signal_ConnectBluetooth = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bluetooth!=null)
                    {   // С Bluetooth все в порядке.
                        if (!bluetooth.isEnabled()) {
                            // Bluetooth выключен. Предложим пользователю включить его.
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 435);
                        }
                        String status;
                        if(bluetooth.isEnabled()){
                            BluetoothStatus_text.setText("");
                            String mydeviceaddress = bluetooth.getAddress();
                            String mydevicename = bluetooth.getName();
                            //BTAddress1.setText(mydeviceaddress);
                            BTname1.setText(mydevicename);


                            //Объявляем адаптер
                            final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(
                                    rootView.getContext(),android.R.layout.simple_list_item_1);
                            //Ищем спаренные устройства
                            Set<BluetoothDevice> pairedDevices= bluetooth.getBondedDevices();
                            // Если список спаренных устройств не пуст
                            if(pairedDevices.size()>0){
                            // идём в цикле по этому списку
                                for(BluetoothDevice device: pairedDevices){
                                // Добавляем имена и адреса в mArrayAdapter,
                                // чтобы показать через ListView
                                    mArrayAdapter.add(device.getName()+"\n"+ device.getAddress());
                                }
                                // подключаем список к адаптеру
                            }
                            listView.setOnTouchListener(new View.OnTouchListener() {
                                // Setting on Touch Listener for handling the touch inside ScrollView
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    // Disallow the touch request for parent scroll on touch of child view
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    return false;
                                }
                            });


                            // Создаем BroadcastReceiver для ACTION_FOUND
                            BroadcastReceiver mReceiver = new BroadcastReceiver(){
                                public void onReceive(Context context, Intent intent){
                                    String action= intent.getAction();
                                    // Когда найдено новое устройство
                                    if(BluetoothDevice.ACTION_FOUND.equals(action)){
                                        // Получаем объект BluetoothDevice из интента
                                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                        //Добавляем имя и адрес в array adapter, чтобы показывать в ListView
                                        mArrayAdapter.add(device.getName()+"\n"+ device.getAddress());

                                        listView.setAdapter(mArrayAdapter);
                                    }
                                }
                            };
                            // Регистрируем BroadcastReceiver
                            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                            //rootView.getContext().registerReceiver(mReceiver, filter);
                            //bluetooth.startDiscovery();
                            listView.setAdapter(mArrayAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    BluetoothStatus_text.setText(mArrayAdapter.getItem(position));
                                    SendButton.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                        else
                        {
                            status=getResources().getString(R.string._err_BToff);
                            BluetoothStatus_text.setText(status);
                        }

                    }
                }
            };

            View.OnClickListener Signal_Send = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AcceptThread threadBluetooth = new AcceptThread(bluetooth);
                    Thread thread = new Thread(threadBluetooth);
                    threadBluetooth.setRunning(true);
                    thread.start();
                    if(threadOld!=null) threadOld.interrupt();
                    threadOld = thread;
                }
            };

            View.OnClickListener Signal_Speech = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ok_Speech = false;
                    startSpeak();
                }
            };

            View.OnClickListener Signal_Accept = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SData.setLaps(Integer.parseInt(editLaps.getText().toString()));
                    SData.setTimeUntilSHown(Integer.parseInt(editTimeUntil.getText().toString()));
                    if (getNames()) {
                        int Runners = Integer.parseInt(editRunners.getText().toString());
                        if (Runners != 0) {
                            for (int i = Runners; i < SData.getRunners(); i++) {
                                arrTiming[i] = 0;
                            }
                            SData.setRunners(Runners);
                            arrIDNumber = arrIDNumberSettings;
                        }
                    }
                    SData.setAccuracyFlag((radioGroup1.getCheckedRadioButtonId() == R.id.radio_1) ? true : false);
                    FilePrint(SData);
                }
            };

            View.OnClickListener Signal_Add = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonAccept.callOnClick();
                    texterr.setText("");
                    String[] arrIDTemp = new String[101];
                    int toName = 1;
                    if(editRunnersNames.getText().length() == 0)
                    {
                        OverallNumber = 1;
                        arrIDTemp[0] = "1";

                        SData.setRunners(OverallNumber);
                    }
                    else
                    {
                        if(OverallNumber == 100) return;
                        OverallNumber++;
                        SData.setRunners(OverallNumber);
                        for(int i = 0; i < OverallNumber-1; i++) arrIDTemp[i] = arrIDNumber[i];

                        boolean hasName = false;
                        do {
                            hasName = false;
                            for (int j = 0; j < SData.getRunners() && hasName == false; j++) {
                                if(j < OverallNumber-1)
                                    if (arrIDNumber[j].equals(String.valueOf(toName))) hasName = true;
                            }
                            if(hasName) toName++;
                        } while(hasName);
                    }

                    arrIDTemp[OverallNumber-1] = String.valueOf(toName);
                    arrIDNumber = arrIDNumberSettings = arrIDTemp;
                    String rewriteText = "";
                    for(int i = 0; i < OverallNumber; i++) {
                        rewriteText += arrIDNumberSettings[i];
                        if(i + 1 != OverallNumber) rewriteText += ",";
                    }
                    editRunnersNames.setText(rewriteText);
                    getNames();
                    editRunnersNames.append("");
                    buttonAccept.callOnClick();
                    editRunnersNames.setSelection(editRunnersNames.length());
                    texterr.setText("");
                }
            };
            //Кнопка отмены
            View.OnClickListener Signal_Decline = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rewriteText = "";
                    OverallNumber = SData.getRunners();
                    for(int i = 0; i < OverallNumber; i++) {
                        rewriteText += arrIDNumber[i];
                        if(i + 1 != OverallNumber) rewriteText += ",";
                    }
                    editRunnersNames.setText(rewriteText);
                    editRunners.setText(String.valueOf(OverallNumber));
                }
            };
            //Кнопка принять

            View.OnClickListener Signal_ClearInput = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editRunnersNames.setText("");
                }
            };
            buttonDeleteText.setOnClickListener(Signal_ClearInput);
            buttonAccept.setOnClickListener(Signal_Accept);
            buttonAdd.setOnClickListener(Signal_Add);
            butAdd2.setOnClickListener(Signal_add2);
            SpeechBut.setOnClickListener(Signal_Speech);
            SendButton.setOnClickListener(Signal_Send);
            Button_BluetoothConnect.setOnClickListener(Signal_ConnectBluetooth);
            //************************************************
            return rootView;
        }

        void setRunners(){
            int toSet = Integer.parseInt(editRunners.getText().toString());
            String rewriteText = "";
            if(toSet == OverallNumber) {

            }
            else if(OverallNumber < toSet) {
                String TempArray[] = new String[toSet];
                for (int i = 0; i < OverallNumber; i++) {
                    TempArray[i] = arrIDNumberSettings[i];
                }
                for (int i = OverallNumber; i < toSet; i++) {
                    TempArray[i] = String.valueOf(i + 1);
                }
                OverallNumber = toSet;
                arrIDNumberSettings = TempArray;
                Arrays.sort(arrIDNumberSettings);
            }
            else{
                String TempArray[] = new String[toSet];
                for (int i = 0; i < toSet; i++) {
                    TempArray[i] = arrIDNumberSettings[i];
                }
                OverallNumber = toSet;
                arrIDNumberSettings = TempArray;
                Arrays.sort(arrIDNumberSettings);
            }
            for(int i = 0; i < OverallNumber; i++) {
                rewriteText += arrIDNumberSettings[i];
                if(i + 1 != OverallNumber) rewriteText += ",";
            }
            editRunnersNames.setText(rewriteText);
        }



        boolean getNames() {
            int result = 0;
            int Runners = Integer.parseInt(editRunners.getText().toString());
            String str = editRunnersNames.getText().toString();
            OverallNumber = 1;
            String acceptable = "0123456789,";
            if(str.length() == 0) result = 1; //Ошибка: пусто
            str = str.replace(' ', ',');
            for(int i = 0; i < str.length(); i++) {
                if(str.length() > i+1)
                if(str.charAt(i) == ',' && str.charAt(i+1) != ',') OverallNumber++;
            }
            OverallNumber -= (str.startsWith(",")) ? 1 : 0;
            OverallNumber = OverallNumber > 100 ? 100 : OverallNumber;
            tempArr = new String[OverallNumber];
            String got = "";
            int count1 = 0, len = 0;
            boolean fineTemp = false;
            for(int i = 0; i < str.length() && count1 < OverallNumber; i++) {
                //if(str.charAt(i) == ',' && len == 0) result = 3;// Ошибка: ожидалось число, получен иной символ
                if(str.charAt(i) == ',') {
                    if(got != null && got != "" && fineTemp) {
                        tempArr[count1] = got;
                        count1++;
                    }
                    fineTemp = false;
                    got = "";
                    len = 0;
                }
                else {
                    len++;
                    if(str.charAt(i) != ' ') fineTemp = true;
                    /*
                    else {
                        if(len == 1) continue;
                        if(i > 0)
                            if(str.charAt(i - 1) == ' ') continue;
                    }
                    */
                    got += str.charAt(i);
                }
                if(len > 9) result = 4;// Ошибка: слишком длинный номер участника (>=10^10)
            }
            if(got != null && got != "" && fineTemp && count1 < OverallNumber)
            tempArr[count1] = got;
            else count1--;
            count1 = OverallNumber;
            //result = (str.charAt(str.length() - 1) == ',' ? 3 : result);
            arrIDNumberSettings = tempArr;

            //if(OverallNumber < Runners) for(int i = OverallNumber; i < Runners; i++) arrIDNumber[i] = i+1;
            if(result == 0) {
                class ComparString implements Comparator<String> {
                    public int compare(String first, String second) {
                        boolean resultBool;
                        boolean firstNum = true, secondNum = true;
                        for(int i = 0; i < first.length(); i++) {
                            if(first.charAt(i) != '0' && first.charAt(i) != '1' &&
                               first.charAt(i) != '2' && first.charAt(i) != '3' &&
                               first.charAt(i) != '4' && first.charAt(i) != '5' &&
                               first.charAt(i) != '6' && first.charAt(i) != '7' &&
                               first.charAt(i) != '8' && first.charAt(i) != '9'
                            ) firstNum = false;
                        }
                        for(int i = 0; i < second.length(); i++) {
                            if(second.charAt(i) != '0' && second.charAt(i) != '1' &&
                                    second.charAt(i) != '2' && second.charAt(i) != '3' &&
                                    second.charAt(i) != '4' && second.charAt(i) != '5' &&
                                    second.charAt(i) != '6' && second.charAt(i) != '7' &&
                                    second.charAt(i) != '8' && second.charAt(i) != '9'
                            ) secondNum = false;
                        }
                        if(firstNum) {
                            if(secondNum) {
                                int firstInt = Integer.parseInt(first);
                                int secondInt = Integer.parseInt(second);
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
                                resultBool = first.compareTo(second) > 0;
                            }
                        }
                        return resultBool ? 1 : -1;
                    }
                }
                Arrays.sort(arrIDNumberSettings, new ComparString());
                for(int i = 1; i < OverallNumber; i++) {
                    if(arrIDNumberSettings[i].equals(arrIDNumberSettings[i - 1])) {
                        arrIDNumberSettings[i - 1] = "0";
                    }
                }
                Arrays.sort(arrIDNumberSettings, new ComparString());
                int tempNumber = OverallNumber;
                String rewriteText = "";
                result = 0;
                for(int i = 0; i < tempNumber; i++) {
                    if(arrIDNumberSettings[i].equals("0")) {
                        OverallNumber--;
                        continue;
                    }
                    if(result != 0) rewriteText += ",";
                    result++;
                    rewriteText += arrIDNumberSettings[i];
                }
                if(result == 0) {
                    texterr.setText(getResources().getString(R.string._err_Empty));
                    return false;
                }
                result = 0;
                if(tempNumber - OverallNumber != 0) {
                    tempArr = new String[OverallNumber];
                    for(int i = 0; i < tempNumber; i++) {
                        if(!arrIDNumberSettings[i].equals("0")) {
                            tempArr[result] = arrIDNumberSettings[i];
                            result++;
                        }
                    }
                    arrIDNumberSettings = tempArr;
                }
                editRunners.setText(String.valueOf(OverallNumber));
                editRunnersNames.setText(rewriteText);
                texterr.setText("");
                return true;
            }
            else {

                if(result == 1) texterr.setText(getResources().getString(R.string._err_Empty));
                //else if(result == 2) texterr.setText("Принимаются без пробелов только номера, разделенные запятыми");
                //else if(result == 3) texterr.setText("Вместо числа был обнаружен иной символ");
                if(result == 4) texterr.setText(getResources().getString(R.string._err_Length));
                else                 texterr.setText(getResources().getString(R.string._err_Default));

                return false;
            }
        }

    }
    //----------------------------------------------------------------------------------------------\\
    //-------------------------------------database fragment----------------------------------------\\

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


    public static class database extends Fragment {
        protected TextView text_Column1, text_Column2, text_Column3, text2_Column1;
        public DrawView CanvasView;
        public int int_Column1, int_Column2, int_Column3;
        protected Switch switchVis;
        public View rootView;
        public boolean ShownCanvas, ShowFull;
        protected TableLayout tableManage;

        @Override

        public void onResume() {
            super.onResume();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) text_Column2.getLayoutParams();
            params.leftMargin = toDP(180);

            ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) text_Column3.getLayoutParams();
            params1.leftMargin = toDP( 270);

            text2_Column1.setVisibility(ShownCanvas ? View.VISIBLE : View.INVISIBLE);
            text_Column2.setVisibility(!ShownCanvas ? View.VISIBLE : View.INVISIBLE);
            text_Column3.setVisibility(!ShownCanvas ? View.VISIBLE : View.INVISIBLE);
        }

        public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            rootView = inflater.inflate(R.layout.activity_database, container, false);
            //Отмена фокусировки на EditText'ы
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            //Предустановка переменных
            arrRowData = new TableRow[101];
            arrTextData = new TextView[101];
            arrTextLapsTime = new TextView[101];
            arrTimeData = new TextView[101];
            arrLapsData = new TextView[101];
            ShownCanvas = false;
            ShowFull = false;
            int_Column1 = 1;
            int_Column2 = 0;
            int_Column3 = 0;
            //Сохранение элементов экрана
            tableManage = (TableLayout)rootView.findViewById(R.id.tableManage);
            switchVis = (Switch)rootView.findViewById(R.id.switchVisual);
            text_Column1 = (TextView)rootView.findViewById(R.id.textColumn1);
            text_Column2 = (TextView)rootView.findViewById(R.id.textColumn2);
            text_Column3 = (TextView)rootView.findViewById(R.id.textColumn3);
            text2_Column1 = (TextView)rootView.findViewById(R.id.text2Column1);


            switchVis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ShownCanvas = !ShownCanvas;
                    text2_Column1.setVisibility(ShownCanvas ? View.VISIBLE : View.INVISIBLE);
                    text_Column2.setVisibility(!ShownCanvas ? View.VISIBLE : View.INVISIBLE);
                    text_Column3.setVisibility(!ShownCanvas ? View.VISIBLE : View.INVISIBLE);
                    int_Column1 = 1;
                    int_Column2 = 0;
                    int_Column3 = 0;
                    if(ShownCanvas) text_Column1.setText(getResources().getString(R.string._participant_str));
                    else {
                        text_Column1.setText(getResources().getString(R.string._participant_str));
                        text_Column2.setText(getResources().getString(R.string._time_str));
                        text_Column3.setText(getResources().getString(R.string._eachLap));
                    }
                    showTable();
                }
            });

            View.OnClickListener Signal_Column1 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(int_Column2 != 0) {
                        String tempStr = text_Column2.getText().toString();
                        text_Column2.setText(tempStr.substring(0, tempStr.length() - 3));
                        int_Column2 = 0;
                    }
                    if(int_Column3 != 0) {
                        String tempStr = text_Column3.getText().toString();
                        text_Column3.setText(tempStr.substring(0, tempStr.length() - 3));
                        int_Column3 = 0;
                    }

                    String newText = text_Column1.getText().toString();
                    if(int_Column1 != 0) {
                        if (int_Column1 == 1) {
                            newText = newText.substring(0, newText.length() - 3) + "(↓)";
                            int_Column1++;
                        }
                        else {
                            newText = newText.substring(0, newText.length() - 3) + "(↑)";
                            int_Column1 = 1;
                        }
                    }
                    else {
                        int_Column1++;
                        newText = newText + "(↑)";
                    }
                    text_Column1.setText(newText);
                    showTable();
                }
            };

            View.OnClickListener Signal_Column2 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(int_Column1 != 0) {
                        String tempStr = text_Column1.getText().toString();
                        text_Column1.setText(tempStr.substring(0, tempStr.length() - 3));
                        int_Column1 = 0;
                    }
                    if(int_Column3 != 0) {
                        String tempStr = text_Column3.getText().toString();
                        text_Column3.setText(tempStr.substring(0, tempStr.length() - 3));
                        int_Column3 = 0;
                    }

                    String newText = text_Column2.getText().toString();
                    if(int_Column2 != 0) {
                        if (int_Column2 == 1) {
                            newText = newText.substring(0, newText.length() - 3) + "(↓)";
                            int_Column2++;
                        }
                        else {
                            newText = newText.substring(0, newText.length() - 3) + "(↑)";
                            int_Column2 = 1;
                        }
                    }
                    else {
                        int_Column2++;
                        newText = newText + "(↑)";
                    }
                    text_Column2.setText(newText);
                    showTable();
                }
            };

            View.OnClickListener Signal_Column3 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(int_Column1 != 0) {
                        String tempStr = text_Column1.getText().toString();
                        text_Column1.setText(tempStr.substring(0, tempStr.length() - 3));
                        int_Column1 = 0;
                    }
                    if(int_Column2 != 0) {
                        String tempStr = text_Column2.getText().toString();
                        text_Column2.setText(tempStr.substring(0, tempStr.length() - 3));
                        int_Column2 = 0;
                    }

                    String newText = text_Column3.getText().toString();
                    if(int_Column3 != 0) {
                        if (int_Column3 == 1) {
                            newText = newText.substring(0, newText.length() - 3) + "(↓)";
                            int_Column3++;
                        }
                        else {
                            newText = newText.substring(0, newText.length() - 3) + "(↑)";
                            int_Column3 = 1;
                        }
                    }
                    else {
                        int_Column3++;
                        newText = newText + "(↑)";
                    }
                    text_Column3.setText(newText);
                    showTable();
                }
            };

            text_Column1.setOnClickListener(Signal_Column1);
            text_Column2.setOnClickListener(Signal_Column2);
            text_Column3.setOnClickListener(Signal_Column3);
            //Заполнение таблицы информацией
            showTable();
            return rootView;
        }

        //Заполнение таблицы информацией
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
        Tab3 = new SettingsActivity();
        Tab2 = new database();
        Tab1 = new MainActivity();
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
