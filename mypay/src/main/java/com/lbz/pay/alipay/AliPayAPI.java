package com.lbz.pay.alipay;

public class AliPayAPI {

    private static final Object mLock = new Object();

    private static AliPayAPI mInstance;

    public static AliPayAPI getInstance(){
        if(mInstance == null){
            synchronized (mLock){
                if(mInstance == null){
                    mInstance = new AliPayAPI();
                }
            }
        }
        return mInstance;
    }

    public void sendPayReq(AliPayReq aliPayReq){
    	aliPayReq.send();
    }

    public void sendPatReqByOrderInfo(AliPayReq aliPayReq){
        aliPayReq.sendByOrderInfo();
    }

}
