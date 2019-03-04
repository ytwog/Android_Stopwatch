package com.example.applicationtimer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import java.util.*;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.sql.Time;

public class SettingsActivity extends Fragment {

    public View rootView;
    protected Button buttonAccept;
    protected Button buttonDecline;
    protected TextView textStatic;
    protected TextView texterr;
    protected EditText editTimeUntil;
    protected EditText editRunners;
    protected EditText editLaps;
    protected ImageButton buttonDeleteText;
    protected CheckBox checkStatic;
    protected RadioButton radio1;
    protected RadioGroup radioGroup1;
    protected EditText editRunnersNames;

    protected int OverallNumber;
    protected int arrIDNumber[];
    protected int tempArr[];

    @Override


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.layoutsettings, container, false);

        //Сохранение элементов окна
        buttonAccept = (Button)rootView.findViewById(R.id.button_Accept);
        buttonDecline = (Button)rootView.findViewById(R.id.button_Decline);
        textStatic = (TextView) rootView.findViewById(R.id.text_Static);
        texterr = (TextView) rootView.findViewById(R.id.text_Err);
        checkStatic = (CheckBox) rootView.findViewById(R.id.check_Static);
        editRunners = (EditText) rootView.findViewById(R.id.edit_runners);
        editLaps = (EditText) rootView.findViewById(R.id.edit_laps);
        editRunnersNames = (EditText) rootView.findViewById(R.id.edit_RunnersNames);
        editTimeUntil = (EditText) rootView.findViewById(R.id.edit_TimeUntil);
        buttonDeleteText = (ImageButton) rootView.findViewById(R.id.button_DeleteText);
        radio1 = (RadioButton) rootView.findViewById(R.id.radio_1);
        radioGroup1 = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
        //Получение значений из главного меню
        OverallNumber = 30;
        editRunners.setText(String.valueOf(OverallNumber));
        checkStatic.setChecked(true);
        editLaps.setText(String.valueOf(10));
        radioGroup1.check(true ? R.id.radio_1 : R.id.radio_2);
        editTimeUntil.setText(String.valueOf(10));
        arrIDNumber = new int[101];//getIntent().getIntArrayExtra("Settings6");
        //Установка ограничений на ввод числа бегущих
        editRunners.addTextChangedListener(new TextWatcher() {
            private String before;
            private boolean mFormating;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before = editRunners.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int position = editRunners.getSelectionEnd();

                if(editRunners.getText().toString().length() == 0) {
                    editRunners.setText("0");
                    editRunners.setSelection(1);
                }
                else if(!mFormating) {
                    String temp = editRunners.getText().toString();
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
                        editRunners.setText(before);
                        editRunners.setSelection(position > before.length()-1 ? before.length()-1 : position-1);
                    }
                }
                mFormating = false;
            }
        });
        //Установка ограничений на ввод числа кругов
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
        editRunners.setKeyListener(DigitsKeyListener.getInstance(false, false));
        editLaps.setKeyListener(DigitsKeyListener.getInstance(false, false));
        editTimeUntil.setKeyListener(DigitsKeyListener.getInstance(false, false));

        String rewriteText = "";
        for(int i = 0; i < OverallNumber; i++) {
            rewriteText += String.valueOf(arrIDNumber[i]);
            if(i + 1 != OverallNumber) rewriteText += ",";
        }
        editRunnersNames.setText(rewriteText);
        View.OnFocusChangeListener FocusUpdated = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getNames();
            }
        };
        editRunnersNames.setOnFocusChangeListener(FocusUpdated);
        View.OnFocusChangeListener FocusUpdated2 = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setRunners();
            }
        };
        editRunners.setOnFocusChangeListener(FocusUpdated2);
        //***********-Установка слотов на кнопки***************
        //Кнопка отмены
        View.OnClickListener Signal_Decline = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                getActivity().setResult(getActivity().RESULT_CANCELED, returnIntent);
                getActivity().finish();
            }
        };
        //Кнопка принять
        View.OnClickListener Signal_Accept = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Laps = Integer.parseInt(editLaps.getText().toString());
                int TimeUnt = Integer.parseInt(editTimeUntil.getText().toString());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("staticFlag", checkStatic.isChecked());
                if (TimeUnt != 0) returnIntent.putExtra("timeUntil", TimeUnt);
                if (Laps != 0) returnIntent.putExtra("lapsNumber", Laps);
                if (getNames()) {
                    int Runners = Integer.parseInt(editRunners.getText().toString());
                    if (Runners != 0) returnIntent.putExtra("runnersNumber", Runners);
                    returnIntent.putExtra("runnersIDs", arrIDNumber);
                    getActivity().findViewById(R.id.radioGroup1);
                    returnIntent.putExtra("accuracy", (radioGroup1.getCheckedRadioButtonId() == R.id.radio_1) ? true : false);
                    getActivity().setResult(getActivity().RESULT_OK, returnIntent);
                    getActivity().finish();
                }
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
            int TempArray[] = new int[toSet];
            for (int i = 0; i < OverallNumber; i++) {
                TempArray[i] = arrIDNumber[i];
            }
            for (int i = OverallNumber; i < toSet; i++) {
                TempArray[i] = i + 1;
            }
            OverallNumber = toSet;
            arrIDNumber = TempArray;
            Arrays.sort(arrIDNumber);
        }
        else{
            int TempArray[] = new int[toSet];
            for (int i = 0; i < toSet; i++) {
                TempArray[i] = arrIDNumber[i];
            }
            OverallNumber = toSet;
            arrIDNumber = TempArray;
            Arrays.sort(arrIDNumber);
        }
        for(int i = 0; i < OverallNumber; i++) {
            rewriteText += String.valueOf(arrIDNumber[i]);
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
        for(int i = 0; i < str.length(); i++) {
            boolean fineTemp = false;
            if(str.charAt(i) == ',') OverallNumber++;
            for(int j = 0; j < 11; j++) {
                if(str.charAt(i) == acceptable.charAt(j)) fineTemp = true;
            }
            if(fineTemp == false) result = 2; //Ошибка: неверный символ
        }
        tempArr = new int[OverallNumber];
        int got = 0, count1 = 0, len = 0;
        for(int i = 0; i < str.length(); i++) {
            //if(str.charAt(i) == ',' && len == 0) result = 3;// Ошибка: ожидалось число, получен иной символ
            if(str.charAt(i) == ',') {
                tempArr[count1] = got;
                count1++;
                got = 0;
                len = 0;
            }
            else {
                len++;
                got = got*10 + str.charAt(i) - 48;
            }
            if(len > 9) result = 4;// Ошибка: слишком длинный номер участника (>=10^10)
        }
        tempArr[count1] = got;
        //result = (str.charAt(str.length() - 1) == ',' ? 3 : result);
        arrIDNumber = tempArr;

        //if(OverallNumber < Runners) for(int i = OverallNumber; i < Runners; i++) arrIDNumber[i] = i+1;
        if(result == 0) {
            Arrays.sort(arrIDNumber);
            for(int i = 1; i < OverallNumber; i++) {
                if(arrIDNumber[i] == arrIDNumber[i - 1]) {
                    arrIDNumber[i - 1] = 0;
                }
            }
            Arrays.sort(arrIDNumber);
            int tempNumber = OverallNumber;
            String rewriteText = "";
            result = 0;
            for(int i = 0; i < tempNumber; i++) {
                if(arrIDNumber[i] == 0) {
                    OverallNumber--;
                    continue;
                }
                if(result != 0) rewriteText += ",";
                result++;
                rewriteText += String.valueOf(arrIDNumber[i]);
            }
            if(result == 0) {
                texterr.setText("Введите номера участников");
                return false;
            }
            result = 0;
            if(tempNumber - OverallNumber != 0) {
                tempArr = new int[OverallNumber];
                for(int i = 0; i < tempNumber; i++) {
                    if(arrIDNumber[i] != 0) {
                        tempArr[result] = arrIDNumber[i];
                        result++;
                    }
                }
                arrIDNumber = tempArr;
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


