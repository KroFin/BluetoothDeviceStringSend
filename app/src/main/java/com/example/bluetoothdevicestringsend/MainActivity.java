package com.example.bluetoothdevicestringsend;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public byte message[] = new byte[1];
    private SensorManager sensorManager;
    private Sensor sensor;
    public static boolean sensor_isOn = false;

    private TextView sensor_info;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket = null;
    OutputStream outputStream = null ;

    private int ENABLE_BLUETOOTH = 2;
    private ImageButton imageButton;
    private PopupWindow mPopupWindow;
    private String TAG = "KroFin";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private List<BluetoothDeviceWhichBonded> mData;
    private Context mContext;
    private CheckBox checkBox = null;
    private MyAdapter mAdapter;
    private ListView list_device;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.notifyDataSetChanged();
                Snackbar.make(view, "Freshen successful", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            return;
        }
        if (!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Intent intent1 = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(intent,ENABLE_BLUETOOTH);
            startActivityForResult(intent1,ENABLE_BLUETOOTH);
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);// 搜索发现设备
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);// 结束搜索设备
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);// 状态改变
        registerReceiver(mReceiver,filter);

        mContext = this;
        list_device = (ListView) findViewById(R.id.lv_01);
        mData = new ArrayList<>();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mData.add(new BluetoothDeviceWhichBonded(device.getName(),device.getAddress()));
                mAdapter = new MyAdapter((ArrayList<BluetoothDeviceWhichBonded>)mData,mContext);
                list_device.setAdapter((mAdapter));
            }
        }
        mAdapter.notifyDataSetChanged();

        list_device.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            int currentNun = -1;
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                for (BluetoothDeviceWhichBonded bluetoothDeviceWhichBonded : mData){
                    bluetoothDeviceWhichBonded.setChecked(false);
                }
                if (currentNun == -1){
                    mData.get(position).setChecked(true);
                    currentNun = position;
                }else if (currentNun != position){
                    for (BluetoothDeviceWhichBonded bluetoothDeviceWhichBonded : mData){
                        bluetoothDeviceWhichBonded.setChecked(false);
                    }
                    currentNun = -1;
                }else if (currentNun != position){
                    for (BluetoothDeviceWhichBonded bluetoothDeviceWhichBonded : mData){
                        bluetoothDeviceWhichBonded.setChecked(false);
                    }
                    mData.get(position).setChecked(true);
                    currentNun = position;
                }

                new Thread(){
                    @Override
                    public void run(){
                        final UUID MY_UUID_SECURE=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        String blueAddress = mData.get(position).getAddress();
                        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                        bluetoothDevice = bluetoothAdapter.getRemoteDevice(blueAddress);
                        try{
                            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                            Log.d("true","开始连接");
                            bluetoothSocket.connect();
                            Log.d("true","完成连接");
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }.start();
                dialogSendMessage(view);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            Toast.makeText(getApplicationContext(),"123",Toast.LENGTH_LONG).show();
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mData.add(new BluetoothDeviceWhichBonded(device.getName(),device.getAddress()));
            }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                Toast.makeText(getApplicationContext(),"Discovery Finished",Toast.LENGTH_LONG).show();
            }
        }
    };

    public void bluetoothConnected(){
        BluetoothSocket bluetoothSocket;
    }

    public void dialogSendMessage (View view){
        final EditText editText = new EditText(this);
        final String message = String.valueOf(editText.getText());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Message");
        builder.setView(editText);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SendtoBlueTooth(message);
                Toast.makeText(MainActivity.this, "消息发送成功",Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                for (BluetoothDeviceWhichBonded bluetoothDeviceWhichBonded : mData){
                    bluetoothDeviceWhichBonded.setChecked(false);
                }
            }
        });

        builder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (BluetoothDeviceWhichBonded bluetoothDeviceWhichBonded : mData){
                    bluetoothDeviceWhichBonded.setChecked(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void dialogShowPersonalMessage (){
        final TextView t = new TextView(this);
        t.setText(Html.fromHtml("KroFin.icu"));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("About this App");
        alertDialog.setMessage("Author: KroFin\n");
        alertDialog.show();
    }

    public void waiting(){
        final ProgressDialog progressDialog = ProgressDialog.show(this,"Wait a moment","Connecting to the Device you choose",true,false);
        new Thread(){
          @Override
          public void run(){
              try {
                  Thread.sleep(5000);
                  progressDialog.cancel();
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
        }.start();
    }

    public void SendtoBlueTooth(String message){
        try{
            outputStream = bluetoothSocket.getOutputStream();
            outputStream.write(message.getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            bluetoothSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            dialogShowPersonalMessage();
        }
        return super.onOptionsItemSelected(item);
    }
}
