package com.example.bluetoothdevicestringsend;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private int ENABLE_BLUETOOTH = 2;
    private ImageButton imageButton;
    private PopupWindow mPopupWindow;
    private String TAG = "KroFin";
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDeviceWhichBonded> mData;
    private Context mContext;
    private MyAdapter mAdapter;
    private ListView list_device;
    private TextView textView;
    private String[] gender = new String[]{"Iot小车","文字传输系统","自定义模式"};

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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

        list_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogSendMessage(view);
            }
        });
    }

    class buttonListener implements View.OnClickListener{
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("123");
        builder.setTitle("123");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "yes",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "no",Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
