package com.lbz.pay.wechat;

import android.content.Context;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;


public class WeChatPayReq {

    IWXAPI api;

    private Context mContext;

    private WeChatReqParam mWeChatReqParam;

    public void send() {
        api = WXAPIFactory.createWXAPI(this.mContext, this.mWeChatReqParam.getAppid());

        PayReq req = new PayReq();
        req.appId = this.mWeChatReqParam.getAppid();
        req.partnerId = this.mWeChatReqParam.getMch_id();
        req.prepayId = this.mWeChatReqParam.getPrepay_id();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = this.mWeChatReqParam.getNonce_str();
        req.timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

        List<NameValuePair> signParams = new LinkedList<>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = WXPaySignUtil.genAppSign(signParams,this.mWeChatReqParam.getWechatKey());

        api.registerApp(this.mWeChatReqParam.getAppid());
        api.sendReq(req);

    }


    public static class Builder {

        private Context mContext;

        private WeChatReqParam mWeChatReqParam;

        public Builder with(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setWeChatReqParam(WeChatReqParam weChatReqParam) {
            this.mWeChatReqParam = weChatReqParam;
            return this;
        }

        public WeChatPayReq create() {
            WeChatPayReq wechatPayReq = new WeChatPayReq();
            wechatPayReq.mContext = this.mContext;
            wechatPayReq.mWeChatReqParam = this.mWeChatReqParam;
            return wechatPayReq;
        }

    }


}
