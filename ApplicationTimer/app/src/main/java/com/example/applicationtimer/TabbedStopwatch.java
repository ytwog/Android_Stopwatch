package com.example.applicationtimer;

 import android.content.ContextWrapper;
 import android.content.pm.ActivityInfo;
 import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
 import android.graphics.drawable.GradientDrawable;
 import android.os.Handler;
import android.os.SystemClock;
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
import android.view.LayoutInflater;
import android.view.Menu;
 import android.view.View;
import android.view.ViewGroup;

 import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;

 import static java.lang.Math.sqrt;



public class TabbedStopwatch extends AppCompatActivity {
    //Данные фрагментов
    static String currentTime;

    static public class Pair<F, S> {
        private F first;
        private S second;
    }
    static protected Pair<Integer, Long> arrRestore[];
    static protected int nowRestoring;
    static protected float minSize = 80;
    static protected TableRow arrRowData[];
    static protected long arrTiming[];
    static protected ContextWrapper CW ;
    static protected boolean BuiltOnce = false;
    static protected TextView arrTextData[];
    static protected TextView arrTimeData[];
    static protected SettingsActivity Tab3;
    static protected database Tab2;
    static protected MainActivity Tab1;
    static protected boolean buttonsCreated = false;
    static long startTime=0L, timeInMilliseconds=0L, timeSwapBuffer, updateTime=0L;
    static int secs, mins, milliseconds;
    static protected boolean dataExist;
    static protected boolean isStarted = false;
    static protected SettingsData SData;
    static protected String arrIDNumber[];
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
        protected int runnerNum = 10;
        protected int laps = 10;
        protected int numX = 3;
        protected int numY = 4;
        protected int styleID = 0;
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
                                arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].setVisibility(View.VISIBLE);
                            }
                    }
                customHandler.postDelayed(this, 0);
            }
        };
        Handler customHandler = new Handler();

        @Override
        public void onPause() {
            super.onPause();
            currentTime = textTimer.getText().toString();
        }

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
                SData.setSlashLaps(Tab3.checkBoxSlashLaps.isChecked());
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
                    if(isStarted) {
                        //Когда секундомер останавливаем
                        buttonStart.setText(R.string._start_str);
                        buttonStart.getBackground().setColorFilter(Color.rgb(0x3F, 0x51, 0xB5), PorterDuff.Mode.MULTIPLY);
                        timeSwapBuffer += timeInMilliseconds;
                        customHandler.removeCallbacks(updateTimeThread);
                        buttonReset.setEnabled(true);
                    }
                    else {
                        //Когда стартует секундомер
                        if(dataExist) {
                            buttonReset.callOnClick();
                        }
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
                    deleteButtons();
                    createButtons();
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
                for(int i = 0; i < SData.getRunners(); i++) {
                    arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].callOnClick();
                }
            }
        };

        final View.OnClickListener Signal_NumeralButton = new View.OnClickListener() {
            public void onClick(View v) {
                //Если запущен секундомер
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
                            buttonStart.callOnClick();
                            buttonStart.setEnabled(true);
                            dataExist = true;
                            ViewPager _viewPager = getActivity().findViewById(R.id.container);
                            _viewPager.setCurrentItem(1, true);
                        }
                        v.getBackground().setColorFilter(Color.rgb(0xFF, 0xFF, 0xFF), PorterDuff.Mode.MULTIPLY);
                        v.setEnabled(false);
                        runData[idNum].LapsTaken++;
                        if(SData.getslashLaps())
                        {
                            int maxWidth = 345;
                            int everyWidth = maxWidth / SData.getFieldX();
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
                        return;
                    }
                    v.setVisibility(View.INVISIBLE);
                    runData[idNum].setHidden();
                    if(SData.getslashLaps())
                    {
                        int maxWidth = 345;
                        int everyWidth = maxWidth / SData.getFieldX();
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
            numY += (eachToOther && SData.getRunners() > 1) ? 1 : 0;

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int widthDevice = display.getWidth();  // deprecated
            int heightDevice = display.getHeight();
            int maxWidth = 345;
            int maxHeight = 435;
            int everyWidth = maxWidth / numX;
            int everyHeight = (maxHeight / numY) > 370 ? 370 : (maxHeight / numY);
            int height = everyHeight;
            int width = everyWidth;


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
                    //arrButtons[i][j].setMaxLines(1);
                    //arrButtons[i][j].setSingleLine(true);
                    //arrButtons[i][j].setHorizontallyScrolling(false);
                    arrButtons[i][j].setId(i*(numX) + j);
                    Typeface font_style = Typeface.create("sans-serif", Typeface.NORMAL);
                    arrButtons[i][j].setAllCaps(false);
                    arrButtons[i][j].setTypeface(font_style);
                    arrButtons[i][j].setOnClickListener(Signal_NumeralButton);
                    String s = "" + (runData[i*(numX) + j].getIDNumber()) + "/" + String.valueOf(runData[i*(numX) + j].getLapsTaken());

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

                        if(testPaint.measureText(s) >= targetWidth)
                            hi = size; // too big
                        else
                            lo = size; // too small
                    }
                    if(minSize > lo) minSize = lo;
                    arrRows[i].addView(arrButtons[i][j]);

                    arrButtons[i][j].setLayoutParams(new TableRow.LayoutParams(
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics()),
                            (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics())));
                }
            }
            if(SData.getRunners() > 1) {
                buttonEachLap = new Button(getActivity());
                buttonEachLap.setText(R.string._eachLap);
                buttonEachLap.setLayoutParams(new TableRow.LayoutParams(
                        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics()),
                        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()),
                        (float) 105));
                buttonEachLap.setTextSize(5 + 40 * everyWidth / maxWidth);
                if (eachToOther) {
                    arrRows[numY] = new TableRow(getActivity());
                    arrRows[numY].addView(buttonEachLap);
                    tableLayout.addView(arrRows[numY]);
                } else {
                    arrRows[numY - 1].addView(buttonEachLap);
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
        public View rootView;
        protected ImageButton buttonAccept;
        protected ImageButton buttonDecline;
        protected TextView texterr;
        protected EditText editTimeUntil;
        protected TextView editRunners;
        protected EditText editLaps;
        protected ImageButton buttonDeleteText;
        //Radio 1
        protected RadioGroup radioGroup1;
        protected RadioButton radio1;
        //Radio 2
        protected RadioGroup radioGroup2;
        protected RadioButton radio2;

        protected EditText editRunnersNames;
        protected CheckBox checkBoxSlashLaps;

        protected int OverallNumber;
        protected String arrIDNumberSettings[];
        protected String tempArr[];

        @Override
        public void onResume() {
            super.onResume();
            showTable();
            BuiltOnce = true;
            editRunnersNames.setEnabled(startTime==0);
            buttonAccept.setEnabled(startTime==0);
            buttonDecline.setEnabled(startTime==0);
            buttonDeleteText.setEnabled(startTime==0);
            editLaps.setEnabled(startTime==0);

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

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            rootView = inflater.inflate(R.layout.layoutsettings, container, false);

            //Сохранение элементов окна
            buttonAccept = (ImageButton)rootView.findViewById(R.id.button_Accept);
            buttonDecline = (ImageButton)rootView.findViewById(R.id.button_Decline);
            texterr = (TextView) rootView.findViewById(R.id.text_Err);
            editRunners = (TextView) rootView.findViewById(R.id.edit_runners);
            editLaps = (EditText) rootView.findViewById(R.id.edit_laps);
            editRunnersNames = (EditText) rootView.findViewById(R.id.edit_RunnersNames);
            editTimeUntil = (EditText) rootView.findViewById(R.id.edit_TimeUntil);
            buttonDeleteText = (ImageButton) rootView.findViewById(R.id.button_DeleteText);
            radio1 = (RadioButton) rootView.findViewById(R.id.radio_1);
            radioGroup1 = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
            radio2 = (RadioButton) rootView.findViewById(R.id.radioStyle_1);
            radioGroup2 = (RadioGroup) rootView.findViewById(R.id.radioGroup2);
            checkBoxSlashLaps = (CheckBox) rootView.findViewById(R.id.checkLapsSlash);
            //Получение значений из главного меню
            OverallNumber = SData.getRunners();
            checkBoxSlashLaps.setChecked(SData.getslashLaps());
            editRunners.setText(String.valueOf(OverallNumber));
            radioGroup1.check(SData.getAccuracyFlag() ? R.id.radio_1 : R.id.radio_2);
            radioGroup2.check(SData.styleID == 0 ? R.id.radioStyle_3 : (SData.styleID == 1 ? R.id.radioStyle_1 : R.id.radioStyle_2));
            arrIDNumberSettings = arrIDNumber;
            editLaps.setText(String.valueOf(SData.getLaps()));
            editTimeUntil.setText(String.valueOf(SData.getTimeUntilShown()));

            //Установка ограничений на ввод числа кругов
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
            View.OnFocusChangeListener FocusUpdated = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (getNames()) {
                        int Runners = Integer.parseInt(editRunners.getText().toString());
                        if (Runners != 0) SData.setRunners(Runners);
                        arrIDNumber = arrIDNumberSettings;
                    }
                }
            };
            //editRunnersNames.setOnFocusChangeListener(FocusUpdated);
            //***********-Установка слотов на кнопки***************
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

            View.OnClickListener Signal_ClearInput = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editRunnersNames.setText("");
                }
            };
            buttonDeleteText.setOnClickListener(Signal_ClearInput);
            buttonDecline.setOnClickListener(Signal_Decline);
            buttonAccept.setOnClickListener(Signal_Accept);
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
            tempArr = new String[OverallNumber];
            String got = "";
            int count1 = 0, len = 0;
            boolean fineTemp = false;
            for(int i = 0; i < str.length(); i++) {
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
            if(got != null && got != "" && fineTemp)
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
                    texterr.setText("Введите номера участников");
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

                if(result == 1) texterr.setText("Введите номера участников");
                else if(result == 2) texterr.setText("Принимаются без пробелов только номера, разделенные запятыми");
                else if(result == 3) texterr.setText("Вместо числа был обнаружен иной символ");
                else if(result == 4) texterr.setText("Слишком длинный номер участника");
                else                 texterr.setText("Обнаружена ошибка ввода");

                return false;
            }
        }

    }
    //----------------------------------------------------------------------------------------------\\
    //-------------------------------------database fragment----------------------------------------\\

    public static class database extends Fragment {
        public View rootView;
        protected TableLayout tableManage;

        @Override

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            rootView = inflater.inflate(R.layout.activity_database, container, false);
            //Отмена фокусировки на EditText'ы
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            //Предустановка переменных
            arrRowData = new TableRow[101];
            arrTextData = new TextView[101];
            arrTimeData = new TextView[101];

            //Сохранение элементов экрана
            tableManage = (TableLayout) rootView.findViewById(R.id.tableManage);

            //Получение значений из главного меню
            //*******************************************************

            //Заполнение таблицы информацией
            showTable();
            return rootView;
        }

        //Заполнение таблицы информацией
    }
    //-----------------------------------------------------------------------------------------------------\\

    static protected void showTable(){

        Tab2.tableManage.removeAllViews();

        //
        for(int i = 0; i < SData.getRunners(); i++) {
            arrRowData[i] = new TableRow(Tab2.getActivity());
            arrRowData[i].setX((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Tab2.getResources().getDisplayMetrics()));
            Tab2.tableManage.addView(arrRowData[i]);
            arrTextData[i] = new TextView(Tab2.getActivity());
            arrTextData[i].setText(arrIDNumber[i]);
            arrTextData[i].setPadding(5,5,5,0);
            arrTextData[i].setTextSize(26);
            arrTextData[i].setLayoutParams(new TableRow.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, Tab2.getResources().getDisplayMetrics()),
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            arrRowData[i].addView(arrTextData[i]);
            long RunnerTime = arrTiming[i];
            long secs = ((int)RunnerTime/1000);
            long mins = secs/60;
            secs %= 60;
            long milliseconds = (int)(RunnerTime%1000);
            arrTimeData[i] = new TextView(Tab2.getActivity());
            arrTimeData[i].setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                                            Tab2.getResources().getDisplayMetrics()),5,5,0);
            if(SData.getAccuracyFlag())
                arrTimeData[i].setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs);
            else {
                arrTimeData[i].setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs +"."+
                        ((milliseconds/10) < 10 ? "0" : "") + (milliseconds/10) );
            }
            arrTimeData[i].setTextSize(26);
            arrRowData[i].addView(arrTimeData[i]);

            TableRow.MarginLayoutParams marginParams1 = new TableRow.MarginLayoutParams(arrRowData[i].getLayoutParams());
            marginParams1.setMargins(5, 0, 0, 0);
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
            bw.write("[versionNumber]\n");                                  // Метка 1
            bw.write("Save_ver1\n");
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
            for(int i = 0; i < SData.getRunners(); i++) {
                bw.write("[Runner ID number" + String.valueOf(i) + "\n");   // Метки для номеров
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
                        if(!str.equals("Save_ver1")) return SData;
                        break;
                    case 4:
                        if(Integer.parseInt(str) > 0 && Integer.parseInt(str) < 101) SData.setRunners(Integer.parseInt(str));
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
                }
                if(stage > 17 && stage % 2 == 0) {
                    arrIDNumber[(stage / 2) - 9] = str;
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
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tabbed_stopwatch);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
