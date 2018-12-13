package com.lbz.pay.wechat;

public class WeChatPayAPI {

    private static final Object mLock = new Object();

    private static WeChatPayAPI mInstance;

    public static WeChatPayAPI getInstance(){
        if(mInstance == null){
            synchronized (mLock){
                if(mInstance == null){
                    mInstance = new WeChatPayAPI();
                }
            }
        }
        return mInstance;
    }

    public void sendPayReq(WeChatPayReq wechatPayReq){
        wechatPayReq.send();
    }

    public void sendPayReqWithOutKey(WeChatPayReq wechatPayReq){
        wechatPayReq.sendWithOutKey();
    }

}
