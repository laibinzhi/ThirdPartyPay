package com.lbz.pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public class AliPayReq {

    public static final String TAG = AliPayReq.class.getSimpleName();

    private Activity mActivity;

    private AliPayReqParam mAliPayReqParam;

    @SuppressLint("HandlerLeak")
    private Handler mHandler;

    private static final int SDK_PAY_FLAG = 1;

    public AliPayReq() {
        mHandler = new Handler() {
            @SuppressWarnings("unused")
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SDK_PAY_FLAG: {
                        PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                        String resultInfo = payResult.getResult();

                        String resultStatus = payResult.getResultStatus();

                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            if (mOnAliPayListener != null)
                                mOnAliPayListener.onPaySuccess(resultInfo);
                        } else {
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                if (mOnAliPayListener != null)
                                    mOnAliPayListener.onPayConfirmimg(resultInfo);
                                //用户主动取消支付，"6001"
                            } else if (TextUtils.equals(resultStatus, "6001")) {
                                if (mOnAliPayListener != null)
                                    mOnAliPayListener.onPayCancel(resultInfo);
                            } else {
                                // 其他值就可以判断为支付失败，包括或者系统返回的错误
                                if (mOnAliPayListener != null)
                                    mOnAliPayListener.onPayFailure(resultInfo);
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
            }

            ;
        };
    }

    public void send() {

        final String orderInfo = this.mAliPayReqParam.createOrderInfo();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mActivity);
                Map<String, String> result = alipay.payV2(orderInfo, false);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    public void sendByOrderInfo() {

        final String orderInfo = this.mAliPayReqParam.getAlipay_request();

        Log.e(TAG,"orderInfo="+orderInfo);

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mActivity);
                Map<String, String> result = alipay.payV2(orderInfo, false);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    public static class Builder {
        private Activity mActivity;
        private AliPayReqParam mAliPayReqParam;

        public Builder with(Activity activity) {
            this.mActivity = activity;
            return this;
        }

        public Builder setAliPayReqParam(AliPayReqParam aliPayOrderInfo) {
            this.mAliPayReqParam = aliPayOrderInfo;
            return this;
        }

        public AliPayReq create() {
            AliPayReq aliPayReq = new AliPayReq();
            aliPayReq.mActivity = this.mActivity;
            aliPayReq.mAliPayReqParam = this.mAliPayReqParam;
            return aliPayReq;
        }

    }

    //支付宝支付监听
    private OnAliPayListener mOnAliPayListener;

    public AliPayReq setOnAliPayListener(OnAliPayListener onAliPayListener) {
        this.mOnAliPayListener = onAliPayListener;
        return this;
    }

    /**
     * 支付宝支付监听
     */
    public interface OnAliPayListener {

        void onPaySuccess(String resultInfo);

        void onPayFailure(String resultInfo);

        void onPayConfirmimg(String resultInfo);

        void onPayCancel(String resultInfo);

    }
}
