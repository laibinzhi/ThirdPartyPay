package com.lbz.pay_sample;

public class PayResultEvent {

    public static int PAY_SUCCESS = 1;
    public static int PAY_FAIL = 2;
    public static int PAY_CANCEL = 3;
    private int type;

    public PayResultEvent(int pay) {
        this.type = pay;
    }

    public int getType() {
        return type;
    }

}
