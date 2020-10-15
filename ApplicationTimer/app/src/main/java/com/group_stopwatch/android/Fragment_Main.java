package com.group_stopwatch.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class Fragment_Main extends TabbedStopwatch {
    public static class MainActivity extends Fragment {
        public static final int VOICE_RECOGNITION_REQUEST_CODE = 4300;
        protected int eachShown;
        protected float sp1, sp2;
        protected LayoutInflater LI;
        public    View rootView;
        protected Button buttonStart, buttonReset, buttonEachLap, arrButtons[][];
        protected TextView textTimer, textSpoken, textUpc[] = new TextView[101];
        protected TableLayout tableLayout;
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
            FilePrint(SData);
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
                        if(currentTime.equals("00:00")) updateTime = 0;
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

        public boolean licenceCheck() {
            if(activateTimes == 0)
                return false;
            else {
                activateTimes -= ((activateTimes==-1) ? 0 : 1);
                FilePrintLicence(activateTimes);
                return true;
            }

        }


        public void UpcomingSet(int value) {
            int prev = DefUpcoming[value];
            for(int i = 0; i < SData.getRunners(); i++) {
                if(i == value) {
                    DefUpcoming[i] = SData.getRunners() - 1;
                }
                else if(DefUpcoming[i] > prev) {
                    DefUpcoming[i]--;
                }
                textUpc[DefUpcoming[i]].setText(UpcomText[i]);
            }
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
                Log.d("AAA", Locale.getDefault().getLanguage());
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
                            if (!licenceCheck()) return;
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
                    eachShown += SData.getRunners();
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
                            UpcomingReset();
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
                            UpcomingReset();
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
                            RunParam1.append("," + textSpoken.getText());
                            RunButton1.callOnClick();
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
            eachShown = 0;
            if(buttonEachLap != null)
                buttonEachLap.setVisibility(View.VISIBLE);
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
                    eachShown--;
                    if(eachShown < 0)
                        buttonEachLap.setVisibility(View.INVISIBLE);
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
                            String finishedOrLeft;
                            if(SData.getFinishedOrLeft())
                                finishedOrLeft = String.valueOf(runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken());
                            else
                                finishedOrLeft = String.valueOf(SData.laps - runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken());
                            String s = "" + (runData[(idNum / (SData.getFieldX()))*(SData.getFieldX()) +
                                    idNum % (SData.getFieldX())].getIDNumber()) + "/" +
                                    finishedOrLeft;
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
                        String finishedOrLeft;
                        if(SData.getFinishedOrLeft())
                            finishedOrLeft = String.valueOf(runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken());
                        else
                            finishedOrLeft = String.valueOf(SData.laps - runData[idNum / (SData.getFieldX())*(SData.getFieldX()) + idNum % (SData.getFieldX())].getLapsTaken());
                        String s = "" + (runData[(idNum / (SData.getFieldX()))*(SData.getFieldX()) +
                                idNum % (SData.getFieldX())].getIDNumber()) + "/" +
                                finishedOrLeft;
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
                    - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
            maxWidth -= (SData.formMain ? toDP(60) : 0);
            int maxHeight = sizeM.y
                    - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    165, getResources().getDisplayMetrics());
            int width= maxWidth / numX;
            int height  = (maxHeight / numY) > 370 ? 370 : (maxHeight / numY);
            buttonEachLap = new Button(getActivity());
            if(SData.formMain) {
                ConstraintLayout.LayoutParams LP = (ConstraintLayout.LayoutParams) tableLayout.getLayoutParams();
                LP.width = maxWidth;
                tableLayout.setLayoutParams(LP);
            }


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


            Paint testPaint = new Paint();
            buttonEachLap.setPadding(5, 5, 5, 5);
            float hi = 80;
            float lo = 2;
            arrRows[numY - 2 + (eachToOther ? 1 : 0)].addView(buttonEachLap);
            if(eachToOther) buttonEachLap.setLayoutParams(new TableRow.LayoutParams(width, height, (float) 105));
            else buttonEachLap.setLayoutParams(new TableRow.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT, (float) 105));

            //-------------------------------------------------------------------------------------------START CHECK

            int targetWidth = (width - buttonEachLap.getPaddingLeft() - buttonEachLap.getPaddingRight());
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
                String finishedOrLeft;
                if(SData.getFinishedOrLeft())
                    finishedOrLeft = String.valueOf(runData[i / (SData.getFieldX()) * (numX) + i % (SData.getFieldX())].getLapsTaken());
                else
                    finishedOrLeft = String.valueOf(SData.laps - runData[i / (SData.getFieldX()) * (numX) + i % (SData.getFieldX())].getLapsTaken());

                if(SData.getslashLaps()) s = "" + (runData[i / (SData.getFieldX()) * (numX) + i % (SData.getFieldX())].getIDNumber()) + "/" + finishedOrLeft;
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
            if(!BuiltOnce) UpcomingReset();
            LinearUp.removeAllViewsInLayout();
            for(int i = 0; i < SData.getRunners(); i++) {
                textUpc[i] = new TextView(getActivity());
                textUpc[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toDP(30)));
                textUpc[i].setTextSize(20);
                textUpc[i].setTextColor(getResources().getColor(android.R.color.black));
                textUpc[i].setText(UpcomText[i]);
                LinearUp.addView(textUpc[i]);
            }

            buttonEachLap.setVisibility(eachShown >= 0 ? View.VISIBLE : View.INVISIBLE);
        }

        //Удаление кнопок
        protected void deleteButtons(){
            buttonsCreated = false;
            arrButtons = null;
            arrRows = null;
            tableLayout.removeAllViewsInLayout();
        }
    }
}
