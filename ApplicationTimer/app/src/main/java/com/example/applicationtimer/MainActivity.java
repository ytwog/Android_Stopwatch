package com.example.applicationtimer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Vector;

import static android.app.Activity.RESULT_CANCELED;
import static java.lang.Math.sqrt;

public class MainActivity extends Fragment {

    public View rootView;
    protected Button buttonStart;
    protected Button buttonReset;
    protected Button buttonManage;
    protected Button buttonEachLap;
    protected ImageButton buttonSettings;
    protected TextView textTimer;
    protected int ArrayID[];
    protected TableLayout tableLayout;
    protected boolean buttonsCreated = false;
    long startTime=0L, timeInMilliseconds=0L, timeSwapBuffer=0L, updateTime=0L;
    int secs, mins, milliseconds;
    protected Button arrButtons[][];
    protected boolean dataExist;
    protected TableRow arrRows[];
    protected SettingsData SData;
    protected boolean isStarted = false;
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
                textTimer.setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs +":"+
                        ((milliseconds/10) < 10 ? "0" : "") + (milliseconds/10) );
            }

            //Проход по всем кнопкам
            if(isStarted)
            for(int i = 0; i < SData.getRunners(); i++){
                //Проверка для появления кнопки
                if(arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].getVisibility() == View.INVISIBLE)
                if(runData[i].passSecond() == false){
                    arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].setVisibility(View.VISIBLE);
                    //arrButtons[i / (SData.getFieldX())][i% (SData.getFieldX())].setText(String.valueOf(runData[i].timeHidden));
                }
            }
            customHandler.postDelayed(this, 0);
        }
    };
    Handler customHandler = new Handler();
    //Класс бегущего
    protected class RunnerData{
        protected int LapsTaken = 0;
        protected int IDNumber = 0;
        protected int arrMinutes[] = new int[100];
        protected int arrSeconds[] = new int[100];
        protected int arrMilisnd[] = new int[100];
        protected long lastTime = 0;
        protected boolean isHidden = false;
        protected long timeHidden = 0;
        protected long startTime1 = 0;
        //setters
        void setHidden() {isHidden = true; startTime1 = updateTime; LapsTaken++;}
        void setIDNumber(int ID){ IDNumber = ID; }
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
        int getIDNumber(){ return IDNumber; }
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
    protected class SettingsData {
        protected int runnerNum = 30;
        protected int laps = 10;
        protected int numX = 3;
        protected int numY = 4;
        protected boolean staticButtons = true;
        protected boolean accuracyFlag  = true;
        protected int timeUntilSHown = 10;

        SettingsData(){}

        SettingsData(int _runnerNumber) {
            this.setRunners(_runnerNumber);
            setFieldX(numX);
        }

        // Setters
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


    @Override

    public void onResume() {
        super.onResume();
        setChronology();
        textTimer.setText(SData.getAccuracyFlag() ? "00:00" : "00:00.00");
        //Обновление кнопок
        deleteButtons();
        createButtons();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_main, container, false);
        //Предустановка переменных
        dataExist = false;
        isStarted = false;
        ArrayID = new int[101];
        for(int i = 0; i < 99; i++) {
            ArrayID[i] = i+1;
        }
        for(int i = 0; i < 101; i++)
            runData[i] = new RunnerData();
        //Загрузка настроек
        SData = FileRead();

        setChronology();
        //Сохранение элементов окна
        buttonManage = (Button) rootView.findViewById(R.id.Button_Manage);
        buttonStart = (Button) rootView.findViewById(R.id.Button_Start);
        buttonReset = (Button) rootView.findViewById(R.id.button_Reset);
        textTimer = (TextView)rootView.findViewById(R.id.text_Timer);
        buttonSettings = (ImageButton) rootView.findViewById(R.id.button_Settings);
        tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
        textTimer.setText(SData.getAccuracyFlag() ? "00:00" : "00:00.00");

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
                    buttonStart.setBackgroundColor(Color.rgb(0x3F, 0x51, 0xB5));
                    timeSwapBuffer += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimeThread);
                    buttonManage.setEnabled(true);
                }
                else {
                    //Когда стартует секундомер
                    if(dataExist) {
                        buttonReset.callOnClick();
                    }
                    buttonStart.setBackgroundColor(Color.rgb(0xE5, 0x21, 0x3F));
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimeThread, 0);
                    buttonSettings.setEnabled(false);
                    buttonManage.setEnabled(false);
                    buttonStart.setText(R.string._stop_str);
                }
                isStarted = !isStarted;
            }
        };
        //Кнопка Сброса
        View.OnClickListener Signal_Reset = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataExist = false;
                buttonStart.setEnabled(true);
                isStarted = false;
                buttonStart.setBackgroundColor(Color.rgb(0x3F, 0x51, 0xB5));
                buttonStart.setText(R.string._start_str);
                timeSwapBuffer += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimeThread);
                startTime = timeInMilliseconds = timeSwapBuffer = updateTime = 0L;
                textTimer.setText(SData.getAccuracyFlag() ? "00:00" : "00:00.00");
                for(int i = 0; i < 99; i++) {
                    runData[i].reset();
                }
                buttonSettings.setEnabled(true);
                buttonManage.setEnabled(true);
                deleteButtons();
                createButtons();

            }
        };

        //Кнопка перехода в настройки
        View.OnClickListener Signal_Settings = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Settingsintent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                //Передача значений в activity
                Settingsintent.putExtra("Settings1", SData.getRunners());
                Settingsintent.putExtra("Settings2", SData.getStaticButtons());
                Settingsintent.putExtra("Settings3", SData.getLaps());
                Settingsintent.putExtra("Settings4", SData.getAccuracyFlag());
                Settingsintent.putExtra("Settings5", SData.getTimeUntilShown());
                Settingsintent.putExtra("Settings6", ArrayID);
                startActivityForResult(Settingsintent, 1);
            }
        };
        //Кнопка перехода в управление
        View.OnClickListener Signal_Manage = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < SData.getRunners(); i++) {
                    ArrayID[i] = runData[i].getIDNumber();
                }
                long TempTime[] = new long[101];
                for(int i = 0; i < SData.getRunners(); i++) {
                    TempTime[i] = runData[i].lastTime;
                }

                //-----------------------!------------------------\\
                //Intent TabbedIntent = new Intent(MainActivity.this, TabbedStopwatch.class);
                //startActivity(TabbedIntent);
                //
                //-----------------------!------------------------\\
                Intent Manageintent = new Intent(getActivity().getApplicationContext(), database.class);
                //Передача значений в activity
                Manageintent.putExtra("ManageNumber", SData.getRunners());
                Manageintent.putExtra("ManageTime", TempTime);
                Manageintent.putExtra("ManageArray", ArrayID);
                Manageintent.putExtra("ManageAccuracy", SData.getAccuracyFlag());
                //Manageintent.putExtra("ManageTime", ArrayTime);
                startActivityForResult(Manageintent, 2);

            }
        };

        buttonStart.setOnClickListener(Signal_Start);
        buttonReset.setOnClickListener(Signal_Reset);
        buttonSettings.setOnClickListener(Signal_Settings);
        buttonManage.setOnClickListener(Signal_Manage);
        //**********************************************
        return rootView;
    }

    //Получение значений из activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Проверка на нажатие отмены
        if (data == null || resultCode == RESULT_CANCELED) {
            return;
        }

        if(requestCode == 1) {
            //Значения из настроек
            int tempNumb = SData.getRunners();
            SData.setTimeUntilSHown(data.getIntExtra("timeUntil", 10000));
            SData.setRunners(data.getIntExtra("runnersNumber", 30));
            SData.setLaps(data.getIntExtra("lapsNumber", 10));
            SData.setStaticButtons(data.getBooleanExtra("staticFlag", true));
            SData.setAccuracyFlag(data.getBooleanExtra("accuracy", true));
            ArrayID = data.getIntArrayExtra("runnersIDs");
            textTimer.setText(SData.getAccuracyFlag() ? "00:00" : "00:00.000");
            //SData.setInfoShown(data.getBooleanExtra("infoFlag", true));
            //Обновление настроек
            /* old version
            * if(tempNumb < SData.getRunners()) {
            *
            * int TempArray[] = new int[SData.getRunners()];
            * for(int i = 0; i < tempNumb; i++) {
            *     TempArray[i] = ArrayID[i];
            * }
            * for(int i = tempNumb; i < SData.getRunners(); i++) {
            *    TempArray[i] = i+1;
            * }
            * ArrayID = TempArray;
            *}
            */

        } else {
            //Значения из управления

        }
        setChronology();
        textTimer.setText(SData.getAccuracyFlag() ? "00:00" : "00:00.00");
        FilePrint(SData);
        //Обновление кнопок
        deleteButtons();
        createButtons();

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
            //  for(int i = 0; i < SData.getRunners(); i++) {
            //      arrRows[i].removeView(v);
            //  }
            //Если запущен секундомер
            if (isStarted && v.getVisibility() == View.VISIBLE && v.isEnabled()) {
                //Сделать кнопку невидимой - только при динамичных кнопках
                int idNum = v.getId();
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
                        buttonSettings.setEnabled(true);
                        buttonStart.setEnabled(true);
                        dataExist = true;
                        buttonManage.callOnClick();
                    }
                    v.getBackground().setColorFilter(Color.rgb(0xFF, 0xFF, 0xFF), PorterDuff.Mode.MULTIPLY);
                    v.setEnabled(false);
                    return;
                }
                if (SData.getStaticButtons() == false) {
                    v.setVisibility(View.INVISIBLE);
                }
                runData[idNum].setHidden();
                runData[idNum].setTime(idNum, mins, secs, milliseconds);

            }
        }
    };

    //Сохранение настроек
    protected void FilePrint(SettingsData SData) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    getActivity().openFileOutput("config.cfg", getActivity().MODE_PRIVATE)));
            // пишем данные
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
                    getActivity().openFileInput("config.cfg")));
            String str = "";
            // читаем содержимое
            int stage = 0;
            while ((str = br.readLine()) != null) {
                Log.d("State", str);
                stage++;
                switch (stage) {
                    case 2:
                        if(Integer.parseInt(str) > 0 && Integer.parseInt(str) < 101) SData.setRunners(Integer.parseInt(str));
                        break;
                    case 4:
                        if(str.equals("false")) SData.setStaticButtons(false);
                        else               SData.setStaticButtons(true);
                        break;
                    case 6:
                        if(Integer.parseInt(str) > 0 && Integer.parseInt(str) < 100) SData.setLaps(Integer.parseInt(str));
                        break;
                    case 8:
                        if(str.equals("false")) SData.setAccuracyFlag(false);
                        else               SData.setAccuracyFlag(true);
                        break;
                    case 10:
                        if(Integer.parseInt(str) > 0 && Integer.parseInt(str) < 100) SData.setTimeUntilSHown(Integer.parseInt(str));
                        break;
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
    //Установка массива номеров бегущих
    protected void setChronology(){
        int num = SData.getRunners();
        for(int i = 0; i < num; i++){
            runData[i].setIDNumber(ArrayID[i]);
        }
    }

    //Создание кнопок
    protected void createButtons(){
        int numX = SData.getFieldX();
        int numY = SData.getFieldY();
        boolean eachToOther = (numY*numX == SData.getRunners());
        numY += (eachToOther && SData.getRunners() > 1) ? 1 : 0;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int widthDevice = display.getWidth();  // deprecated
        int heightDevice = display.getHeight();
        int maxWidth = 345;
        int maxHeight = 370;
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
            //arrRows[i].setY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
            tableLayout.addView(arrRows[i]);
            for(int j = 0; j < numX; j++){
                if((i + 1 == numY) && (SData.runnerNum < i*(numX) + j+1)) break;
                arrButtons[i][j] = new Button(getActivity());
                arrButtons[i][j].setPadding(5,5,5,5);
                arrButtons[i][j].setId(i*(numX) + j);
                Typeface font_style = Typeface.create("sans-serif", Typeface.NORMAL);
                arrButtons[i][j].setTypeface(font_style);
                arrButtons[i][j].setOnClickListener(Signal_NumeralButton);
                arrButtons[i][j].setText("" + (runData[i*(numX) + j].getIDNumber()));
                arrButtons[i][j].setTextSize(5 + 40*everyWidth/maxWidth);
                arrRows[i].addView(arrButtons[i][j]);

                arrButtons[i][j].setLayoutParams(new TableRow.LayoutParams(
                        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics()),
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
    }

    //Удаление кнопок
    protected void deleteButtons(){
        buttonsCreated = false;
        arrButtons = null;
        arrRows = null;
        tableLayout.removeAllViewsInLayout();
    }
}