package com.nulp.daliavskyimusic.uiComponents;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nulp.daliavskyimusic.R;
import com.nulp.daliavskyimusic.logicComponents.parser.ItemInform;

import java.util.List;


public class MusicItemAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater lInflater;
    private List<ItemInform> objects;
    private String selectedHref = "";

    public void setSelectedHref(String selectedHref) {
        this.selectedHref = selectedHref;
    }

    public MusicItemAdapter(Context context, List<ItemInform> products) {
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_item, parent, false);
        }
        LinearLayout back_lay = (LinearLayout)view.findViewById(R.id.back_lay);
        RoundedImageView flagView = view.findViewById(R.id.item_image);
        TextView author = (TextView) view.findViewById(R.id.item_author);
        TextView song = (TextView) view.findViewById(R.id.item_song);
        TextView len = (TextView) view.findViewById(R.id.item_len);
        ItemInform state = objects.get(position);

        if(selectedHref.equals(state.getSong_href())){
            back_lay.setBackgroundColor(Color.argb(120,250, 15, 15));
        } else {
            back_lay.setBackgroundColor(Color.WHITE);
        }

        author.setText(state.getAuthor_name());
        song.setText(state.getSong_name());
        len.setText(state.getLen());
        if(!state.getImage_href().equals("no_image"))
        {
            Glide
                    .with(ctx)
                    .load(state.getImage_href())
                    .placeholder(R.drawable.face)
                    .into(flagView);
        }

        return view;
    }

}

