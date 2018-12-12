package com.lbz.pay.alipay;

import android.util.Log;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AliPayReqParam implements Serializable {

    public static final String TAG = AliPayReqParam.class.getSimpleName();

    //公共参数
    private String app_id;//支付宝分配给开发者的应用ID
    private String sign;//商户请求参数的签名串，详见签名
    private String timestamp;//发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
    private String notify_url;//支付宝服务器主动通知商户服务器里指定的页面http/https路径。建议商户使用https

    //业务请求参数集合
    private String subject;//商品的标题/交易标题/订单标题/订单关键字等。
    private String out_trade_no;//商户网站唯一订单号
    private String total_amount;//订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]

    private String alipay_request;

    public String getAlipay_request() {
        return alipay_request;
    }

    public void setAlipay_request(String alipay_request) {
        this.alipay_request = alipay_request;
    }

    public String createOrderInfo() {
        return getOrderInfo(this.app_id, this.timestamp, this.notify_url, this.subject, this.out_trade_no, this.total_amount, this.sign);
    }

    private String getOrderInfo(String app_id, String timestamp, String notify_url, String subject, String out_trade_no, String total_amount, String sign) {
        Map<String, String> keyValues = new HashMap<String, String>();

        keyValues.put("app_id", app_id);
        keyValues.put("method", "alipay.trade.app.pay");
        keyValues.put("charset", "utf-8");
        keyValues.put("sign_type", "RSA2");
        keyValues.put("timestamp", timestamp);
        keyValues.put("version", "1.0");
        keyValues.put("notify_url", notify_url);
        String biz_content = "{subject:" + "\"" + subject + "\"";
        biz_content += ",out_trade_no:" + "\"" + out_trade_no + "\"";
        biz_content += ",total_amount:" + "\"" + total_amount + "\"";
        biz_content += ",product_code:" + "\"" + "QUICK_MSECURITY_PAY" + "\"}";
        keyValues.put("biz_content", biz_content);
        keyValues.put("sign", sign);
        Log.e(TAG, "biz_content=" + biz_content);
        return buildOrderParam(keyValues);
    }


    private String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    public AliPayReqParam(String app_id, String sign, String timestamp, String notify_url, String subject, String out_trade_no, String total_amount) {
        this.app_id = app_id;
        this.sign = sign;
        this.timestamp = timestamp;
        this.notify_url = notify_url;
        this.subject = subject;
        this.out_trade_no = out_trade_no;
        this.total_amount = total_amount;
    }

    public AliPayReqParam(String alipay_request) {
        this.alipay_request = alipay_request;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    @Override
    public String toString() {
        return "AliPayReqParam{" +
                "app_id='" + app_id + '\'' +
                ", sign='" + sign + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", notify_url='" + notify_url + '\'' +
                ", subject='" + subject + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", total_amount='" + total_amount + '\'' +
                ", alipay_request='" + alipay_request + '\'' +
                '}';
    }
}
