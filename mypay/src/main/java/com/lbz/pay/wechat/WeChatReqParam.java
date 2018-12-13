package com.lbz.pay.wechat;

import java.io.Serializable;

public class WeChatReqParam implements Serializable{

    private String nonce_str;//随机字符串
    private String sign;//签名
    private String prepay_id;//预支付交易会话标识
    private String mch_id;//商户号	微信支付分配的商户号
    private String appid;//应用ID
    private String wechatKey;//微信支付商户支付key

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getWechatKey() {
        return wechatKey;
    }

    public void setWechatKey(String wechatKey) {
        this.wechatKey = wechatKey;
    }

    public WeChatReqParam(String nonce_str, String sign, String prepay_id, String mch_id, String appid,String wechatKey) {
        super();
        this.nonce_str = nonce_str;
        this.sign = sign;
        this.prepay_id = prepay_id;
        this.mch_id = mch_id;
        this.appid = appid;
        this.wechatKey =wechatKey;
    }

    public WeChatReqParam(String nonce_str, String sign, String prepay_id, String mch_id, String appid) {
        this.nonce_str = nonce_str;
        this.sign = sign;
        this.prepay_id = prepay_id;
        this.mch_id = mch_id;
        this.appid = appid;
    }

    @Override
    public String toString() {
        return "WeChatReqParam{" +
                "nonce_str='" + nonce_str + '\'' +
                ", sign='" + sign + '\'' +
                ", prepay_id='" + prepay_id + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", appid='" + appid + '\'' +
                ", wechatKey='" + wechatKey + '\'' +
                '}';
    }
}
