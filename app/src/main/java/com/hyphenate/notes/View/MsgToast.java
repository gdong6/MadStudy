package com.hyphenate.notes.View;



import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeim.R;


public class MsgToast {

    public static void showToast(Context context, String message) {

            TextView mTextView;

            View toastRoot = LayoutInflater.from(context).inflate(R.layout.msg_toast, null);

            mTextView = (TextView) toastRoot.findViewById(R.id.msg_toast);

            mTextView.setText(message);

            Toast toastStart = new Toast(context);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int height = wm.getDefaultDisplay().getHeight();

            toastStart.setGravity(Gravity.TOP, 0, height / 2);
            toastStart.setDuration(Toast.LENGTH_SHORT);
            toastStart.setView(toastRoot);
            toastStart.show();
        }

}
