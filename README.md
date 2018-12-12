# 简介
封装支付宝和微信第三方支付的一个库
# 快速集成
1.在项目根目录build.gradle中allprojects添加对aar的支持。
```
allprojects {
    repositories {
        // 添加下面的内容
        flatDir {
            dirs 'libs'
        }
        google()
        jcenter()
        maven { url "https://jitpack.io" }
        maven { url "https://dl.bintray.com/thelasterstar/maven/" }
        maven {
            url 'https://dl.bintray.com/laibinzhi/maven/'
        }
    }
}
```

2. 引入库
- 方法一，下载整个[项目](https://github.com/laibinzhi/ThirdPartyPay),把moudle模块***mypay***整个文件夹复制到你项目根目录，在app的build.gradle中的dependencies添加如下代码

```
    implementation project(':mypay')

```

然后再同一个文件android标签添加对mypay的aar文件的引用

```
android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.lbz.pay_sample"
        minSdkVersion 16
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
    } 
    repositories {
        flatDir {
            dirs 'libs', '/../mypay/libs'
        }
    }
}
```


然后在settings.gradle中加入

```
    include ':app', ':mypay'

```
最后rebuild一下就ok了。

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
implementation 'com.lbz.pay:mypay:0.0.5'

implementation (name: 'alipaySdk-15.5.9-20181123210601',ext: 'aar')

```
然后把aar文件copy到目录的libs文件夹（因为支付宝是以aar文件导入，aar文件无法引用aar文件的原因，需要重新导入一次）

最后rebuild一下就ok了

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

```
以上为**AliPayReqParam**的构造参数，建议以上参数通过服务器返回比较安全。它有两个构造方法，方法一

```
 public AliPayReqParam(String app_id, String sign, String timestamp, String notify_url, String subject, String out_trade_no, String total_amount) {
        this.app_id = app_id;
        this.sign = sign;
        this.timestamp = timestamp;
        this.notify_url = notify_url;
        this.subject = subject;
        this.out_trade_no = out_trade_no;
        this.total_amount = total_amount;
    }
```

方法二：

```
    public AliPayReqParam(String alipay_request) {
        this.alipay_request = alipay_request;
    }

```



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
//第一种构造方法
PayAPI.getInstance().sendPayRequest(aliPayReq);
//第二种构造方法
PayAPI.getInstance().sendPayRequestByOrderInfo(aliPayReq);

```


# 注意点
微信和支付宝回调的结果，若支付成功，建议从服务器查询一下订单状态是否生成，因为存在支付成功了，但是回调有延迟 导致服务器数据库没有更新。然后提示用户是否真的成功。推荐使用***RxJava*** ***trywhen***操作符


```
 Disposable orderReqDisposable;
    int mRequestTimes = 0;
    int REPEAT_TIMES = 3;

    private void checkOrderStatus(String id) {
        mRequestTimes = 0;
        showCheckOrderStatusLoadingDialog();
        orderReqDisposable = eliteApi.getVipOrderStstus(id)
                .delay(3, TimeUnit.SECONDS, true)       // 设置delayError为true，表示出现错误的时候也需要延迟3s进行通知，达到无论是请求正常还是请求失败，都是3s后重新订阅，即重新请求。
                .subscribeOn(Schedulers.io())
                .repeat(REPEAT_TIMES)   // repeat保证请求成功后能够重新订阅。
                .retry(REPEAT_TIMES)    // retry保证请求失败后能重新订阅
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseHttpResult<VipOrderStatusBean>>() {
                    @Override
                    public void accept(BaseHttpResult<VipOrderStatusBean> result) throws Exception {
                        //TODO 暂时以1为成功标志
                        if (result.getData().getStatus() == 1) {
                            orderReqDisposable.dispose();
                            hideCheckOrderStatusLoadingDialog();
                            showPaySuccessDialog();
                        } else {
                            mRequestTimes++;
                            if (mRequestTimes == REPEAT_TIMES) {
                                orderReqDisposable.dispose();
                                hideCheckOrderStatusLoadingDialog();
                                Toast.makeText(getActivity(), R.string.check_order_fail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideCheckOrderStatusLoadingDialog();
                    }
                });
        addDisposable(mCompositeDisposable);
    }
```



# 参考文档
- 微信：[https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5](https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5)
- 支付宝：[https://docs.open.alipay.com/204/105296/](https://docs.open.alipay.com/204/105296/)



