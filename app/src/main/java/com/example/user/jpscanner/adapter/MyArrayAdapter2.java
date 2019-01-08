package com.example.user.jpscanner.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.jpscanner.R;
import com.example.user.jpscanner.model.MyDataModel2;

import java.util.List;

public class MyArrayAdapter2 extends ArrayAdapter<MyDataModel2> {
    List<MyDataModel2> modelList;
    Context context;
    private LayoutInflater mInflater;


    public MyArrayAdapter2(Context context, List<MyDataModel2> objects) {
        super(context, 0, objects);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        modelList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyArrayAdapter2.ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.layout2, parent, false);
            vh = MyArrayAdapter2.ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (MyArrayAdapter2.ViewHolder) convertView.getTag();
        }

        MyDataModel2 item = getItem(position);

        vh.textViewName.setText(item.getnama());
        vh.textViewName.setTypeface(Typeface.createFromAsset(parent.getContext().getAssets(),"American_Typewriter_Regular.ttf"));
        vh.textViewJenis.setText(item.getjenis());
        vh.textViewJenis.setTypeface(Typeface.createFromAsset(parent.getContext().getAssets(),"American_Typewriter_Regular.ttf"));

        vh.textViewTime.setText(item.gettime());
        vh.textViewTime.setTypeface(Typeface.createFromAsset(parent.getContext().getAssets(),"American_Typewriter_Regular.ttf"));


        return vh.rootView;
    }

    @Override
    public MyDataModel2 getItem(int position) {
        return modelList.get(position);
    }

    private static class ViewHolder {
        public final RelativeLayout rootView;
        public final TextView textViewName;
        public final TextView textViewTime;
        public final TextView textViewJenis;
        private ViewHolder(RelativeLayout rootView, TextView textViewName, TextView textViewTime, TextView textViewJenis) {
            this.rootView = rootView;
            this.textViewName = textViewName;
            this.textViewTime = textViewTime;
            this.textViewJenis = textViewJenis;
        }

        public static MyArrayAdapter2.ViewHolder create(RelativeLayout rootView) {
            TextView textViewName = (TextView) rootView.findViewById(R.id.textViewName);
            TextView textViewJenis = (TextView) rootView.findViewById(R.id.textViewjenis);
            TextView textViewtime = (TextView) rootView.findViewById(R.id.textViewtime);
            return new MyArrayAdapter2.ViewHolder(rootView, textViewName, textViewJenis,textViewtime);
        }
    }
}

