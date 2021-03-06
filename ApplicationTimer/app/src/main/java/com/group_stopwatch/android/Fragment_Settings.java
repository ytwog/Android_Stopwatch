package com.group_stopwatch.android;

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
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.PrecomputedText;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.Size;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.XMLFormatter;

public class Fragment_Settings extends TabbedStopwatch {
    public static class SettingsActivity extends Fragment {
        public static UUID uuid;
        public static EditText editLaps, Param1Edit, Param2Edit;
        public static final int VOICE_RECOGNITION_REQUEST_CODE = 4300;
        final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        protected Thread threadOld;
        public View rootView;
        protected ListView listView;
        protected ImageButton Button_BluetoothConnect;
        protected Button ButtonStyle1, ButtonStyle2, ButtonStyle3, SendButton, slash1Radio, slash2Radio;
        protected TextView BTAddress1, BTname1, BluetoothStatus_text;
        protected EditText editTimeUntil, editWaitAfter;
        //Radio 1
        protected RadioGroup radioGroup1, radioGroup2;
        protected RadioButton radio1, radioF;
        //Radio Up
        protected RadioGroup radioGroupUp1;
        protected RadioButton radioUp1;
        protected CheckBox checkVolumeStart;
        protected List <BluetoothDevice> bDevices;
        protected int ChosenDevice;
        protected int OverallNumber;
        protected String arrIDNumberSettings[], tempArr[];

        public static class AcceptThread extends Thread{
            private BluetoothServerSocket mmServerSocket;
            OutputStream mmOutStream;
            protected BluetoothAdapter bl1;
            InputStream mmInStream;
            private boolean running;
            public void manageConnectedSocket(BluetoothSocket socket) {
                InputStream tmpIn=null;
                OutputStream tmpOut=null;
                try{
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch(IOException e){}
                mmInStream = tmpIn;
                mmOutStream = tmpOut;
                String message = "HELLO WORLD";
                long timeInMilliseconds1 = SystemClock.uptimeMillis()-startTime;
                long updateTime1 = timeSwapBuffer+timeInMilliseconds;
                int secs1 = ((int)updateTime/1000);
                int mins1 = secs/60;
                secs1 %= 60;
                byte bytes[] = {(byte)(updateTime/60000), (byte)secs1};
                try{
                    mmOutStream.write(bytes);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }

            public void setRunning(boolean running) {
                this.running = running;
            }

            public AcceptThread(BluetoothAdapter bluetooth){
                bl1 = bluetooth;
                BluetoothServerSocket tmp = null;
                setRunning(false);
                try{
                    tmp = bluetooth.listenUsingInsecureRfcommWithServiceRecord("BluetoothTimer", uuid);
                } catch(IOException e){
                    Log.d("Error", "Didn't connect to device");
                    e.printStackTrace();}
                mmServerSocket = tmp;
            }

            public void run(){
                BluetoothSocket socket = null;
                if(mmServerSocket == null) return;
                while(true){
                    try{
                        socket = mmServerSocket.accept(30000);
                    } catch(IOException e){
                        Log.d("INFO", "TIMEOUT HAS REACHED");
                        break;
                    }
                    Log.d("INFO", "socketFound");
                    // если соединение было подтверждено
                    if(socket!=null){
                        // управляем соединением
                        manageConnectedSocket(socket);
                    }
                    else {
                        break;
                    }
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BluetoothServerSocket tmp = null;
                    try{
                        tmp = bl1.listenUsingInsecureRfcommWithServiceRecord("BluetoothTimer", uuid);
                    } catch(IOException e){
                        Log.d("Error", "Didn't connect to device");
                        e.printStackTrace();}
                    mmServerSocket = tmp;
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
            radioGroup2.check(SData.getFinishedOrLeft() ? R.id.radio_finished : R.id.radio_left);
            //SendButton.setVisibility(View.INVISIBLE);
            editLaps.setEnabled(startTime==0);
            Param1Edit.setEnabled(startTime==0);
            Param2Edit.setEnabled(startTime==0);
            if(toUpdate){
                String rewriteText = "";
                for(int i = 0; i < OverallNumber; i++) {
                    rewriteText += arrIDNumberSettings[i];
                    if(i + 1 != OverallNumber) rewriteText += ",";
                }
                toUpdate = false;
            }
            if(SData.slashLaps) {
                radioGroup2.setVisibility(View.VISIBLE);
                ((TextView)rootView.findViewById(R.id.textView_LorF)).setTextColor(getResources().getColor(R.color.black));

            } else {
                radioGroup2.setVisibility(View.INVISIBLE);
                ((TextView)rootView.findViewById(R.id.textView_LorF)).setTextColor(getResources().getColor(R.color.gray));
            }
            Tab1.updateStyle();
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data){

            if (requestCode == 435 && resultCode == RESULT_OK) {
                Button_BluetoothConnect.callOnClick();
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
            ButtonStyle1 = (Button)rootView.findViewById(R.id.buttonStyle1);
            ButtonStyle2 = (Button)rootView.findViewById(R.id.buttonStyle2);
            ButtonStyle3 = (Button)rootView.findViewById(R.id.buttonStyle3);
            BTAddress1 = (TextView) rootView.findViewById(R.id.AddressBT_1);
            BTname1 = (TextView) rootView.findViewById(R.id.NameBT_1);
            editLaps = new EditText(Tab3.getActivity());
            editWaitAfter = new EditText(Tab3.getActivity());
            Param1Edit = new EditText(Tab3.getActivity());
            Param2Edit = new EditText(Tab3.getActivity());
            editTimeUntil = new EditText(Tab3.getActivity());
            radio1 = (RadioButton) rootView.findViewById(R.id.radio_1);
            radioGroup1 = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
            radioGroup2 = (RadioGroup) rootView.findViewById(R.id.radio_group2);
            slash1Radio = (Button) rootView.findViewById(R.id.button_withSlash);
            slash2Radio = (Button) rootView.findViewById(R.id.button_woSlash);
            radioGroupUp1 = (RadioGroup) rootView.findViewById(R.id.radioGroupUpcoming);
            radioUp1 = (RadioButton) rootView.findViewById(R.id.radio_up1);
            checkVolumeStart = (CheckBox) rootView.findViewById(R.id.check_volumeStart);
            //final TextView VersionApp = (TextView) rootView.findViewById(R.id._versionApp);

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
                    radioGroup2.setVisibility(View.VISIBLE);
                    ((TextView)rootView.findViewById(R.id.textView_LorF)).setTextColor(getResources().getColor(R.color.black));
                    slash1Radio.setAlpha(1);
                    slash2Radio.setAlpha(0.5f);
                }
            });

            slash2Radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SData.slashLaps = false;
                    radioGroup2.setVisibility(View.INVISIBLE);
                    ((TextView)rootView.findViewById(R.id.textView_LorF)).setTextColor(getResources().getColor(R.color.gray));
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

            radioGroup1.check(SData.getAccuracyFlag() ? R.id.radio_1 : R.id.radio_2);

            radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    SData.finishedOrLeft = checkedId==R.id.radio_finished ? true : false;
                }
            });

            radioGroup2.check(SData.getFinishedOrLeft() ? R.id.radio_finished : R.id.radio_left);


            arrIDNumberSettings = arrIDNumber;
            editLaps.setText(String.valueOf(SData.getLaps()));
            Param1Edit.setText(String.valueOf(SData.Dialog_Param1));
            Param2Edit.setText(String.valueOf(SData.Dialog_Param2));
            editTimeUntil.setText(String.valueOf(SData.getTimeUntilShown()));

            uuid = UUID.fromString("B62C4E8D-62CC-404b-BBBF-BF3E3BBB1374");
                                   //"00001133-0000-1000-8000-00805F9B34FB");
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
                            if(CanvasView != null) CanvasView.default_time = SData.timeUntilSHown * 1000;
                            editWaitAfter.setError(null);
                        }

                    }
                    mFormating = false;
                }
            });
            //Установка ограничений на ввод целых десятичных чисел
            editLaps.setKeyListener(DigitsKeyListener.getInstance(false, false));
            editTimeUntil.setKeyListener(DigitsKeyListener.getInstance(false, false));

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
                                    String action = intent.getAction();
                                    // Когда найдено новое устройство
                                    if(BluetoothDevice.ACTION_FOUND.equals(action)){
                                        // Получаем объект BluetoothDevice из интента
                                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                        //Добавляем имя и адрес в array adapter, чтобы показывать в ListView
                                        //mArrayAdapter.add(device.getName()+"\n"+ device.getAddress());
                                        //bDevices.add(device);
                                        //listView.setAdapter(mArrayAdapter);
                                    }
                                }
                            };
                            // Регистрируем BroadcastReceiver
                            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                            rootView.getContext().registerReceiver(mReceiver, filter);
                            //bluetooth.startDiscovery();
                            //listView.setAdapter(mArrayAdapter);
                            /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    BluetoothStatus_text.setText(mArrayAdapter.getItem(position));
                                    ChosenDevice = position;
                                    SendButton.setVisibility(View.VISIBLE);
                                }
                            });*/

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
                    if (!bluetooth.isEnabled()) {
                        BluetoothStatus_text.setText("Bluetooth выключен");
                        return;
                    }
                    //bluetooth.cancelDiscovery();
                    AcceptThread threadBluetooth = new AcceptThread(bluetooth);
                    Thread thread = new Thread(threadBluetooth);
                    threadBluetooth.setRunning(true);
                    BluetoothStatus_text.setText("Начало передачи");
                    thread.start();
                    if(threadOld!=null) threadOld.interrupt();
                    threadOld = thread;
                }
            };

            SendButton.setOnClickListener(Signal_Send);
            Button_BluetoothConnect.setOnClickListener(Signal_ConnectBluetooth);
            //************************************************
            Vector<String> vecSettingNames = new Vector<String>();
            Vector<EditText> vecSettingEdits = new Vector<EditText>();
            vecSettingNames.add(getString(R.string._TimeUntil));
            vecSettingNames.add(getString(R.string._strParamSettings));
            vecSettingNames.add(getString(R.string._textLengthDistanse));
            vecSettingNames.add(getString(R.string._lapsNum_str));
            vecSettingNames.add(getString(R.string._textTimeWaitAfter));
            // protected EditText editTimeUntil, editWaitAfter, Param1Edit, Param2Edit, editLaps;
            vecSettingEdits.add(editTimeUntil);
            vecSettingEdits.add(Param1Edit);
            vecSettingEdits.add(Param2Edit);
            vecSettingEdits.add(editLaps);
            vecSettingEdits.add(editWaitAfter);
            loadSettingTable((LinearLayout) rootView.findViewById(R.id.tableSettings), vecSettingNames, vecSettingEdits);
            return rootView;
        }

/*
                android:textAppearance="@style/TextAppearance.AppCompat.Button"
                android:theme="@android:style/Theme.Black.NoTitleBar"
 */

        private void loadSettingTable(LinearLayout tableSettings, Vector<String> vec1, Vector<EditText> vec2) {
            int iter = 0;
            for (String q: vec1) {
                TextView tv = new TextView(Tab3.getActivity());
                EditText ed = vec2.elementAt(iter);
                ed.setInputType(InputType.TYPE_CLASS_NUMBER);
                ed.setEms(10);
                ed.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tv.setTextColor(getResources().getColor(R.color.black));
                TextViewCompat.setTextAppearance(ed, R.style.TextAppearance_AppCompat_Button);
                LinearLayout ll_Vertical = new LinearLayout(Tab3.getActivity());
                ll_Vertical.setOrientation(LinearLayout.HORIZONTAL);
                ll_Vertical.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        toDP(50)));
                tv.setText(q);
                tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                ed.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 5));
                ll_Vertical.addView(tv);
                ll_Vertical.addView(ed);
                tableSettings.addView(ll_Vertical);
                iter++;
            }
        }

    }
}
