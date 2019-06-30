package com.example.applicationtimer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Fragment_Database extends TabbedStopwatch {
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
}
