package com.lbz.pay;

import com.lbz.pay.alipay.AliPayAPI;
import com.lbz.pay.alipay.AliPayReq;
import com.lbz.pay.wechat.WeChatPayAPI;
import com.lbz.pay.wechat.WeChatPayReq;

public class PayAPI {


    private static final Object mLock = new Object();
    private static PayAPI mInstance;

    public static PayAPI getInstance(){
        if(mInstance == null){
            synchronized (mLock){
                if(mInstance == null){
                    mInstance = new PayAPI();
                }
            }
        }
        return mInstance;
    }


    public void sendPayRequest(WeChatPayReq wechatPayReq){
        WeChatPayAPI.getInstance().sendPayReq(wechatPayReq);
    }

    public void sendPayRequestWithOutKey(WeChatPayReq wechatPayReq){
        WeChatPayAPI.getInstance().sendPayReqWithOutKey(wechatPayReq);
    }

    public void sendPayRequest(AliPayReq aliPayReq){
        AliPayAPI.getInstance().sendPayReq(aliPayReq);
    }

    public void sendPayRequestByOrderInfo(AliPayReq aliPayReq){
        AliPayAPI.getInstance().sendPatReqByOrderInfo(aliPayReq);
    }

}
