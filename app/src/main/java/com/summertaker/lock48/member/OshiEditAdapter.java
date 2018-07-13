package com.summertaker.lock48.member;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.lock48.R;
import com.summertaker.lock48.common.BaseDataAdapter;
import com.summertaker.lock48.common.Config;
import com.summertaker.lock48.data.Member;
import com.summertaker.lock48.util.Util;

import java.io.File;
import java.util.ArrayList;

public class OshiEditAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Member> mMembers = new ArrayList<>();
    private OshiEditInterface mOshiEditInterface;

    public OshiEditAdapter(Context context, ArrayList<Member> members, OshiEditInterface oshiEditInterface) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mMembers = members;
        this.mOshiEditInterface = oshiEditInterface;
    }

    @Override
    public int getCount() {
        return mMembers.size();
    }

    @Override
    public Object getItem(int position) {
        return mMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;

        Member member = mMembers.get(position);

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = mLayoutInflater.inflate(R.layout.oshiedit_item, null);

            holder = new ViewHolder();
            holder.ivPicture = view.findViewById(R.id.ivPicture);
            holder.cbLike = view.findViewById(R.id.cbLike);
            holder.tvName = view.findViewById(R.id.tvName);
            //holder.tvNameSelected = view.findViewById(R.id.tvNameSelected);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String imageUrl = member.getPictureUrl(); // member.getThumbnail();

        if (imageUrl == null || imageUrl.isEmpty()) {
            //holder.loLoading.setVisibility(View.GONE);
            //holder.ivThumbnail.setImageResource(R.drawable.placeholder);
        } else {
            String fileName = Util.getUrlToFileName(imageUrl);
            File file = new File(Config.DATA_PATH, fileName);

            if (file.exists()) {
                //holder.loLoading.setVisibility(View.GONE);
                Picasso.with(mContext).load(file).into(holder.ivPicture);
                //Log.d(mTag, fileName + " local loaded.");
            } else {
                Picasso.with(mContext).load(imageUrl).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.e(mTag, "Picasso Image Load Error...");
                    }
                });
            }
        }

        holder.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member m = mMembers.get(position);
                mOshiEditInterface.onPictureClick(m);
            }
        });

        holder.cbLike.setChecked(member.isSelected());
        holder.cbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member m = mMembers.get(position);
                mOshiEditInterface.onLikeClick((CheckBox) view, m);
            }
        });

        holder.tvName.setText(member.getName());
        /*
        if (member.isSelected()) {
            holder.tvName.setVisibility(View.GONE);
            holder.tvNameSelected.setText(member.getName());
            holder.tvNameSelected.setVisibility(View.VISIBLE);
        } else {
            holder.tvName.setText(member.getName());
            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvNameSelected.setVisibility(View.GONE);
        }
        */

        return view;
    }

    static class ViewHolder {
        ImageView ivPicture;
        CheckBox cbLike;
        TextView tvName;
        //TextView tvNameSelected;
    }
}
