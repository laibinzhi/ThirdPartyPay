package com.lbz.pay_sample;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class SelectPayPopupWindow extends PopupWindow implements View.OnClickListener {

    private View mMenuView;
    private View mParentView;
    private RadioButton paytype_of_wechat_rb, paytype_of_alipay_rb;
    private LinearLayout paytype_of_wechat, paytype_of_alipay;
    private TextView priceTv;
    private TextView pay;


    public SelectPayPopupWindow(Context context, String price) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.select_pay_popup_window, null);
        paytype_of_wechat = mMenuView.findViewById(R.id.paytype_of_wechat);
        paytype_of_alipay = mMenuView.findViewById(R.id.paytype_of_alipay);
        paytype_of_wechat_rb = mMenuView.findViewById(R.id.paytype_of_wechat_rb);
        paytype_of_alipay_rb = mMenuView.findViewById(R.id.paytype_of_alipay_rb);
        priceTv = mMenuView.findViewById(R.id.price);
        pay = mMenuView.findViewById(R.id.pay);
        priceTv.setText(price);
        paytype_of_wechat.setOnClickListener(this);
        paytype_of_alipay.setOnClickListener(this);
        paytype_of_wechat_rb.setOnClickListener(this);
        paytype_of_alipay_rb.setOnClickListener(this);

        RxView.clicks(pay).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object obj) throws Exception {
                        if (paytype_of_wechat_rb.isChecked()) {
                            mISelectPayWayListener.payByWeChat();
                        } else {
                            mISelectPayWayListener.payByAliPay();
                        }
                    }
                });

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.paytype_of_wechat:
                if (paytype_of_alipay_rb.isChecked()) {
                    paytype_of_alipay_rb.setChecked(false);
                }
                if (!paytype_of_wechat_rb.isChecked()) {
                    paytype_of_wechat_rb.setChecked(true);
                }
                break;
            case R.id.paytype_of_alipay:
                if (paytype_of_wechat_rb.isChecked()) {
                    paytype_of_wechat_rb.setChecked(false);
                }
                if (!paytype_of_alipay_rb.isChecked()) {
                    paytype_of_alipay_rb.setChecked(true);
                }
                break;
            case R.id.paytype_of_wechat_rb:
                if (paytype_of_alipay_rb.isChecked()) {
                    paytype_of_alipay_rb.setChecked(false);
                }
                if (!paytype_of_wechat_rb.isChecked()) {
                    paytype_of_wechat_rb.setChecked(true);
                }
                break;
            case R.id.paytype_of_alipay_rb:
                if (paytype_of_wechat_rb.isChecked()) {
                    paytype_of_wechat_rb.setChecked(false);
                }
                if (!paytype_of_alipay_rb.isChecked()) {
                    paytype_of_alipay_rb.setChecked(true);
                }
                break;


        }

    }


    public SelectPayPopupWindow show() {
        showAtLocation(this.mParentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        return this;
    }

    private ISelectPayWayListener mISelectPayWayListener;

    public void setOnAliPayListener(ISelectPayWayListener iSelectPayWayListener) {
        this.mISelectPayWayListener = iSelectPayWayListener;
    }

    public interface ISelectPayWayListener {

        void payByAliPay();

        void payByWeChat();
    }

    public static class Builder {
        private Context mContext;
        private String mPrice;
        private View mParentView;

        public Builder with(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setPrice(String price) {
            this.mPrice = price;
            return this;
        }

        public Builder setParentView(View view) {
            this.mParentView = view;
            return this;
        }

        public SelectPayPopupWindow create() {
            SelectPayPopupWindow selectPayPopupWindow = new SelectPayPopupWindow(this.mContext, this.mPrice);
            selectPayPopupWindow.mParentView = this.mParentView;
            return selectPayPopupWindow;
        }

    }

}
