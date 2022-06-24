package com.itheima.qq15.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.itheima.qq15.R;

import java.util.Date;
import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-10-19 14:42
 * 网址：http://www.itheima.com
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private List<EMConversation> mEMConversationList;

    public ConversationAdapter(List<EMConversation> EMConversationList) {
        mEMConversationList = EMConversationList;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conversation, parent, false);
        ConversationViewHolder conversationViewHolder = new ConversationViewHolder(view);
        return conversationViewHolder;
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        final EMConversation emConversation = mEMConversationList.get(position);
        //聊天的对方的名称
        String userName = emConversation.getUserName();
        int unreadMsgCount = emConversation.getUnreadMsgCount();
        EMMessage lastMessage = emConversation.getLastMessage();
        long msgTime = lastMessage.getMsgTime();
        EMTextMessageBody lastMessageBody = (EMTextMessageBody) lastMessage.getBody();
        String lastMessageBodyMessage = lastMessageBody.getMessage();

        holder.mTvMsg.setText(lastMessageBodyMessage);
        holder.mTvUsername.setText(userName);
        holder.mTvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));
        if (unreadMsgCount>99){
            holder.mTvUnread.setVisibility(View.VISIBLE);
            holder.mTvUnread.setText("99+");
        }else if (unreadMsgCount>0){
            holder.mTvUnread.setVisibility(View.VISIBLE);
            holder.mTvUnread.setText(unreadMsgCount+"");
        }else{
            holder.mTvUnread.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClick(emConversation);
                }
            }
        });
    }

    public interface OnItemClickListener{
        void onItemClick(EMConversation conversation);
    }
    private OnItemClickListener mOnItemClickListener;
    public  void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mEMConversationList==null?0:mEMConversationList.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder{

        TextView mTvUsername;
        TextView mTvTime;
        TextView mTvMsg;
        TextView mTvUnread;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            mTvUnread = (TextView) itemView.findViewById(R.id.tv_unread);
        }
    }

}
