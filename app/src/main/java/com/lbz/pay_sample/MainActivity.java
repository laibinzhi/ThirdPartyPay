package com.lbz.pay_sample;

import android.Manifest;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.lbz.pay.PayAPI;
import com.lbz.pay.alipay.AliPayReq;
import com.lbz.pay.alipay.AliPayReqParam;
import com.lbz.pay.wechat.WeChatPayReq;
import com.lbz.pay.wechat.WeChatReqParam;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initToolbar();
        initCheckOutBtn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initCheckOutBtn() {

        findViewById(R.id.btn_checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPayPopupWindow selectPayPopupWindow = new SelectPayPopupWindow.Builder().with(MainActivity.this)
                        .setPrice("120")
                        .setParentView(findViewById(R.id.parent_view))
                        .create()
                        .show();
                selectPayPopupWindow.setOnAliPayListener(new SelectPayPopupWindow.ISelectPayWayListener() {
                    @Override
                    public void payByAliPay() {
                        new RxPermissions(MainActivity.this)
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean granted) throws Exception {
                                        if (granted) {
                                            AliPay();
                                        }
                                    }
                                });


                    }

                    @Override
                    public void payByWeChat() {
                        weChatPay();
                    }
                });
            }
        });

    }

    private void showLoading() {
    }

    private void hideLoading() {

    }

    private void AliPay() {
        //以下数据从服务器获取
        String app_id = "2014072300007148";//支付宝分配给开发者的应用ID
        String sign = "thissignthis";//商户请求参数的签名串，详见签名
        String timestamp = "2014-07-24 03:07:50";//发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
        String notify_url = "https://api.xx.com/receive_notify.htm";//支付宝服务器主动通知商户服务器里指定的页面http/https路径。建议商户使用https

        //业务请求参数集合
        String subject = "大乐透";//商品的标题/交易标题/订单标题/订单关键字等。
        String out_trade_no = "70501111111S001111119";//商户网站唯一订单号
        String total_amount = "9.00";//订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
        //以上数据从服务器获取

        String orderinfo = "orderinfo";//完整请求信息

//        AliPayReqParam aliPayReqParam = new AliPayReqParam(app_id, sign, timestamp, notify_url, subject, out_trade_no, total_amount);
        AliPayReqParam aliPayReqParam = new AliPayReqParam(orderinfo);
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
//        PayAPI.getInstance().sendPayRequest(aliPayReq);
        PayAPI.getInstance().sendPayRequestByOrderInfo(aliPayReq);

    }

    private void weChatPay() {
        //以下下数据从服务器获取
        String nonce_str = "";
        String sign = "";
        String prepay_id = "";
        String mch_id = "";
        String appid = "";
        String wx_key = "";
        //以上数据从服务器获取

        WeChatReqParam weChatReqParam = new WeChatReqParam(nonce_str, sign, prepay_id, mch_id, appid, wx_key);
        WeChatPayReq weChatPayReq = new WeChatPayReq.Builder().with(MainActivity.this)
                .setWeChatReqParam(weChatReqParam)
                .create();
        PayAPI.getInstance().sendPayRequest(weChatPayReq);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("结算");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

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

}
