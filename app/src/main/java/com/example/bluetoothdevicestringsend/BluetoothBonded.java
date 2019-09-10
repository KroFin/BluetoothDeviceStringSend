package com.example.bluetoothdevicestringsend;

class BluetoothBonded {
    private String DeviceName;
    private String Address;

    public BluetoothBonded(){
    }

    public BluetoothBonded(String DeviceName,String Address){
        this.DeviceName = DeviceName;
        this.Address = Address;
    }

    public String getDeviceName (){
        return DeviceName;
    }

    public String getAddress (){
        return Address;
    }

    public void setDeviceName(){
        this.DeviceName = DeviceName;
    }

    public void setAddress(){
        this.Address = Address;
    }
}
