package com.klimovmax.myapplication21.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.klimovmax.myapplication21.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<String> imageUris;

    public ImageAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        imageUris=new ArrayList<>();
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.pager_item,container,false);
        ImageView imItem = view.findViewById(R.id.imageViewPager);
        String uri =imageUris.get(position);
        if(uri.substring(0,4).equals("http"))
        {
            Picasso.get().load(uri).into(imItem);
        }
        else
        {
            imItem.setImageURI(Uri.parse(imageUris.get(position)));
        }

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout)object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {


        container.removeView((LinearLayout)object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void updateImages(List<String> images)
    {
        imageUris.clear();
        imageUris.addAll(images);
        notifyDataSetChanged();
    }
}
