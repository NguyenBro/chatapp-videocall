package net.jitsi.sdktest.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.squareup.picasso.Picasso;

import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.SendAgainActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchAdapter extends BaseAdapter  {
    private SendAgainActivity context;
    private  int layout;
    private List<User> listUser;

    public SearchAdapter(SendAgainActivity context, int layout, List<User> listImage) {
        this.context = context;
        this.layout = layout;
        this.listUser = listImage;
    }


    @Override
    public int getCount() {
        return listUser.size();
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

        TextView txtName,txtSend;
        CircleImageView imgAvatar;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view ==null){
            holder=new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            //Anh xa

            holder.txtName = view.findViewById(R.id.textViewUserNameItem_Again);
            holder.txtSend = view.findViewById(R.id.textView8);
            holder.imgAvatar = view.findViewById(R.id.imageUserItem_Again);

            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        User user =listUser.get(i);
        holder.txtName.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.imgAvatar.setImageResource(R.drawable.image_avatar);
        }
        else{
            Picasso.get().load(user.getImageURL()).into(holder.imgAvatar);
        }

        holder.txtSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                context.sendMessage(user.getId());
                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
