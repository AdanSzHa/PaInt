package com.adans.app_10.Cowtech54;

import java.util.LinkedList;

/**
 * Created by Wily on 27/03/2018.
 */

public class IDObject {
    public Number ObjID;
    public Number[] CurrentTram;
    public LinkedList<Number> Time;
    public LinkedList<Number> CanID;
    public LinkedList<Number> Len;
    public LinkedList<Number> Byte1;
    public LinkedList<Number> Byte2;
    public LinkedList<Number> Byte3;
    public LinkedList<Number> Byte4;
    public LinkedList<Number> Byte5;
    public LinkedList<Number> Byte6;
    public LinkedList<Number> Byte7;
    public LinkedList<Number> Byte8;
    public Number TimePeriod;

    public IDObject(Number objID) {
        ObjID = objID;

        Time = new LinkedList<Number>();
        CanID = new LinkedList<Number>();
        Len = new LinkedList<Number>();
        Byte1 = new LinkedList<Number>();
        Byte2 = new LinkedList<Number>();
        Byte3 = new LinkedList<Number>();
        Byte4 = new LinkedList<Number>();
        Byte5 = new LinkedList<Number>();
        Byte6 = new LinkedList<Number>();
        Byte7 = new LinkedList<Number>();
        Byte8 = new LinkedList<Number>();
        Number[] trm = {0,0,0,0,0,0,0,0,0,0,0};
        addTram(trm);
    }

    public IDObject(Number objID, Number[] currentTram) {
        ObjID = objID;
        CurrentTram = currentTram;

        //Add tram to each linked List
        addTram(currentTram);

    }

    public IDObject(Number objID, Number[] currentTram, LinkedList<Number> time, LinkedList<Number> canID, LinkedList<Number> len, LinkedList<Number> byte1, LinkedList<Number> byte2, LinkedList<Number> byte3, LinkedList<Number> byte4, LinkedList<Number> byte5, LinkedList<Number> byte6, LinkedList<Number> byte7, LinkedList<Number> byte8) {
        ObjID = objID;
        CurrentTram = currentTram;
        Time = time;
        CanID = canID;
        Len = len;
        Byte1 = byte1;
        Byte2 = byte2;
        Byte3 = byte3;
        Byte4 = byte4;
        Byte5 = byte5;
        Byte6 = byte6;
        Byte7 = byte7;
        Byte8 = byte8;
    }

    // Methods -----------------------------
    public void addTram(Number[] tram){
        int tramLength = tram.length;
        CurrentTram = tram;
        double t2 = Double.parseDouble(CurrentTram[0].toString())/1000.0;
        //double t2 = CurrentTram[0].toString())/1000.0;
        Time.addLast(t2);//(CurrentTram[0]);
        CanID.addLast(CurrentTram[1]);
        Len.addLast(CurrentTram[2]);
        Byte1.addLast(CurrentTram[3]);
        if(tramLength>4)    Byte2.addLast(CurrentTram[4]);
        else return;
        if(tramLength>5)    Byte3.addLast(CurrentTram[5]);
        else return;
        if(tramLength>6)    Byte4.addLast(CurrentTram[6]);
        else return;
        if(tramLength>7)    Byte5.addLast(CurrentTram[7]);
        else return;
        if(tramLength>8)    Byte6.addLast(CurrentTram[8]);
        else return;
        if(tramLength>9)    Byte7.addLast(CurrentTram[9]);
        else return;
        if(tramLength>10)    Byte8.addLast(CurrentTram[10]);
    }

    public void removeFirstItemInLists(){

        removeFirstItem(Time);
        removeFirstItem(CanID);
        removeFirstItem(Len);
        removeFirstItem(Byte1);
        removeFirstItem(Byte2);
        removeFirstItem(Byte3);
        removeFirstItem(Byte4);
        removeFirstItem(Byte5);
        removeFirstItem(Byte6);
        removeFirstItem(Byte7);
        removeFirstItem(Byte8);
/*
        Time.removeFirst();
        CanID.removeFirst();
        Len.removeFirst();
        Byte1.removeFirst();
        Byte2.removeFirst();
        Byte3.removeFirst();
        Byte4.removeFirst();
        Byte5.removeFirst();
        Byte6.removeFirst();
        Byte7.removeFirst();
        Byte8.removeFirst();
*/
    }

    public void removeFirstItem(LinkedList <Number> lst){
        if(lst.size() >= 1)
            lst.removeFirst();
    }

    public void removeAll(){

        Time = new LinkedList<Number>();
        CanID = new LinkedList<Number>();
        Len = new LinkedList<Number>();
        Byte1 = new LinkedList<Number>();
        Byte2 = new LinkedList<Number>();
        Byte3 = new LinkedList<Number>();
        Byte4 = new LinkedList<Number>();
        Byte5 = new LinkedList<Number>();
        Byte6 = new LinkedList<Number>();
        Byte7 = new LinkedList<Number>();
        Byte8 = new LinkedList<Number>();
    }

    // Initers

    // Getters -------------------------------------

    public Number getTimePeriod(){

        if(Time.size()>2){
            TimePeriod = (double)Time.getLast()-(double)Time.getFirst();
        }
        else{
            TimePeriod = 1.0;
        }
        return TimePeriod;
    }

    public Number getObjID() {
        return ObjID;
    }

    public LinkedList<Number> getTime() {
        return Time;
    }

    public LinkedList<Number> getCanID() {
        return CanID;
    }

    public LinkedList<Number> getLen() {
        return Len;
    }

    public LinkedList<Number> getByte1() {
        return Byte1;
    }

    public LinkedList<Number> getByte2() {
        return Byte2;
    }

    public LinkedList<Number> getByte3() {
        return Byte3;
    }

    public LinkedList<Number> getByte4() {
        return Byte4;
    }

    public LinkedList<Number> getByte5() {
        return Byte5;
    }

    public LinkedList<Number> getByte6() {
        return Byte6;
    }

    public LinkedList<Number> getByte7() {
        return Byte7;
    }

    public LinkedList<Number> getByte8() {
        return Byte8;
    }

   /* public class Store {

        private ArrayList<IDObject> custArr;

        public new Store() {
            custArr = new ArrayList<IDObject>();
        }

        public void addSale(String customerName, double amount) {
            custArr.add(new IDObject(customerName, amount));
        }

        public IDObject getSaleAtIndex(int index) {
            return custArr.get(index);
        }

        //or if you want the entire ArrayList:
        public ArrayList getCustArr() {
            return custArr;
        }
    }*/
}
