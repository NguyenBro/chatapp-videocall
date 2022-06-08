package net.jitsi.sdktest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.jitsi.sdktest.Model.ImageFile;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.InfoImageActivity;

import java.util.List;



public class ImageFileAdapter extends BaseAdapter {
    private Context context;
    private  int layout;
    private List<ImageFile> listImage;

    public ImageFileAdapter(Context context, int layout, List<ImageFile> listImage) {
        this.context = context;
        this.layout = layout;
        this.listImage = listImage;
    }

    @Override
    public int getCount() {
        return listImage.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{

        ImageView imgImage;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view ==null){
            holder=new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            //Anh xa

            holder.imgImage = view.findViewById(R.id.imageViewItemImage);

            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        ImageFile image =listImage.get(i);
        Picasso.get().load(image.getImageUrl()).into(holder.imgImage);

        holder.imgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, InfoImageActivity.class);
                intent.putExtra("url",image.getImageUrl());
                context.startActivity(intent);
            }
        });



        return view;
    }
}
