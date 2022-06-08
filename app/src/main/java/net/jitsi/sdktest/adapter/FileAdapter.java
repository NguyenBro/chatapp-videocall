package net.jitsi.sdktest.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import net.jitsi.sdktest.Model.File;
import net.jitsi.sdktest.R;

import java.util.List;



public class FileAdapter extends BaseAdapter {
    private Context context;
    private  int layout;
    private List<File> listNameFile;

    public FileAdapter(Context context, int layout, List<File> listImage) {
        this.context = context;
        this.layout = layout;
        this.listNameFile = listImage;
    }

    @Override
    public int getCount() {
        return listNameFile.size();
    }

    @Override
    public Object getItem(int i) {
        return listNameFile.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{

        TextView txtFileName;
        View view;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view ==null){
            holder=new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            //Anh xa

            holder.txtFileName = view.findViewById(R.id.textViewFileName_File);
            holder.view = view.findViewById(R.id.view2);

            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        File file = listNameFile.get(i);
        holder.txtFileName.setText(file.getName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Download File");
                builder.setMessage("Do you want to download file "+ file.getName()+ " ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        downloadFile(Uri.parse(file.getUrlFile()),file.getName());

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });



        return view;
    }
    public void downloadFile(Uri uri,String filename) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle(filename)
                .setDescription("This is file from ChatApp")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        downloadManager.enqueue(request);
    }

}
