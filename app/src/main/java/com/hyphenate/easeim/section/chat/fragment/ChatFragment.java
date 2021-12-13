package com.hyphenate.easeim.section.chat.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.MainActivity;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.TimerActivity;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.model.EmojiconExampleGroupData;

import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.chat.activity.ForwardMessageActivity;
import com.hyphenate.easeim.section.chat.activity.ImageGridActivity;
import com.hyphenate.easeim.section.chat.activity.PickAtUserActivity;
import com.hyphenate.easeim.section.chat.activity.SelectUserCardActivity;
import com.hyphenate.easeim.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeim.section.conference.ConferenceInviteActivity;
import com.hyphenate.easeim.section.contact.activity.ContactDetailActivity;
import com.hyphenate.easeim.section.dialog.DemoDialogFragment;
import com.hyphenate.easeim.section.dialog.DemoListDialogFragment;
import com.hyphenate.easeim.section.dialog.FullEditDialogFragment;
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment;
import com.hyphenate.easeim.section.group.GroupHelper;
import com.hyphenate.easeim.section.me.activity.UserDetailActivity;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.hyphenate.easeui.modules.chat.interfaces.IChatExtendMenu;
import com.hyphenate.easeui.modules.chat.interfaces.OnRecallMessageResultListener;
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper;
import com.hyphenate.easeui.modules.menu.MenuItemBean;
import com.hyphenate.util.EMFileHelper;
import com.hyphenate.util.EMLog;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;



public class ChatFragment extends EaseChatFragment implements OnRecallMessageResultListener {
    private static final String TAG = ChatFragment.class.getSimpleName();
    private static final int REQUEST_CODE_SELECT_USER_CARD = 20;
    private MessageViewModel viewModel;
    protected ClipboardManager clipboard;

    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    //    private static final String[] calls = {"Video", "Voice"};
    private OnFragmentInfoListener infoListener;
    private Dialog dialog;
    private int seconds;

    @Override
    public void initView() {
        super.initView();
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);


    }

    private void addItemMenuAction() {
        MenuItemBean itemMenu = new MenuItemBean(0, R.id.action_chat_forward, 11, "Share");
        itemMenu.setResourceId(R.drawable.ease_chat_item_menu_forward);
        chatLayout.addItemMenu(itemMenu);
    }

    private void resetChatExtendMenu() {
        IChatExtendMenu chatExtendMenu = chatLayout.getChatInputMenu().getChatExtendMenu();
        chatExtendMenu.clear();
        chatExtendMenu.registerMenuItem("Albums", R.drawable.ease_chat_image_selector, R.id.extend_item_picture);
        chatExtendMenu.registerMenuItem("Pictures", R.drawable.ease_chat_takepic_selector, R.id.extend_item_take_picture);

//        chatExtendMenu.registerMenuItem("Position", R.drawable.ease_chat_location_selector, R.id.extend_item_location);
        chatExtendMenu.registerMenuItem("File", R.drawable.em_chat_file_selector, R.id.extend_item_file);

        chatExtendMenu.registerMenuItem(R.string.attach_user_card, R.drawable.em_chat_user_card_selector, R.id.extend_item_user_card);

        if (chatType == EaseConstant.CHATTYPE_GROUP && EMClient.getInstance().getOptions().getRequireAck()) {
            EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(conversationId);
            if (GroupHelper.isOwner(group)) {
                chatExtendMenu.registerMenuItem(R.string.em_chat_group_delivery_ack, R.drawable.demo_chat_delivery_selector, R.id.extend_item_delivery);
            }
        }

        chatLayout.getChatInputMenu().getEmojiconMenu().addEmojiconGroup(EmojiconExampleGroupData.getData());
    }

    @Override
    public void initListener() {
        super.initListener();
        chatLayout.setOnRecallMessageResultListener(this);
    }

    final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mTvContent.setText(String.valueOf(msg.obj));
            }
        }
    };


    private TextView mTvContent;

    @Override
    public void initData() {
        super.initData();
        resetChatExtendMenu();
        addItemMenuAction();
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            chatLayout.getChatInputMenu().setVisibility(View.GONE);
            chatLayout.getChatMessageListLayout().setVisibility(View.GONE);
            View view = View.inflate(getActivity(), R.layout.layout, null);
            chatLayout.addView(view);
            mTvContent = view.findViewById(R.id.tv_content);
            setTime();
            view.findViewById(R.id.tv_leave).setOnClickListener(v -> {
                getActivity().finish();
            });

            view.findViewById(R.id.backgroundImageChangeButton).setOnClickListener(v -> {

                String[] colors = {"#FF7889","#8a5082","#5f5f90","#758eb7","#a5cad2"};
                Random rand = new Random();
                view.setBackgroundColor(Color.parseColor(colors[rand.nextInt(colors.length)]));
            });


        }


        chatLayout.getChatInputMenu().getPrimaryMenu().getEditText().setText(getUnSendMsg());
        chatLayout.turnOnTypingMonitor(DemoHelper.getInstance().getModel().isShowMsgTyping());

        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));

        LiveDataBus.get().with(DemoConstant.MESSAGE_CALL_SAVE, Boolean.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event) {
                chatLayout.getChatMessageListLayout().refreshToLatest();
            }
        });

        LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });

        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshToLatest();
            }
        });
        LiveDataBus.get().with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });

        LiveDataBus.get().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event != null) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });

        LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event != null) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });
    }





    private void setTime() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                int hour = seconds / 3600 % 24;
                int minute = seconds % 3600 / 60;
                String time = String.format("%02d:%02d:%02d", hour, minute, seconds % 60);
                Message message = new Message();
                message.obj = time;
                message.what = 1;
                handler.sendMessage(message);
                seconds++;
                handler.postDelayed(this, 1000);
                if(seconds ==10){
                    AlertDialog.Builder builder
                            = new AlertDialog
                            .Builder((BaseActivity) mContext);

                    // Set the message show for the Alert time
                    builder.setMessage("Jump round!\n"+"You have already studied "+minute+" minutes." );

                    // Set Alert Title
                    builder.setTitle("Congratulations!");
                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();

                    // Show the Alert Dialog box
                    alertDialog.show();
                    new CountDownTimer(5000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {

                            alertDialog.dismiss();
                        }
                    }.start();
                }
            }
        });
    }

    private void showDeliveryDialog() {
        new FullEditDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.em_chat_group_read_ack)
                .setOnConfirmClickListener(R.string.em_chat_group_read_ack_send, new FullEditDialogFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String content) {
                        chatLayout.sendTextMessage(content, true);
                    }
                })
                .setConfirmColor(R.color.em_color_brand)
                .setHint(R.string.em_chat_group_read_ack_hint)
                .show();
    }

//    private void showSelectDialog() {
//        new DemoListDialogFragment.Builder((BaseActivity) mContext)
//                //.setTitle(R.string.em_single_call_type)
//                .setData(åcalls)
//                .setCancelColorRes(R.color.black)
//                .setWindowAnimations(R.style.animate_dialog)
//                .setOnItemClickListener(new DemoListDialogFragment.OnDialogItemClickListener() {
//                    @Override
//                    public void OnItemClick(View view, int position) {
//                        switch (position) {
//                            case 0:
//                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL, conversationId, null, VideoCallActivity.class);
//                                break;
//                            case 1:
//                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL, conversationId, null, VideoCallActivity.class);
//                                break;
//                        }
//                    }
//                })
//                .show();
//    }

    @Override
    public void onUserAvatarClick(String username) {
        if (!TextUtils.equals(username, DemoHelper.getInstance().getCurrentUser())) {
            EaseUser user = DemoHelper.getInstance().getUserInfo(username);
            if (user == null) {
                user = new EaseUser(username);
            }
            boolean isFriend = DemoHelper.getInstance().getModel().isContact(username);
            if (isFriend) {
                user.setContact(0);
            } else {
                user.setContact(3);
            }
            ContactDetailActivity.actionStart(mContext, user);
        } else {
            UserDetailActivity.actionStart(mContext, null, null);
        }
    }

    @Override
    public void onUserAvatarLongClick(String username) {

    }

    @Override
    public boolean onBubbleLongClick(View v, EMMessage message) {
        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!chatLayout.getChatMessageListLayout().isGroupChat()) {
            return;
        }
        if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
            PickAtUserActivity.actionStartForResult(ChatFragment.this, conversationId, REQUEST_CODE_SELECT_AT_USER);
        }
    }


    @Override
    public boolean onBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onChatExtendMenuItemClick(View view, int itemId) {
        super.onChatExtendMenuItemClick(view, itemId);
        switch (itemId) {

            case R.id.extend_item_conference_call:
                Intent intent = new Intent(getContext(), ConferenceInviteActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_ID, conversationId);
                getContext().startActivity(intent);
                break;
            case R.id.extend_item_delivery:
                showDeliveryDialog();
                break;
            case R.id.extend_item_user_card:
                EMLog.d(TAG, "select user card");
                Intent userCardIntent = new Intent(this.getContext(), SelectUserCardActivity.class);
                userCardIntent.putExtra("toUser", conversationId);
                startActivityForResult(userCardIntent, REQUEST_CODE_SELECT_USER_CARD);
                break;
        }
    }

    @Override
    public void onChatError(int code, String errorMsg) {
        if (infoListener != null) {
            infoListener.onChatError(code, errorMsg);
        }
    }

    @Override
    public void onOtherTyping(String action) {
        if (infoListener != null) {
            infoListener.onOtherTyping(action);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        chatLayout.inputAtUsername(username, false);
                    }
                    break;

                case REQUEST_CODE_SELECT_USER_CARD:
                    if (data != null) {
                        EaseUser user = (EaseUser) data.getSerializableExtra("user");
                        if (user != null) {
                            sendUserCardMessage(user);
                        }
                    }
                    break;
            }
        }
    }

    private void sendUserCardMessage(EaseUser user) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(DemoConstant.USER_CARD_EVENT);
        Map<String, String> params = new HashMap<>();
        params.put(DemoConstant.USER_CARD_ID, user.getUsername());
        params.put(DemoConstant.USER_CARD_NICK, user.getNickname());
        params.put(DemoConstant.USER_CARD_AVATAR, user.getAvatar());
        body.setParams(params);
        message.setBody(body);
        message.setTo(conversationId);
        chatLayout.sendMessage(message);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mContext != null && mContext.isFinishing()) {
            if (chatLayout.getChatInputMenu() != null) {
                saveUnSendMsg(chatLayout.getInputContent());
                LiveDataBus.get().with(DemoConstant.MESSAGE_NOT_SEND).postValue(true);
            }
        }
    }

    private void saveUnSendMsg(String content) {
        DemoHelper.getInstance().getModel().saveUnSendMsg(conversationId, content);
    }

    private String getUnSendMsg() {
        return DemoHelper.getInstance().getModel().getUnSendMsg(conversationId);
    }

    @Override
    public void onPreMenu(EasePopupWindowHelper helper, EMMessage message) {
        if (System.currentTimeMillis() - message.getMsgTime() > 2 * 60 * 1000) {
            helper.findItemVisible(R.id.action_chat_recall, false);
        }
        EMMessage.Type type = message.getType();
        helper.findItemVisible(R.id.action_chat_forward, false);
        switch (type) {
            case TXT:
                if (!message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)
                        && !message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    helper.findItemVisible(R.id.action_chat_forward, true);
                }
                break;
            case IMAGE:
                helper.findItemVisible(R.id.action_chat_forward, true);
                break;
        }

        if (chatType == DemoConstant.CHATTYPE_CHATROOM) {
            helper.findItemVisible(R.id.action_chat_forward, true);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItemBean item, EMMessage message) {
        switch (item.getItemId()) {
            case R.id.action_chat_forward:
                ForwardMessageActivity.actionStart(mContext, message.getMsgId());
                return true;
            case R.id.action_chat_delete:
                showDeleteDialog(message);
                return true;
            case R.id.action_chat_recall:
                showProgressBar();
                chatLayout.recallMessage(message);
                return true;
        }
        return false;
    }

    private void showProgressBar() {
        View view = View.inflate(mContext, R.layout.demo_layout_progress_recall, null);
        dialog = new Dialog(mContext, R.style.dialog_recall);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(view, layoutParams);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showDeleteDialog(EMMessage message) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(getString(R.string.em_chat_delete_title))
                .setConfirmColor(R.color.red)
                .setOnConfirmClickListener(getString(R.string.delete), new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        chatLayout.deleteMessage(message);
                    }
                })
                .showCancelButton(true)
                .show();
    }

    public void setOnFragmentInfoListener(OnFragmentInfoListener listener) {
        this.infoListener = listener;
    }

    @Override
    public void recallSuccess(EMMessage message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void recallFail(int code, String errorMsg) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public interface OnFragmentInfoListener {
        void onChatError(int code, String errorMsg);

        void onOtherTyping(String action);
    }
}