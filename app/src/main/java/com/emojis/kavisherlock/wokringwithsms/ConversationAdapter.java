package com.emojis.kavisherlock.wokringwithsms;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harryarakkal on 1/5/17.
 */

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int user = 1;
    private final int responder = 2;
    private Context context;
    private List<Text> texts;

    public ConversationAdapter(Context context, List<Text> texts){
        this.context = context;
        this.texts = texts;
    }

    @Override
    public int getItemViewType(int position) {
        Text text = texts.get(position);
        switch(text.origin) {
            case "self":
                return user;
            default:
                return responder;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case user:
                View textView = LayoutInflater.from(context).inflate(R.layout.user_message, parent, false);
                return new UserViewHolder(textView);
            default:
                textView = LayoutInflater.from(context).inflate(R.layout.responder_message, parent, false);
                return new ResponderViewHolder(textView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Text text = texts.get(position);
        TextView message;
        if (text.origin == "self"){
            message = ((UserViewHolder) holder).message;
            message.setText(text.text);
        }else{
            message = ((ResponderViewHolder) holder).message;
            message.setText(text.text);
        }
    }

    @Override
    public int getItemCount() {
        return texts.size();
    }

    private static class UserViewHolder extends RecyclerView.ViewHolder{
        public TextView message;

        public UserViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.user_message_text);
        }
    }

    private static class ResponderViewHolder extends RecyclerView.ViewHolder{
        public TextView message;

        public ResponderViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.responder_message_text);
        }
    }
}
