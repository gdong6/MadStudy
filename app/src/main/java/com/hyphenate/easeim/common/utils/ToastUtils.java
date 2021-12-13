package com.hyphenate.easeim.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.R;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.lang.reflect.Field;


public class ToastUtils {
    private static final int DEFAULT = 0;
    private static final int SUCCESS = 1;
    private static final int FAIL = 2;
    private static final int TOAST_LAST_TIME = 1000;
    private static Toast toast;


    public static void showSuccessToast(String message) {
        showCenterToast(null, message, SUCCESS, TOAST_LAST_TIME);
    }


    public static void showSuccessToast(@StringRes int message) {
        showCenterToast(0, message, SUCCESS, TOAST_LAST_TIME);
    }


    public static void showFailToast(String message) {
        showCenterToast(null, message, FAIL, TOAST_LAST_TIME);
    }


    public static void showFailToast(@StringRes int message) {
        showCenterToast(0, message, FAIL, TOAST_LAST_TIME);
    }

    public static void showToast(String message) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showBottomToast(null, message, DEFAULT, TOAST_LAST_TIME);
    }


    public static void showToast(@StringRes int message) {
        showBottomToast(0, message, DEFAULT, TOAST_LAST_TIME);
    }


    public static void showSuccessToast(String title, String message) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(title, message, SUCCESS, TOAST_LAST_TIME);
    }


    public static void showSuccessToast(@StringRes int title, @StringRes int message) {
        showCenterToast(title, message, SUCCESS, TOAST_LAST_TIME);
    }


    public static void showFailToast(String title, String message) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(title, message, FAIL, TOAST_LAST_TIME);
    }


    public static void showFailToast(@StringRes int title, @StringRes int message) {
        showCenterToast(title, message, FAIL, TOAST_LAST_TIME);
    }

    public static void showSuccessToast(String title, String message, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(title, message, SUCCESS, duration);
    }


    public static void showSuccessToast(@StringRes int title, @StringRes int message, int duration) {
        showCenterToast(title, message, SUCCESS, duration);
    }


    public static void showFailToast(String title, String message, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(title, message, FAIL, duration);
    }


    public static void showFailToast(@StringRes int title, @StringRes int message, int duration) {
        showCenterToast(title, message, FAIL, duration);
    }


    public static void showToast(String message, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(null, message, DEFAULT, duration);
    }


    public static void showToast(@StringRes int message, int duration) {
        showCenterToast(0, message, DEFAULT, duration);
    }


    public static void showCenterToast(String title, String message, int type, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showToast(DemoApplication.getInstance(), title, message, type, duration, Gravity.CENTER);
    }


    public static void showCenterToast(@StringRes int title, @StringRes int message, int type, int duration) {
        showToast(DemoApplication.getInstance(), title, message, type, duration, Gravity.CENTER);
    }


    public static void showBottomToast(String title, String message, int type, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showToast(DemoApplication.getInstance(), title, message, type, duration, Gravity.BOTTOM);
    }


    public static void showBottomToast(@StringRes int title, @StringRes int message, int type, int duration) {
        showToast(DemoApplication.getInstance(), title, message, type, duration, Gravity.BOTTOM);
    }


    public static void showToast(Context context, @StringRes int title, @StringRes int message, int type, int duration, int gravity) {
        showToast(context, title == 0 ? null:context.getString(title), context.getString(message), type, duration, gravity);
    }


    public static void showToast(Context context, String title, String message, int type, int duration, int gravity) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        EaseThreadManager.getInstance().runOnMainThread(() -> {
            if(toast != null) {
                toast.cancel();
            }
            toast = getToast(context, title, message, type, duration, gravity);
            toast.show();
        });

    }

    private static Toast getToast(Context context, String title, String message, int type, int duration, int gravity) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.demo_toast_layout, null);
        toast.setView(toastView);
        ImageView ivToast = toastView.findViewById(R.id.iv_toast);
        TextView tvToastTitle = toastView.findViewById(R.id.tv_toast_title);
        TextView tvToastContent = toastView.findViewById(R.id.tv_toast_content);
        if(TextUtils.isEmpty(title)) {
            tvToastTitle.setVisibility(View.GONE);
        }else {
            tvToastTitle.setVisibility(View.VISIBLE);
            tvToastTitle.setText(title);
        }

        if(!TextUtils.isEmpty(message)) {
            tvToastContent.setText(message);
        }

        ivToast.setVisibility(View.VISIBLE);
        if(type == SUCCESS) {
            ivToast.setImageResource(R.drawable.em_toast_success);
        }else if(type == FAIL) {
            ivToast.setImageResource(R.drawable.em_toast_fail);
        }else {
            ivToast.setVisibility(View.GONE);
        }
        int yOffset = 0;
        if(gravity == Gravity.BOTTOM || gravity == Gravity.TOP) {
            yOffset = (int) EaseCommonUtils.dip2px(context, 50);
        }
        toast.setDuration(duration);
        toast.setGravity(gravity, 0, yOffset);
        hookToast(toast);
        return toast;
    }

    private static void hookToast(Toast toast) {
        Class<Toast> cToast = Toast.class;
        try {
            Field fTn = cToast.getDeclaredField("mTN");
            fTn.setAccessible(true);

            Object oTn = fTn.get(toast);
            Class<?> cTn = oTn.getClass();
            Field fHandle = cTn.getDeclaredField("mHandler");

            fHandle.setAccessible(true);
            fHandle.set(oTn, new HandlerProxy((Handler) fHandle.get(oTn)));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class HandlerProxy extends Handler {

        private Handler mHandler;

        public HandlerProxy(Handler handler) {
            this.mHandler = handler;
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                mHandler.handleMessage(msg);
            } catch (WindowManager.BadTokenException e) {
                //ignore
            }
        }
    }


}
