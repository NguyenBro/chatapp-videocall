package net.jitsi.sdktest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.jitsi.sdktest.Model.Meeting;
import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.fragment.MeetingFragment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeetingAdapter extends BaseAdapter {
    private Context context;
    private  int layout;
    private List<Meeting> listMeeting;

    public MeetingAdapter(Context context, int layout, List<Meeting> listMeeting) {
        this.context = context;
        this.layout = layout;
        this.listMeeting = listMeeting;
    }

    @Override
    public int getCount() {
        return listMeeting.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder{

        TextView txtDate;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view ==null){
            holder=new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            //Anh xa

            holder.txtDate = view.findViewById(R.id.textView23);


            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();

        }

        Meeting meeting = listMeeting.get(position);
        holder.txtDate.setText(meeting.getTime());
        return view;
    }
}
