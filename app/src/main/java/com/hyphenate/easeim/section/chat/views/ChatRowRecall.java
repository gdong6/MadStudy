package com.hyphenate.easeim.section.chat.views;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.R;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

public class ChatRowRecall extends EaseChatRow {
    private TextView contentView;

    public ChatRowRecall(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.demo_row_recall_message, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.text_content);
    }

    @Override
    protected void onSetUpView() {
        String messageStr = null;
        if (message.direct() == EMMessage.Direct.SEND) {
            messageStr = String.format("You recalled a message");
        } else {
            messageStr = String.format("%1$s recalled a message", message.getFrom());
        }
        contentView.setText(messageStr);
    }
}
