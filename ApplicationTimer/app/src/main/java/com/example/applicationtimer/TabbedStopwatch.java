package com.example.applicationtimer;

 import android.app.NotificationManager;
 import android.app.PendingIntent;
 import android.bluetooth.BluetoothAdapter;
 import android.bluetooth.BluetoothDevice;
 import android.bluetooth.BluetoothServerSocket;
 import android.bluetooth.BluetoothSocket;
 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.ContextWrapper;
 import android.content.Intent;
 import android.content.IntentFilter;
 import android.content.pm.ActivityInfo;
 import android.content.res.Resources;
 import android.graphics.Color;
import android.graphics.Paint;
 import android.graphics.Point;
 import android.graphics.PorterDuff;
import android.graphics.Typeface;
 import android.graphics.drawable.GradientDrawable;
 import android.os.Handler;
import android.os.SystemClock;
 import android.speech.RecognizerIntent;
 import android.support.v4.app.NotificationCompat;
 import android.support.v7.app.AppCompatActivity;
 import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
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
 import android.view.View;
import android.view.ViewGroup;

 import android.view.WindowManager;
 import android.widget.AdapterView;
 import android.widget.ArrayAdapter;
 import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
 import android.widget.ListView;
 import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 import java.util.ArrayList;
 import java.util.Arrays;
import java.util.Comparator;
 import java.util.Set;
 import java.util.UUID;

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
    static protected boolean BuiltOnce = false;
    static protected TextView arrTextData[], arrTimeData[], arrLapsData[];
    static protected SettingsActivity Tab3;
    static protected database Tab2;
    static protected MainActivity Tab1;
    static protected boolean buttonsCreated = false;
    static long startTime=0L, timeInMilliseconds=0L, timeSwapBuffer, updateTime=0L, arrTiming[];
    static int secs, mins, milliseconds, nowRestoring;
    static protected boolean dataExist, isCheck = false, isStarted = false, ok_Speech;
    static protected SettingsData SData;
    static protected String arrIDNumber[];
    static protected NotificationManager notificationManager;
    //Класс участника
    protected static class RunnerData{
        protected int LapsTaken = 0;
        protected String IDNumber = "0";
        protected int arrMinutes[] = new int[100];
        protected int arrSeconds[] = new int[100];
        protected int arrMilisnd[] = new int[100];
        protected long lastTime = 0;
        protected boolean isHidden = false;
        protected long timeHidden = 0;
        protected long startTime1 = 0;
        //setters
        void setHidden() {isHidden = true; startTime1 = updateTime; LapsTaken++;}
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
            arrMinutes = new int[100];
            arrSeconds = new int[100];
            arrMilisnd = new int[100];
        }

        public boolean passSecond() {
            timeHidden = updateTime - startTime1;
            if(timeHidden>=(SData.getTimeUntilShown()*1000)) {
                timeHidden = 0; isHidden = false;
            }
            return isHidden;
        }
    }



    //Класс настроек
    protected static class SettingsData {
        protected boolean notification = true;
        protected int runnerNum = 10;
        protected int laps = 10;
        protected int numX = 3;
        protected int numY = 4;
        protected int styleID = 0;
        protected boolean OnVolumeStart = false;
        protected boolean staticButtons = true;
        protected boolean accuracyFlag  = true;
        protected int timeUntilSHown = 10;
        protected boolean slashLaps = true;

        SettingsData(){}

        SettingsData(int _runnerNumber) {
            this.setRunners(_runnerNumber);
            setFieldX(numX);
        }

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
        public    View rootView;
        protected Button buttonStart;
        protected Button buttonReset;
        protected Button buttonEachLap;
        protected TextView textTimer;
        protected TableLayout tableLayout;
        protected Button arrButtons[][];
        protected TableRow arrRows[];
        protected ImageButton BackButton;

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
                        if(arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].getVisibility() == View.INVISIBLE)
                            if(runData[i].passSecond() == false){
                                if (SData.getLaps() - runData[i].getLapsTaken() == 2) {
                                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].getBackground().setColorFilter(Color.rgb(0xFB, 0xA0, 0x15), PorterDuff.Mode.MULTIPLY);

                                } else if (SData.getLaps() - runData[i].getLapsTaken() == 1) {
                                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].getBackground().setColorFilter(Color.rgb(0xFB, 0x10, 0x15), PorterDuff.Mode.MULTIPLY);
                                } else if (SData.getLaps() - runData[i].getLapsTaken() == 0) {
                                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].getBackground().setColorFilter(Color.rgb(0xFF, 0xFF, 0xFF), PorterDuff.Mode.MULTIPLY);
                                }


                                arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].setVisibility(View.VISIBLE);
                            }
                    }
                customHandler.postDelayed(this, 0);
            }
        };
        Handler customHandler = new Handler();


        public void onPause() {
            super.onPause();
            currentTime = textTimer.getText().toString();
        }


        @Override
        public void onResume() {
            super.onResume();

            GradientDrawable gD;
            if (SData.styleID == 1) {
                gD = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{
                        getResources().getColor(R.color.yellowish_Brown), getResources().getColor(R.color.Brown)});
                Tab1.rootView.findViewById(R.id.activity1Layout).setBackgroundDrawable(gD);
            }
            else if (SData.styleID == 2) {
                gD = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{
                        getResources().getColor(R.color.Whitey_pink), getResources().getColor(R.color.Pink)});
                Tab1.rootView.findViewById(R.id.activity1Layout).setBackgroundDrawable(gD);
            }
            else {
                Tab1.rootView.findViewById(R.id.activity1Layout).setBackgroundColor(getResources().getColor(R.color.design_default_colorE));
            }


            if(BuiltOnce) {
                SData.setTimeUntilSHown(Integer.parseInt(Tab3.editTimeUntil.getText().toString()));
                SData.setLaps(Integer.parseInt(Tab3.editLaps.getText().toString()));
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
                if(isStarted) buttonReset.setEnabled(false);
                else buttonReset.setEnabled(true);
            }
            setChronology();
            //Обновление кнопок
            deleteButtons();
            createButtons();
            if(isStarted) buttonEachLap.setText(getResources().getString(R.string._eachLap));
            else buttonEachLap.setText(getResources().getString(R.string._str_Check));
            FilePrint(SData);
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            rootView = inflater.inflate(R.layout.activity_main, container, false);
            //Сохранение элементов окна
            buttonStart = (Button) rootView.findViewById(R.id.Button_Start);
            buttonReset = (Button) rootView.findViewById(R.id.button_Reset);
            textTimer = (TextView)rootView.findViewById(R.id.text_Timer);
            tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
            BackButton = (ImageButton) rootView.findViewById(R.id.backButton);

            //Предустановка переменных
            if(!BuiltOnce) {
                arrRestore = new Pair[5];
                for(int i = 0; i < 5; i++){
                    arrRestore[i] = new Pair();
                }
                nowRestoring = 0;
                arrTiming = new long[101];
                textTimer.setText(SData.getAccuracyFlag() ? "00:00" : "00:00.00");
                timeSwapBuffer=0L;
                dataExist = false;
                isCheck = false;
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
            //Кнопка Старт
            View.OnClickListener Signal_Start = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonEachLap.setText(getResources().getString(R.string._eachLap));
                    if(isStarted) {
                        //Когда секундомер останавливаем
                        buttonStart.setText(R.string._start_str);
                        //Закрыть уведомление
                        notificationManager.cancel(102);
                        //----------------
                        buttonStart.getBackground().setColorFilter(Color.rgb(0x3F, 0x51, 0xB5), PorterDuff.Mode.MULTIPLY);
                        timeSwapBuffer += timeInMilliseconds;
                        customHandler.removeCallbacks(updateTimeThread);
                        buttonReset.setEnabled(true);
                    }
                    else {
                        //Когда стартует секундомер
                        if (dataExist) {
                            buttonReset.callOnClick();
                        }

                        if(SData.notification) {
                            // Создание уведомления

                            Intent notificationIntent = new Intent(rootView.getContext(), MainActivity.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(rootView.getContext(),
                                    0, notificationIntent,
                                    PendingIntent.FLAG_CANCEL_CURRENT);
                            /*
                            String BROADCAST_ACTION2 = "com.example.uniqueTag2";
                            BroadcastReceiver br2 = new BroadcastReceiver() {
                                // действия при получении сообщений
                                public void onReceive(Context context, Intent intent) {
                                    startActivity(intent);
                                    //onResume();
                                }
                            };
                            IntentFilter intFilt2 = new IntentFilter(BROADCAST_ACTION2);
                            rootView.getContext().registerReceiver(br2, intFilt2);
                            Intent Bintent2 = new Intent(BROADCAST_ACTION2);
                            //Intent notificationIntent = new Intent(rootView.getContext(), MainActivity.class);

                            PendingIntent contentIntent = PendingIntent.getActivity(rootView.getContext(),
                                    0, Bintent2, PendingIntent.FLAG_UPDATE_CURRENT);
                            */
                            Resources res = getResources();

                            // до версии Android 8.0 API 26
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(rootView.getContext());

                            String BROADCAST_ACTION = "com.example.uniqueTag1";

                            BroadcastReceiver br = new BroadcastReceiver() {
                                // действия при получении сообщений
                                public void onReceive(Context context, Intent intent) {
                                    SData.notification = false;
                                    if(Tab3 != null) Tab3.checkNotifications.setChecked(false);
                                    notificationManager.cancel(102);
                                }
                            };
                            IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
                            rootView.getContext().registerReceiver(br, intFilt);
                            Intent Bintent = new Intent(BROADCAST_ACTION);
                            //В следующей строке мы определяем кто именно будет обрабатывать наш Intent с помощью .getBroadcast
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(rootView.getContext(), 0, Bintent, 0);
                            builder.setContentIntent(contentIntent)
                                    // обязательные настройки
                                    .setSmallIcon(R.drawable.buttonacceptstyle)
                                    .setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                                    //.setContentTitle("Напоминание")
                                    .setContentText(res.getString(R.string.notifytext) + SData.getRunners())
                                    //.setContentText("Пора покормить кота") // Текст уведомления
                                    // необязательные настройки
                                    //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.hungrycat)) // большая
                                    // картинка
                                    //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                                    .setTicker("Таймер запущен")
                                    .setOngoing(true)
                                    .setWhen(System.currentTimeMillis())
                                    //.addAction(R.mipmap.ic_launcher, "Открыть", contentIntent)
                                    .addAction(R.mipmap.ic_launcher, "Не показывать", pendingIntent)
                                    .setAutoCancel(false); // автоматически закрыть уведомление после нажатия

                            // Альтернативный вариант
                            // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                            notificationManager.notify(102, builder.build());
                        }
                        //------------------------

                        buttonReset.setEnabled(false);
                        buttonStart.getBackground().setColorFilter(Color.rgb(0xE5, 0x21, 0x3F), PorterDuff.Mode.MULTIPLY);
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimeThread, 0);
                        buttonStart.setText(R.string._stop_str);
                    }
                    isStarted = !isStarted;
                }
            };
            //Кнопка отмены
            View.OnClickListener Signal_Back = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            };
            //Кнопка Сброса
            View.OnClickListener Signal_Reset = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i = 0; i < 100; i++) {
                        arrTiming[i] = 0;
                    }
                    nowRestoring = 0;
                    dataExist = false;
                    buttonStart.setEnabled(true);
                    isStarted = false;
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
                    deleteButtons();
                    createButtons();
                    if(isStarted) buttonEachLap.setText(getResources().getString(R.string._eachLap));
                    else buttonEachLap.setText(getResources().getString(R.string._str_Check));
                }
            };

            BackButton.setOnClickListener(Signal_Back);
            buttonStart.setOnClickListener(Signal_Start);
            buttonReset.setOnClickListener(Signal_Reset);
            //**********************************************
            return rootView;
        }

        View.OnClickListener Signal_EachLap = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStarted) {
                    for(int i = 0; i < SData.getRunners(); i++) {
                        arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].callOnClick();
                    }
                }
                else {
                    isCheck = !isCheck;
                    if(isCheck) buttonReset.callOnClick();
                    if(isCheck) buttonEachLap.setText(getResources().getString(R.string._str_CheckEnd));
                    else buttonEachLap.setText(getResources().getString(R.string._str_Check));
                    for(int i = 0; i < SData.getFieldY(); i++) {
                        for(int j = 0; j < SData.getFieldX(); j++) {
                            if((i + 1 == SData.getFieldY()) && (SData.runnerNum < i*(SData.getFieldX()) + j+1)) break;
                            arrButtons[i][j].setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };

        final View.OnClickListener Signal_NumeralButton = new View.OnClickListener() {
            public void onClick(View v) {
                //Если запущен секундомер
                if (isCheck)
                {
                    int i = v.getId();
                    arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setVisibility(View.INVISIBLE);
                    return;
                }
                if (isStarted && v.getVisibility() == View.VISIBLE && v.isEnabled()) {
                    //Сделать кнопку невидимой - только при динамичных кнопках
                    int idNum = v.getId();
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
                    arrTiming[idNum] = updateTime;
                    if (SData.getLaps() - runData[idNum].getLapsTaken() == 3) {
                        v.getBackground().setColorFilter(Color.rgb(0xFB, 0xA0, 0x15), PorterDuff.Mode.MULTIPLY);

                    } else if (SData.getLaps() - runData[idNum].getLapsTaken() == 2) {
                        v.getBackground().setColorFilter(Color.rgb(0xFB, 0x10, 0x15), PorterDuff.Mode.MULTIPLY);
                    } else if (SData.getLaps() - runData[idNum].getLapsTaken() == 1) {
                        runData[idNum].passLap(updateTime);
                        int temp = SData.getRunners();
                        for (int i = 0; i < SData.getRunners(); i++) {
                            temp -= arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].isEnabled() ? 0 : 1;
                        }
                        if (temp == 1) {
                            buttonEachLap.setText(getResources().getString(R.string._str_CheckEnd));
                            buttonStart.callOnClick();
                            buttonStart.setEnabled(true);
                            dataExist = true;
                            ViewPager _viewPager = getActivity().findViewById(R.id.container);
                            //runData[idNum].LapsTaken++;
                            _viewPager.setCurrentItem(1, true);
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
                    v.setVisibility(View.INVISIBLE);
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


            //int width = sizeM.x;
            //int height = sizeM.y;

            int numX = SData.getFieldX();
            int numY = SData.getFieldY();
            boolean eachToOther = (numY*numX == SData.getRunners());
            numY += (eachToOther && SData.getRunners() > 1) ? 1 : 0;
            Point sizeM = new Point();
            display.getSize(sizeM);
            int maxWidth = sizeM.x
                    - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
            //int maxWidth = 345;
            int maxHeight = sizeM.y
                        - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    165, getResources().getDisplayMetrics());
            int width= maxWidth / numX;
            int height  = (maxHeight / numY) > 370 ? 370 : (maxHeight / numY);


            if(buttonsCreated) return;
            arrRows = new TableRow[numY + 1];
            arrButtons = new Button[numY][numX];
            buttonsCreated = true;
            for(int i = 0; i < numY; i++){
                arrRows[i] = new TableRow(getActivity());
                tableLayout.addView(arrRows[i]);
                for(int j = 0; j < numX; j++){
                    if((i + 1 == numY) && (SData.runnerNum < i*(numX) + j+1)) break;
                    arrButtons[i][j] = new Button(getActivity());
                    arrButtons[i][j].setPadding(5,5,5,5);
                    arrButtons[i][j].setId(i*(numX) + j);
                    Typeface font_style = Typeface.create("sans-serif", Typeface.NORMAL);
                    arrButtons[i][j].setAllCaps(false);
                    arrButtons[i][j].setTypeface(font_style);
                    arrButtons[i][j].setOnClickListener(Signal_NumeralButton);
                    String s = "" + (runData[i*(numX) + j].getIDNumber()) + "/" + String.valueOf(runData[i*(numX) + j].getLapsTaken());

                    arrButtons[i][j].setLayoutParams(new TableRow.LayoutParams(
                            width,
                            height));
                            //(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics())));

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
            if(SData.getRunners() > 1) {
                if(eachToOther) {
                    buttonEachLap = new Button(getActivity());
                    buttonEachLap.setText(R.string._eachLap);
                    buttonEachLap.setTextSize(5 + 80 * width / maxWidth);
                    buttonEachLap.setPadding(5, 5, 5, 5);
                    buttonEachLap.setLayoutParams(new TableRow.LayoutParams(
                            width,
                            height,
                            //(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()),
                            (float) 105));
                    arrRows[numY] = new TableRow(getActivity());
                    arrRows[numY].addView(buttonEachLap);
                    tableLayout.addView(arrRows[numY]);
                }
                else {
                    float minSizeLap = 80;
                    buttonEachLap = new Button(getActivity());
                    //buttonEachLap.setText(R.string._eachLap);
                    //buttonEachLap.setHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()));

                    // Вычисление размера шрифта

                    Paint testPaint = new Paint();
                    buttonEachLap.setPadding(5, 5, 5, 5);
                    float hi = 80;
                    float lo = 2;
                    arrRows[numY - 1].addView(buttonEachLap);

                    buttonEachLap.setLayoutParams(new TableRow.LayoutParams(
                            width,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            (float) 105));
                    int targetWidth = width - buttonEachLap.getPaddingLeft() - buttonEachLap.getPaddingRight();
                    //int targetWidth = maxWidth - width*(numX - numY*numX + SData.getRunners());
                    final float threshold = 0.5f; // How close we have to be
                    String s = getString(R.string._eachLap);
                    //
                    testPaint.set(buttonEachLap.getPaint());
                    while((hi - lo) > threshold) {
                        float size = (hi+lo)/2;
                        testPaint.setTextSize(size);

                        if((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, testPaint.measureText(
                                getResources().getString(R.string._str_CheckEnd) + getResources().getString(R.string._str_CheckEnd)),
                                getResources().getDisplayMetrics()) >= targetWidth)
                            hi = size; // too big
                        else
                            lo = size; // too small
                    }
                    if(minSizeLap > lo) minSizeLap = lo;

                    SpannableString sStr = new SpannableString(s);
                    sStr.setSpan(new AbsoluteSizeSpan((int)minSizeLap, true), 0, getString(R.string._eachLap).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    buttonEachLap.setText(sStr);
                    buttonEachLap.setTextSize(minSizeLap);
                    //
                }
                buttonEachLap.setOnClickListener(Signal_EachLap);
            }
            //minSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, minSize, getResources().getDisplayMetrics());
            for(int i = 0; i < SData.getRunners(); i++) {
                String s;

                if(SData.getslashLaps()) s = "" + (runData[i / (SData.getFieldX()) * (numX) + i % (SData.getFieldX())].getIDNumber()) + "/" +
                        String.valueOf(runData[i / (SData.getFieldX()) * (numX) + i % (SData.getFieldX())].getLapsTaken());
                else s = "" + (runData[i / (SData.getFieldX()) * (numX) + i % (SData.getFieldX())].getIDNumber());
                arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setTextSize(minSize);
                SpannableString sStr = new SpannableString(s);
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
                //arrButtons[i / (SData.getFieldX())][i % (SData.getFieldX())].setTextSize(minSize);
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
        public View rootView;
        protected ListView listView;
        protected ImageButton butAdd2;
        protected ImageButton buttonAdd;
        protected ImageButton buttonAccept;
        protected ImageButton SpeechBut;
        protected TextView textSpoken;
        protected TextView texterr;
        protected TextView BTAddress1;
        protected TextView BTname1;
        protected EditText editTimeUntil;
        protected TextView editRunners;
        protected EditText editLaps;
        protected ImageButton buttonDeleteText;
        protected ImageButton Button_BluetoothConnect;
        protected TextView BluetoothStatus_text;
        //Radio 1
        protected RadioGroup radioGroup1;
        protected RadioButton radio1;
        //Radio 2
        protected RadioGroup radioGroup2;
        protected RadioButton radio2;
        //Radio 3
        protected RadioGroup radioGroup3;
        protected RadioButton radio3;

        protected EditText editRunnersNames;
        protected CheckBox checkVolumeStart;
        protected CheckBox checkNotifications;

        protected int OverallNumber;
        protected String arrIDNumberSettings[];
        protected String tempArr[];

        @Override
        public void onResume() {
            super.onResume();
            showTable();
            isCheck = false;
            BuiltOnce = true;
            editRunnersNames.setEnabled(startTime==0);
            buttonAccept.setEnabled(startTime==0);
            buttonAdd.setEnabled(startTime==0);
            buttonDeleteText.setEnabled(startTime==0);
            SpeechBut.setEnabled(startTime==0);
            editLaps.setEnabled(startTime==0);
            buttonAdd.setEnabled(startTime==0);
            checkNotifications.setChecked(SData.notification);

            GradientDrawable gD;
            if(SData.styleID == 1) gD = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[] {
                    getResources().getColor(R.color.yellowish_Brown), getResources().getColor(R.color.Brown)});
            else if(SData.styleID == 2) gD = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[] {
                    getResources().getColor(R.color.Whitey_pink), getResources().getColor(R.color.Pink)});
            else {
                Tab2.rootView.findViewById(R.id.activity2layout).setBackgroundColor(getResources().getColor(R.color.design_default_colorE));
                Tab3.rootView.findViewById(R.id.activity3layout).setBackgroundColor(getResources().getColor(R.color.design_default_colorE));
                return;
            }
            Tab2.rootView.findViewById(R.id.activity2layout).setBackgroundDrawable(gD);
            Tab3.rootView.findViewById(R.id.activity3layout).setBackgroundDrawable(gD);
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
                SpeechSaid = SpeechSaid.replace(" и ", ",");
                SpeechSaid = SpeechSaid.replace(" ", ",");
                SpeechSaid = SpeechSaid.replace(",,", ",");
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
            Button_BluetoothConnect = (ImageButton) rootView.findViewById(R.id.blueToothConnect);
            BluetoothStatus_text = (TextView) rootView.findViewById(R.id.bluetoothStatus);
            butAdd2 = (ImageButton) rootView.findViewById(R.id.buttonAdd2);
            SpeechBut = (ImageButton) rootView.findViewById(R.id.Button_speech);
            buttonAdd = (ImageButton) rootView.findViewById(R.id.buttonAdd);
            buttonAccept = (ImageButton)rootView.findViewById(R.id.button_Accept);
            textSpoken = rootView.findViewById(R.id.SpokenText);
            texterr = (TextView) rootView.findViewById(R.id.text_Err);
            BTAddress1 = (TextView) rootView.findViewById(R.id.AddressBT_1);
            BTname1 = (TextView) rootView.findViewById(R.id.NameBT_1);
            editRunners = (TextView) rootView.findViewById(R.id.edit_runners);
            editLaps = (EditText) rootView.findViewById(R.id.edit_laps);
            editRunnersNames = (EditText) rootView.findViewById(R.id.edit_RunnersNames);
            editTimeUntil = (EditText) rootView.findViewById(R.id.edit_TimeUntil);
            buttonDeleteText = (ImageButton) rootView.findViewById(R.id.button_DeleteText);
            radio1 = (RadioButton) rootView.findViewById(R.id.radio_1);
            radioGroup1 = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
            radio2 = (RadioButton) rootView.findViewById(R.id.radioStyle_1);
            radioGroup2 = (RadioGroup) rootView.findViewById(R.id.radioGroup2);
            checkNotifications = (CheckBox) rootView.findViewById(R.id.notifBox);
            radio3 = (RadioButton) rootView.findViewById(R.id.radio_3);
            radioGroup3 = (RadioGroup) rootView.findViewById(R.id.radioGroup3);

            checkVolumeStart = (CheckBox) rootView.findViewById(R.id.check_volumeStart);
            //Получение значений из главного меню
            OverallNumber = SData.getRunners();
            checkVolumeStart.setChecked(SData.OnVolumeStart);
            radioGroup3.check(SData.getslashLaps() ? R.id.radio_3 : R.id.radio_4);
            radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    SData.setSlashLaps((radioGroup3.getCheckedRadioButtonId() == R.id.radio_3) ? true : false);
                }
            });
            editRunners.setText(String.valueOf(OverallNumber));
            radioGroup1.check(SData.getAccuracyFlag() ? R.id.radio_1 : R.id.radio_2);
            radioGroup2.check(SData.styleID == 0 ? R.id.radioStyle_3 : (SData.styleID == 1 ? R.id.radioStyle_1 : R.id.radioStyle_2));
            arrIDNumberSettings = arrIDNumber;
            editLaps.setText(String.valueOf(SData.getLaps()));
            editTimeUntil.setText(String.valueOf(SData.getTimeUntilShown()));

            uuid = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");
            ok_Speech = false;
            checkNotifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkNotifications.isChecked()) SData.notification = true;
                    else SData.notification = false;
                }
            });

            checkVolumeStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkVolumeStart.isChecked()) SData.OnVolumeStart = true;
                    else SData.OnVolumeStart = false;
                }
            });
            radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    SData.styleID = (radioGroup2.getCheckedRadioButtonId() == R.id.radioStyle_3 ? 0 :
                            (radioGroup2.getCheckedRadioButtonId() == R.id.radioStyle_1 ? 1 : 2));

                    GradientDrawable gD;
                    if(SData.styleID == 1) gD = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[] {
                            getResources().getColor(R.color.yellowish_Brown), getResources().getColor(R.color.Brown)});
                    else if(SData.styleID == 2) gD = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[] {
                            getResources().getColor(R.color.Whitey_pink), getResources().getColor(R.color.Pink)});
                    else {
                        Tab1.rootView.findViewById(R.id.activity1Layout).setBackgroundColor(getResources().getColor(R.color.design_default_colorE));
                        Tab2.rootView.findViewById(R.id.activity2layout).setBackgroundColor(getResources().getColor(R.color.design_default_colorE));
                        Tab3.rootView.findViewById(R.id.activity3layout).setBackgroundColor(getResources().getColor(R.color.design_default_colorE));
                        return;
                    }
                    Tab1.rootView.findViewById(R.id.activity1Layout).setBackgroundDrawable(gD);
                    Tab2.rootView.findViewById(R.id.activity2layout).setBackgroundDrawable(gD);
                    Tab3.rootView.findViewById(R.id.activity3layout).setBackgroundDrawable(gD);
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
                        else if (((temp.length() == 1) || (((temp.charAt(0) <= '9') && (temp.charAt(0) > 0)) && (temp.length() == 2))) && !((temp.charAt(0) == '0') && (temp.length() >= 2))) {
                            before = temp;
                        }
                        mFormating = true;
                        if(before == beforeTemp) {
                            editLaps.setText(before);
                            editLaps.setSelection(position > before.length()-1 ? before.length()-1 : position-1);
                        }
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
                    final BluetoothAdapter bluetooth= BluetoothAdapter.getDefaultAdapter();

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
                            String mydeviceaddress= bluetooth.getAddress();
                            String mydevicename= bluetooth.getName();
                            BTAddress1.setText(mydeviceaddress);
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
                                }
                            });

                            //---------------------------------------TODO-------------------------------
                            class AcceptThread extends Thread{
                                private final BluetoothServerSocket mmServerSocket;
                                OutputStream mmOutStream;
                                InputStream mmInStream;
                                public void manageConnectedSocket(BluetoothSocket socket) {

                                }
                                public AcceptThread(){
                                        // используем вспомогательную переменную, которую в дальнейшем
                                        // свяжем с mmServerSocket,
                                        BluetoothServerSocket tmp=null;
                                        try{
                                            // MY_UUID это UUID нашего приложения, это же значение
                                            // используется в клиентском приложении
                                            tmp= bluetooth.listenUsingRfcommWithServiceRecord("BluetoothTimer", uuid);
                                    } catch(IOException e){e.printStackTrace();}
                                    mmServerSocket = tmp;
                                }

                                public void run(){
                                    BluetoothSocket socket;
                                    // ждем пока не произойдет ошибка или не
                                    // будет возвращен сокет
                                    while(true){
                                        try{
                                            socket= mmServerSocket.accept();
                                        } catch(IOException e){
                                            break;
                                        }
                                        // если соединение было подтверждено
                                        if(socket!=null){
                                            // управляем соединением (в отдельном потоке)
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
                                            byte bytes_t = Byte.valueOf("ПРОВЕРКА").byteValue();
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
                                        mmServerSocket.close();
                                    } catch(IOException e){e.printStackTrace();}
                                }
                            }
                            //---------------------------------------TODO-------------------------------
                            AcceptThread threadBluetooth = new AcceptThread();
                            //Handler customHandler_t = new Handler();
                            //customHandler_t.postDelayed(threadBluetooth, 0);

                        }
                        else
                        {
                            status=getResources().getString(R.string._err_BToff);
                            BluetoothStatus_text.setText(status);
                        }

                    }
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

    public static class database extends Fragment {
        protected TextView text_Column1, text_Column2, text_Column3;
        public int int_Column1, int_Column2, int_Column3;
        public View rootView;
        protected TableLayout tableManage;

        @Override

        public void onResume() {
            super.onResume();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            rootView = inflater.inflate(R.layout.activity_database, container, false);
            //Отмена фокусировки на EditText'ы
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            //Предустановка переменных
            arrRowData = new TableRow[101];
            arrTextData = new TextView[101];
            arrTimeData = new TextView[101];
            arrLapsData = new TextView[101];

            int_Column1 = 1;
            int_Column2 = 0;
            int_Column3 = 0;
            //Сохранение элементов экрана
            tableManage = (TableLayout) rootView.findViewById(R.id.tableManage);

            text_Column1 = (TextView)rootView.findViewById(R.id.textColumn1);
            text_Column2 = (TextView)rootView.findViewById(R.id.textColumn2);
            text_Column3 = (TextView)rootView.findViewById(R.id.textColumn3);

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
        }
        sortTemp []S = new sortTemp[SData.getRunners()];
        for(int i = 0; i < SData.getRunners(); i++) {
            S[i] = new sortTemp();
            S[i].col1 = arrIDNumber[i];
            S[i].col2 = arrTiming[i];
            S[i].col3 = Tab1.runData[i].LapsTaken;
        }
        class ComparSort implements Comparator<sortTemp> {
            public int compare(sortTemp first, sortTemp second) {
                if((Tab2.int_Column1 != 0) || (first.col2 == second.col2 && first.col3 == second.col3)){
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
            arrTextData[i].setText(S[i].col1);//TODO test
            arrTextData[i].setPadding(0,5,5,0);
            arrTextData[i].setTextSize(22);
            arrTextData[i].setLayoutParams(new TableRow.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, Tab2.getResources().getDisplayMetrics()),
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            arrRowData[i].addView(arrTextData[i]);
            //Время
            long RunnerTime = S[i].col2;//TODO test
            long secs = ((int)RunnerTime/1000);
            long mins = secs/60;
            secs %= 60;
            long milliseconds = (int)(RunnerTime%1000);
            arrTimeData[i] = new TextView(Tab2.getActivity());
            arrTimeData[i].setLayoutParams(new TableRow.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105, Tab2.getResources().getDisplayMetrics()),
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            arrTimeData[i].setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                                            Tab2.getResources().getDisplayMetrics()),5,5,0);
            if(SData.getAccuracyFlag())
                arrTimeData[i].setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs);
            else {
                arrTimeData[i].setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs +"."+
                        ((milliseconds/10) < 10 ? "0" : "") + (milliseconds/10));
            }
            arrTimeData[i].setTextSize(22);
            arrRowData[i].addView(arrTimeData[i]);
            //Круги
            arrLapsData[i] = new TextView(Tab2.getActivity());
            arrLapsData[i].setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                    Tab2.getResources().getDisplayMetrics()),5,5,0);
            arrLapsData[i].setText(S[i].col3 + "/" + SData.laps);
            arrLapsData[i].setTextSize(22);
            arrRowData[i].addView(arrLapsData[i]);

            TableRow.MarginLayoutParams marginParams1 = new TableRow.MarginLayoutParams(arrRowData[i].getLayoutParams());
            marginParams1.setMargins(0, 0, 0, 0);
            TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(marginParams1);
            arrRowData[i].setLayoutParams(layoutParams1);

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
                bw.write("[Runner ID number" + String.valueOf(i) + "]\n");   // Метки для номеров
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
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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
                    Tab1 = new MainActivity();
                    return Tab1;
                case 1:
                    Tab2 = new database();
                    return Tab2;
                default:
                    Tab3 = new SettingsActivity();
                    return Tab3;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
