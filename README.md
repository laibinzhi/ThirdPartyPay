# ���
��װ֧������΢�ŵ�����֧����һ����
# ���ټ���
1. �����
- ����һ����������[��Ŀ](https://github.com/laibinzhi/ThirdPartyPay),��moudleģ��***mypay***�����ļ��и��Ƶ�����Ŀ��Ŀ¼����app��build.gradle�е�***dependencies***������´���

```
    implementation project(':mypay')

```

Ȼ����settings.gradle�м���

```
    include ':app', ':mypay'

```
Ȼ��rebuildһ�¾�ok�ˡ�

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
 implementation 'com.lbz.pay:third_party_pay:0.0.1'
```

rebuildһ�¾�ok��

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
  private String body;//��һ�ʽ��׵ľ���������Ϣ������Ƕ�����Ʒ���뽫��Ʒ�����ַ����ۼӴ���body��
    private String seller_id;//����֧�����˺�
    private String total_fee;//���
    private String service;//�ӿ����ƣ��̶�ֵ��
    private String _input_charset;//�̻���վʹ�õı����ʽ���̶�ΪUTF-8��
    private String sign;
    private String out_trade_no;//�̻���վΨһ������
    private String payment_type;//֧�����͡�Ĭ��ֵΪ��1����Ʒ���򣩡�
    private String notify_url;//֧��������������֪ͨ�̻���վ��ָ����ҳ��http·����
    private String sign_type;//ǩ�����ͣ�Ŀǰ��֧��RSA��
    private String partner;//ǩԼ��֧�����˺Ŷ�Ӧ��֧����Ψһ�û��š���2088��ͷ��16λ��������ɡ�
    private String subject;//��Ʒ�ı���/���ױ���/��������/�����ؼ��ֵȡ�
  
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
����Ϊ**AliPayReqParam**�Ĺ���������������ϲ���ͨ�����������رȽϰ�ȫ��

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
              PayAPI.getInstance().sendPayRequest(aliPayReq);


```


# ע���
΢�ź�֧�����ص��Ľ������֧���ɹ�������ӷ�������ѯһ�¶���״̬�Ƿ����ɣ���Ϊ����֧���ɹ��ˣ����ǻص����ӳ� ���·��������ݿ�û�и��¡�Ȼ����ʾ�û��Ƿ���ĳɹ����Ƽ�ʹ��***RxJava*** ***trywhen***������


# �ο��ĵ�
- ΢�ţ�[https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5](https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5)
- ֧������[https://docs.open.alipay.com/59/103662](https://docs.open.alipay.com/59/103662)



