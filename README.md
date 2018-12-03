# 简介
封装支付宝和微信第三方支付的一个库
# 快速集成
1. 引入库
- 方法一，下载整个[项目](https://github.com/laibinzhi/ThirdPartyPay),把moudle模块***mypay***整个文件夹复制到你项目根目录，在app的build.gradle中的***dependencies***添加如下代码

```
    implementation project(':mypay')

```

然后在settings.gradle中加入

```
    include ':app', ':mypay'

```
然后rebuild一下就ok了。

- 方法二，通过dependencies方式：

首先在app根目录的build.gradle中添加

```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://dl.bintray.com/thelasterstar/maven/" }
        maven {
            url 'https://dl.bintray.com/laibinzhi/maven/'
        }
    }
}
```

然后再项目的build.gradle中的dependencies添加


```
 implementation 'com.lbz.pay:third_party_pay:0.0.1'
```

rebuild一下就ok了

2. 微信授权登录需要一个微信回调类WXPayEntryActivity，注意这个类的位置是在程序包名下的wxapi包下。例如我程序的包名是com.lbz.pay_sample，那么这个类就在com.lbz.pay_sample.wxapi下。WXPayEntryActivity可以参考下面。接受到微信的回调信息，使用EventBus通知发起支付的activity支付结果是怎样子的再做相应的显示。

```
public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, ApiConstants.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    EventBus.getDefault().post(new PayResultEvent(PayResultEvent.PAY_SUCCESS));
                    break;
                case -1:
                    EventBus.getDefault().post(new PayResultEvent(PayResultEvent.PAY_FAIL));
                    break;
                case -2:
                    EventBus.getDefault().post(new PayResultEvent(PayResultEvent.PAY_CANCEL));
                    break;
            }
            finish();
        }
    }
}
```


3. 在manifest文件application标签内的加入前面WXPayEntryActivity 

```
  <activity
    android:name="com.lbz.pay_sample.wxapi.WXPayEntryActivity"
    android:exported="true"
    android:launchMode="singleTop"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.NoDisplay" />
```

4. 请求支付

---
微信请求：

- 第一步构建请求参数[WeChatReqParam](https://github.com/laibinzhi/ThirdPartyPay/blob/master/mypay/src/main/java/com/lbz/pay/wechat/WeChatReqParam.java)

```
  private String nonce_str;//随机字符串
    private String sign;//签名
    private String prepay_id;//预支付交易会话标识
    private String mch_id;//商户号	微信支付分配的商户号
    private String appid;//应用ID
    private String wechatKey;//微信支付商户支付key
  
   public WeChatReqParam(String nonce_str, String sign, String prepay_id, String mch_id, String appid,String wechatKey) {
        super();
        this.nonce_str = nonce_str;
        this.sign = sign;
        this.prepay_id = prepay_id;
        this.mch_id = mch_id;
        this.appid = appid;
        this.wechatKey =wechatKey;
    }
```
以上为**WeChatReqParam**的构造参数，建议以上参数通过服务器返回比较安全。

- 第二步，构造请求体**WeChatPayReq**，传入前一步的WeChatReqParam

```
   WeChatPayReq weChatPayReq = new WeChatPayReq.Builder().with(MainActivity.this)
                .setWeChatReqParam(weChatReqParam)
                .create();
```

- 第三步发起请求

```
        PayAPI.getInstance().sendPayRequest(weChatPayReq);

```

- 第四部，接收回调，建议使用[EventBus](https://github.com/greenrobot/EventBus)。


```
  @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PayResultEvent payResultEvent) {

        if (payResultEvent.getType() == PayResultEvent.PAY_SUCCESS) {
            Toast.makeText(MainActivity.this, "微信支付成功", Toast.LENGTH_SHORT).show();
            //TODO 此处为微信回调的结果，若支付成功，建议从服务器查询一下订单状态是否生成，因为存在支付成功了，但是回调有延迟
            // TODO 导致服务器数据库没有更新。然后提示用户是否真的成功
        } else if (payResultEvent.getType() == PayResultEvent.PAY_FAIL) {
            Toast.makeText(MainActivity.this, "微信支付失败", Toast.LENGTH_SHORT).show();
        } else if (payResultEvent.getType() == PayResultEvent.PAY_CANCEL) {
            Toast.makeText(MainActivity.this, "微信支付取消", Toast.LENGTH_SHORT).show();
        }

    }
```

---
支付宝请求：

- 第一步构建请求参数[AliPayReqParam](https://github.com/laibinzhi/ThirdPartyPay/blob/master/mypay/src/main/java/com/lbz/pay/alipay/AliPayReqParam.java)

```
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
```
以上为**AliPayReqParam**的构造参数，建议以上参数通过服务器返回比较安全。

- 第二步，构造请求体**AliPayReq**，传入前一步的AliPayReq,同时设置回调监听器setOnAliPayListener

```
       AliPayReq aliPayReq = new AliPayReq.Builder().with(MainActivity.this)
                .setAliPayReqParam(aliPayReqParam)
                .create()
                .setOnAliPayListener(new AliPayReq.OnAliPayListener() {
                    @Override
                    public void onPaySuccess(String resultInfo) {
                        Toast.makeText(MainActivity.this, "支付宝支付成功", Toast.LENGTH_SHORT).show();
                        //TODO 此处为微信回调的结果，若支付成功，建议从服务器查询一下订单状态是否生成，因为存在支付成功了，但是回调有延迟
                        // TODO 导致服务器数据库没有更新。然后提示用户是否真的成功
                    }

                    @Override
                    public void onPayFailure(String resultInfo) {
                        Toast.makeText(MainActivity.this, "支付宝支付失败", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPayConfirmimg(String resultInfo) {
                        Toast.makeText(MainActivity.this, "支付宝支付确认中", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPayCancel(String resultInfo) {
                        Toast.makeText(MainActivity.this, "支付宝支付取消", Toast.LENGTH_SHORT).show();

                    }
                });
```

- 第三步发起请求

```
              PayAPI.getInstance().sendPayRequest(aliPayReq);


```


# 注意点
微信和支付宝回调的结果，若支付成功，建议从服务器查询一下订单状态是否生成，因为存在支付成功了，但是回调有延迟 导致服务器数据库没有更新。然后提示用户是否真的成功。推荐使用***RxJava*** ***trywhen***操作符


# 参考文档
- 微信：[https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5](https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5)
- 支付宝：[https://docs.open.alipay.com/59/103662](https://docs.open.alipay.com/59/103662)



