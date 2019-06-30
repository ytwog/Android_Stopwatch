package com.example.applicationtimer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

public class Fragment_Settings extends TabbedStopwatch {
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
}
