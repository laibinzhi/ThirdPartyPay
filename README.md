# ���
��װ֧������΢�ŵ�����֧����һ����
# ���ټ���
1.����Ŀ��Ŀ¼build.gradle��allprojects��Ӷ�aar��֧�֡�
```
allprojects {
    repositories {
        // ������������
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

2. �����
- ����һ����������[��Ŀ](https://github.com/laibinzhi/ThirdPartyPay),��moudleģ��***mypay***�����ļ��и��Ƶ�����Ŀ��Ŀ¼����app��build.gradle�е�dependencies������´���

```
    implementation project(':mypay')

```

Ȼ����ͬһ���ļ�android��ǩ��Ӷ�mypay��aar�ļ�������

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


Ȼ����settings.gradle�м���

```
    include ':app', ':mypay'

```
���rebuildһ�¾�ok�ˡ�

- ��������ͨ��dependencies��ʽ��

������app��Ŀ¼��build.gradle�����

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

Ȼ������Ŀ��build.gradle�е�dependencies���


```
implementation 'com.lbz.pay:mypay:0.0.5'

implementation (name: 'alipaySdk-15.5.9-20181123210601',ext: 'aar')

```
Ȼ���aar�ļ�copy��Ŀ¼��libs�ļ��У���Ϊ֧��������aar�ļ����룬aar�ļ��޷�����aar�ļ���ԭ����Ҫ���µ���һ�Σ�

���rebuildһ�¾�ok��

2. ΢����Ȩ��¼��Ҫһ��΢�Żص���WXPayEntryActivity��ע��������λ�����ڳ�������µ�wxapi���¡������ҳ���İ�����com.lbz.pay_sample����ô��������com.lbz.pay_sample.wxapi�¡�WXPayEntryActivity���Բο����档���ܵ�΢�ŵĻص���Ϣ��ʹ��EventBus֪ͨ����֧����activity֧������������ӵ�������Ӧ����ʾ��

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


3. ��manifest�ļ�application��ǩ�ڵļ���ǰ��WXPayEntryActivity 

```
  <activity
    android:name="com.lbz.pay_sample.wxapi.WXPayEntryActivity"
    android:exported="true"
    android:launchMode="singleTop"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.NoDisplay" />
```

4. ����֧��

---
΢������

- ��һ�������������[WeChatReqParam](https://github.com/laibinzhi/ThirdPartyPay/blob/master/mypay/src/main/java/com/lbz/pay/wechat/WeChatReqParam.java)

```
  private String nonce_str;//����ַ���
    private String sign;//ǩ��
    private String prepay_id;//Ԥ֧�����׻Ự��ʶ
    private String mch_id;//�̻���	΢��֧��������̻���
    private String appid;//Ӧ��ID
    private String wechatKey;//΢��֧���̻�֧��key
  
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
����Ϊ**WeChatReqParam**�Ĺ���������������ϲ���ͨ�����������رȽϰ�ȫ��

- �ڶ���������������**WeChatPayReq**������ǰһ����WeChatReqParam

```
   WeChatPayReq weChatPayReq = new WeChatPayReq.Builder().with(MainActivity.this)
                .setWeChatReqParam(weChatReqParam)
                .create();
```

- ��������������

```
        PayAPI.getInstance().sendPayRequest(weChatPayReq);

```

- ���Ĳ������ջص�������ʹ��[EventBus](https://github.com/greenrobot/EventBus)��


```
  @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PayResultEvent payResultEvent) {

        if (payResultEvent.getType() == PayResultEvent.PAY_SUCCESS) {
            Toast.makeText(MainActivity.this, "΢��֧���ɹ�", Toast.LENGTH_SHORT).show();
            //TODO �˴�Ϊ΢�Żص��Ľ������֧���ɹ�������ӷ�������ѯһ�¶���״̬�Ƿ����ɣ���Ϊ����֧���ɹ��ˣ����ǻص����ӳ�
            // TODO ���·��������ݿ�û�и��¡�Ȼ����ʾ�û��Ƿ���ĳɹ�
        } else if (payResultEvent.getType() == PayResultEvent.PAY_FAIL) {
            Toast.makeText(MainActivity.this, "΢��֧��ʧ��", Toast.LENGTH_SHORT).show();
        } else if (payResultEvent.getType() == PayResultEvent.PAY_CANCEL) {
            Toast.makeText(MainActivity.this, "΢��֧��ȡ��", Toast.LENGTH_SHORT).show();
        }

    }
```

---
֧��������

- ��һ�������������[AliPayReqParam](https://github.com/laibinzhi/ThirdPartyPay/blob/master/mypay/src/main/java/com/lbz/pay/alipay/AliPayReqParam.java)

```
   //��������
    private String app_id;//֧��������������ߵ�Ӧ��ID
    private String sign;//�̻����������ǩ���������ǩ��
    private String timestamp;//���������ʱ�䣬��ʽ"yyyy-MM-dd HH:mm:ss"
    private String notify_url;//֧��������������֪ͨ�̻���������ָ����ҳ��http/https·���������̻�ʹ��https

    //ҵ�������������
    private String subject;//��Ʒ�ı���/���ױ���/��������/�����ؼ��ֵȡ�
    private String out_trade_no;//�̻���վΨһ������
    private String total_amount;//�����ܽ���λΪԪ����ȷ��С�������λ��ȡֵ��Χ[0.01,100000000]

    private String alipay_request;

```
����Ϊ**AliPayReqParam**�Ĺ���������������ϲ���ͨ�����������رȽϰ�ȫ�������������췽��������һ

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

��������

```
    public AliPayReqParam(String alipay_request) {
        this.alipay_request = alipay_request;
    }

```



- �ڶ���������������**AliPayReq**������ǰһ����AliPayReq,ͬʱ���ûص�������setOnAliPayListener

```
       AliPayReq aliPayReq = new AliPayReq.Builder().with(MainActivity.this)
                .setAliPayReqParam(aliPayReqParam)
                .create()
                .setOnAliPayListener(new AliPayReq.OnAliPayListener() {
                    @Override
                    public void onPaySuccess(String resultInfo) {
                        Toast.makeText(MainActivity.this, "֧����֧���ɹ�", Toast.LENGTH_SHORT).show();
                        //TODO �˴�Ϊ΢�Żص��Ľ������֧���ɹ�������ӷ�������ѯһ�¶���״̬�Ƿ����ɣ���Ϊ����֧���ɹ��ˣ����ǻص����ӳ�
                        // TODO ���·��������ݿ�û�и��¡�Ȼ����ʾ�û��Ƿ���ĳɹ�
                    }

                    @Override
                    public void onPayFailure(String resultInfo) {
                        Toast.makeText(MainActivity.this, "֧����֧��ʧ��", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPayConfirmimg(String resultInfo) {
                        Toast.makeText(MainActivity.this, "֧����֧��ȷ����", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPayCancel(String resultInfo) {
                        Toast.makeText(MainActivity.this, "֧����֧��ȡ��", Toast.LENGTH_SHORT).show();

                    }
                });
```

- ��������������

```
//��һ�ֹ��췽��
PayAPI.getInstance().sendPayRequest(aliPayReq);
//�ڶ��ֹ��췽��
PayAPI.getInstance().sendPayRequestByOrderInfo(aliPayReq);

```


# ע���
΢�ź�֧�����ص��Ľ������֧���ɹ�������ӷ�������ѯһ�¶���״̬�Ƿ����ɣ���Ϊ����֧���ɹ��ˣ����ǻص����ӳ� ���·��������ݿ�û�и��¡�Ȼ����ʾ�û��Ƿ���ĳɹ����Ƽ�ʹ��***RxJava*** ***trywhen***������


```
 Disposable orderReqDisposable;
    int mRequestTimes = 0;
    int REPEAT_TIMES = 3;

    private void checkOrderStatus(String id) {
        mRequestTimes = 0;
        showCheckOrderStatusLoadingDialog();
        orderReqDisposable = eliteApi.getVipOrderStstus(id)
                .delay(3, TimeUnit.SECONDS, true)       // ����delayErrorΪtrue����ʾ���ִ����ʱ��Ҳ��Ҫ�ӳ�3s����֪ͨ���ﵽ����������������������ʧ�ܣ�����3s�����¶��ģ�����������
                .subscribeOn(Schedulers.io())
                .repeat(REPEAT_TIMES)   // repeat��֤����ɹ����ܹ����¶��ġ�
                .retry(REPEAT_TIMES)    // retry��֤����ʧ�ܺ������¶���
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseHttpResult<VipOrderStatusBean>>() {
                    @Override
                    public void accept(BaseHttpResult<VipOrderStatusBean> result) throws Exception {
                        //TODO ��ʱ��1Ϊ�ɹ���־
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



# �ο��ĵ�
- ΢�ţ�[https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5](https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5)
- ֧������[https://docs.open.alipay.com/204/105296/](https://docs.open.alipay.com/204/105296/)



