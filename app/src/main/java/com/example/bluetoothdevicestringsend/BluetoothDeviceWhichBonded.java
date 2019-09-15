package com.example.bluetoothdevicestringsend;

public class BluetoothDeviceWhichBonded {
    private String DeviceName;
    private String Address;
    private boolean checked;

    public BluetoothDeviceWhichBonded(){

    }

    public BluetoothDeviceWhichBonded(String DeviceName,String Address){
        this.DeviceName = DeviceName;
        this.Address = Address;
    }

    public void setChecked(boolean checked){
        this.checked = checked;
    }

    public String getDeviceName (){
        return DeviceName;
    }

    public String getAddress (){
        return Address;
    }

    public boolean isChecked(){
        return checked;
    }

    public void setDeviceName(){
        this.DeviceName = DeviceName;
    }

    public void setAddress(){
        this.Address = Address;
    }
}
