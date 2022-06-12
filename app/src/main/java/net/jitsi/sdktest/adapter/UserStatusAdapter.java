package net.jitsi.sdktest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.MessageActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//Hiện thị các Usser đang hoạt động
public class UserStatusAdapter extends RecyclerView.Adapter<UserStatusAdapter.ViewHolder> {

    private Context context;
    private List<User> listUser;

    public UserStatusAdapter(Context context, List<User> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_status_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = listUser.get(position);
        holder.userName.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.drawable.image_avatar);
        }
        else{
            Picasso.get().load(user.getImageURL()).into(holder.profile_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName;
        public CircleImageView profile_image;
        private CircleImageView img_status;
        public ViewHolder(View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.textViewName_Status);
            profile_image = itemView.findViewById(R.id.imageUserItem_Status);
            img_status = itemView.findViewById(R.id.imageViewStatus);

        }
    }
}
