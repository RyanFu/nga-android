package com.yulingtech.lycommon.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yulingtech.lycommon.R;

/**
 * @author XZQ
 * @version
 */
public class ZakerProgressDialog extends Dialog {

    public Context context;// 上下文

    public ZakerProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public ZakerProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    public ZakerProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.zaker_loading, null); // 加载自己定义的布局
        ImageView img_loading = (ImageView) view.findViewById(R.id.img_load);
        RelativeLayout img_close = (RelativeLayout) view.findViewById(R.id.img_cancel);
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.refresh); // 加载XML文件中定义的动画
        img_loading.setAnimation(rotateAnimation);// 开始动画
        setContentView(view);// 为Dialoge设置自己定义的布局
        // 为close的那个文件添加事件
        img_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
