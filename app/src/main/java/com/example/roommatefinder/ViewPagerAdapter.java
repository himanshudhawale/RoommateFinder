package com.example.roommatefinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<String> imageUrls;
    private LayoutInflater layoutInflater;

    ViewPagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
//        Log.d("demoooo", imageUrls.toString());
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, container, false);
        ImageView imageView = view.findViewById(R.id.imageViewToShow);

        Picasso.get()
                .load(imageUrls.get(position))
                .into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
//        container.addView(imageView);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;

        View view = (View) object;
        vp.removeView(view);

    }
}