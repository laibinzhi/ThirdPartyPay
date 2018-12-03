package com.lbz.pay.alipay;

import java.io.Serializable;

public class AliPayReqParam implements Serializable {

    private String body;//对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
    private String seller_id;//卖家支付宝账号
    private String total_fee;//金额
    private String service;//接口名称，固定值。
    private String _input_charset;//商户网站使用的编码格式，固定为UTF-8。
    private String sign;
    private String out_trade_no;//商户网站唯一订单号
    private String payment_type;//支付类型。默认值为：1（商品购买）。
    private String notify_url;//支付宝服务器主动通知商户网站里指定的页面http路径。
    private String sign_type;//签名类型，目前仅支持RSA。
    private String partner;//签约的支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。
    private String subject;//商品的标题/交易标题/订单标题/订单关键字等。

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String get_input_charset() {
        return _input_charset;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String createOrderInfo(){
        return getOrderInfo(this.subject, this.body, this.total_fee, this.partner, this.seller_id, this.out_trade_no, this.notify_url, this.service, this.payment_type, this._input_charset);
    }

    private String getOrderInfo(String subject, String body, String total_fee, String partner, String seller_id, String out_trade_no, String notify_url, String service, String payment_type, String _input_charset) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + partner + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + seller_id + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + out_trade_no + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + total_fee + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notify_url + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=" + "\"" + service + "\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=" + "\"" + payment_type + "\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=" + "\"" + _input_charset + "\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
//        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//        orderInfo += "&return_url=\"m.alipay.com\"";

        return orderInfo;
    }

    public AliPayReqParam(String body, String seller_id, String total_fee, String service, String _input_charset, String sign, String out_trade_no, String payment_type, String notify_url, String sign_type, String partner, String subject) {
        super();
        this.body = body;
        this.seller_id = seller_id;
        this.total_fee = total_fee;
        this.service = service;
        this._input_charset = _input_charset;
        this.sign = sign;
        this.out_trade_no = out_trade_no;
        this.payment_type = payment_type;
        this.notify_url = notify_url;
        this.sign_type = sign_type;
        this.partner = partner;
        this.subject = subject;
    }

}
