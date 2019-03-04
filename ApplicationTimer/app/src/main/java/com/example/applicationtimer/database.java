package com.example.applicationtimer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class database extends AppCompatActivity {

    protected TableLayout tableManage;
    protected Button buttonBackMang;
    protected Button buttonBackCanc;
    protected int OverallNumber;
    protected int arrIDNumber[];
    protected long arrTiming[];
    protected TableRow arrRow[];
    protected TextView arrText[];
    protected TextView arrTime[];
    protected int tempArr[];
    protected int Overall;
    protected int shownBefore;
    protected boolean accuracyFlag;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Отмена фокусировки на EditText'ы
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Предустановка переменных
        shownBefore = 0;
        arrRow = new TableRow[101];
        arrText = new TextView[101];
        arrTime = new TextView[101];

        //Сохранение элементов экрана
        tableManage = (TableLayout) findViewById(R.id.tableManage);
        buttonBackMang = (Button) findViewById(R.id.buttonBackManage);
        buttonBackCanc = (Button) findViewById(R.id.buttonCancel);

        //Получение значений из главного меню
        OverallNumber = getIntent().getIntExtra("ManageNumber", 30);
        arrTiming = getIntent().getLongArrayExtra("ManageTime");
        arrIDNumber = getIntent().getIntArrayExtra("ManageArray");
        accuracyFlag = getIntent().getBooleanExtra("ManageAccuracy", true);

        //****************Установка слотов на кнопки***********************

        View.OnClickListener Signal_Cancel1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        };

        //Кнопка принять
        View.OnClickListener Signal_Back = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        };
        buttonBackMang.setOnClickListener(Signal_Back);
        buttonBackCanc.setOnClickListener(Signal_Cancel1);

        //*******************************************************

        //Заполнение таблицы информацией
        showTable();

    }

    //Заполнение таблицы информацией
    protected void showTable(){

        tableManage.removeAllViews();

        //
        for(int i = 0; i < OverallNumber; i++) {
            arrRow[i] = new TableRow(database.this);
            arrRow[i].setX((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
            tableManage.addView(arrRow[i]);
            arrText[i] = new TextView(database.this);
            arrText[i].setText(String.valueOf(arrIDNumber[i]));
            arrText[i].setPadding(5,5,5,0);
            arrRow[i].addView(arrText[i]);
            long RunnerTime = arrTiming[i];
            long secs = ((int)RunnerTime/1000);
            long mins = secs/60;
            secs %= 60;
            long milliseconds = (int)(RunnerTime%1000);
            //Space Sp = new Space(database.this);
            //arrRow[i].addView(Sp);
            arrTime[i] = new TextView(database.this);
            arrTime[i].setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()),5,5,0);
            if(accuracyFlag)
                arrTime[i].setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs);
            else {
                arrTime[i].setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs +":"+
                        ((milliseconds/10) < 10 ? "0" : "") + (milliseconds/10) );
            }
            /* Old version = 00:00.000 format
            * arrTime[i].setText((mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs +":"+
            *        (milliseconds < 100 ? (milliseconds < 10 ? "00" : "0") : "") + milliseconds);
            */
            arrRow[i].addView(arrTime[i]);
            //Space tempSpace = new Space(database.this);
            //tempSpace.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            //arrRow[i].addView(tempSpace);


            TableRow.MarginLayoutParams marginParams1 = new TableRow.MarginLayoutParams(arrRow[i].getLayoutParams());
            marginParams1.setMargins(5, 0, 0, 0);
            TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(marginParams1);
            arrRow[i].setLayoutParams(layoutParams1);
            //arrText[i].setKeyListener(DigitsKeyListener.getInstance(false, false));

        }
    }
}
