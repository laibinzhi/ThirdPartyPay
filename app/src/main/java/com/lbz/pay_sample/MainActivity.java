package com.lbz.pay_sample;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
                        AliPay();
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
        String body = "";
        String seller_id = "";
        String total_fee = "";
        String service = "";
        String _input_charset = "";
        String sign = "";
        String out_trade_no = "";
        String payment_type = "";
        String notify_url = "";
        String sign_type = "";
        String partner = "";
        String subject = "";
        //以上数据从服务器获取


        AliPayReqParam aliPayReqParam = new AliPayReqParam(body, seller_id, total_fee, service, _input_charset, sign, out_trade_no, payment_type, notify_url, sign_type, partner, subject);
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
        PayAPI.getInstance().sendPayRequest(aliPayReq);

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
