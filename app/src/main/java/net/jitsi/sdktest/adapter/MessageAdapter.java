package net.jitsi.sdktest.adapter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import net.jitsi.sdktest.Model.Chat;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.InfoImageActivity;
import net.jitsi.sdktest.UI.InfoVideoActivity;
import net.jitsi.sdktest.UI.MessageActivity;

import java.util.List;



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private MessageActivity context;
    private List<Chat> listChat;
    private String imageurl;
    FirebaseUser firebaseUser;
    public MessageAdapter(MessageActivity context, List<Chat> mChat, String img){
        this.context = context;
        this.listChat = mChat;
        this.imageurl = img;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Kiểm tra tin nhắn của sender hay receiver
        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CountDownTimer countDownTimer;
        Chat chat = listChat.get(position);
        holder.show_message.setText(chat.getMessage());     //Show tin nhắn
        //showw avatar
        if(imageurl.equals("default")){
            holder.profile_image.setImageResource(R.drawable.image_avatar);

        }
        else{
            Picasso.get().load(imageurl).into(holder.profile_image);
        }

        //Kiểm tra loại tin nhắn, để hiện thị
        switch (chat.getType()){
            case "default":
                holder.imgChat.setVisibility(View.GONE);
                holder.video.setVisibility(View.GONE);
                if(chat.getMessage().equals(":D")){

                    showIcon(holder.show_message,holder.imgEmotion,holder.txt_seen,R.drawable.image_smiling);
                }
                else if(chat.getMessage().equals(":v")){
                    showIcon(holder.show_message,holder.imgEmotion,holder.txt_seen, R.drawable.image_smile);
                }
                else if(chat.getMessage().equals(":(") || chat.getMessage().equals(":((") ){
                    showIcon(holder.show_message,holder.imgEmotion,holder.txt_seen, R.drawable.image_sad);
                }
                else if(chat.getMessage().equals(":)") || chat.getMessage().equals(":))") ){
                    showIcon(holder.show_message,holder.imgEmotion,holder.txt_seen, R.drawable.image_smile_heart);
                }
                else if(chat.getMessage().equals("<3") ){
                    showIcon(holder.show_message,holder.imgEmotion,holder.txt_seen, R.drawable.image_heart);
                }
                else if(chat.getMessage().equals(":<") ){
                    showIcon(holder.show_message,holder.imgEmotion,holder.txt_seen, R.drawable.image_angry);
                }
                break;
            case "image":
                holder.imgChat.setVisibility(View.VISIBLE);
                holder.imgEmotion.setVisibility(View.GONE);
                Picasso.get().load(chat.getLink()).into(holder.imgChat);
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.imageViewBody);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                holder.txt_seen.setLayoutParams(params);
                //Chỉnh lại các rules cho hình ảnh
                if(chat.getMessage().equals("")){
                    holder.show_message.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params1= new RelativeLayout.LayoutParams(580,800);
                    if(firebaseUser.getUid().equals(chat.getSender())) {
                        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params1.addRule(RelativeLayout.ALIGN_PARENT_END);
                    }else{
                        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params1.addRule(RelativeLayout.RIGHT_OF,R.id.profile_image_chat);
                        params1.setMargins(10,0,0,0);

                    }
                    holder.imgChat.setLayoutParams(params1);
                }
                break;

            case "video":
                RelativeLayout.LayoutParams paramss= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                paramss.addRule(RelativeLayout.BELOW, R.id.videoViewBody);
                paramss.addRule(RelativeLayout.ALIGN_PARENT_END);
                holder.txt_seen.setLayoutParams(paramss);

                //Chỉnh lại rules
                if(chat.getMessage().equals("")){
                    RelativeLayout.LayoutParams params1= new RelativeLayout.LayoutParams(700,700);
                    holder.show_message.setVisibility(View.GONE);
                    if(firebaseUser.getUid().equals(chat.getSender())) {
                        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params1.addRule(RelativeLayout.ALIGN_PARENT_END);
                    }else{
                        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params1.addRule(RelativeLayout.RIGHT_OF,R.id.profile_image_chat);
                        params1.setMargins(10,0,0,0);
                    }
                    holder.video.setLayoutParams(params1);
                }else{
                    RelativeLayout.LayoutParams params2= new RelativeLayout.LayoutParams(700,700);
                    if(firebaseUser.getUid().equals(chat.getSender())) {
                        params2.addRule(RelativeLayout.BELOW,R.id.show_message);
                        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                    }else{
                        params2.addRule(RelativeLayout.BELOW,R.id.show_message);
                        params2.addRule(RelativeLayout.RIGHT_OF,R.id.profile_image_chat);
                        //params2.addRule(RelativeLayout.ALIGN_PARENT_START);
                        //params2.setMargins(10,0,0,0);
                    }
                    params2.setMargins(0,6,0,0);
                    holder.video.setLayoutParams(params2);

                }

                //Chạy video
                holder.video.setVideoURI(Uri.parse(chat.getLink()));
                holder.video.requestFocus();
                holder.video.setVisibility(View.VISIBLE);
                //holder.video.start();

                break;
            case "file":
                holder.show_message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_archive_24, 0, 0, 0);
                break;
            case "audio":
                holder.imgChat.setVisibility(View.GONE);
                holder.show_message.setVisibility(View.GONE);
                holder.seekBar.setVisibility(View.VISIBLE);
                holder.imgPlay.setVisibility(View.VISIBLE);
                //holder.imgPause.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams paramsss= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsss.addRule(RelativeLayout.BELOW, R.id.seekBarPlay);
                paramsss.addRule(RelativeLayout.ALIGN_PARENT_END);
                holder.txt_seen.setLayoutParams(paramsss);

                //Lấy uri của audio
                Uri uri = Uri.parse(chat.getLink());
                holder.mp = MediaPlayer.create(context, uri);
                int duration = holder.mp.getDuration();
                holder.seekBar.setMax(duration);
                break;



        }

        //Hiện thị status của tin nhắn : xem hay chưa xem
        if(position==listChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setText(context.getResources().getString(R.string.seen));
            }
            else{
                holder.txt_seen.setText(context.getResources().getString(R.string.delivered));
            }
        }
        else{
            holder.txt_seen.setVisibility(View.GONE);
        }


        //event click vào video
        holder.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mp.start();
//                holder.imgPlay.setVisibility(View.GONE);
//                holder.imgPause.setVisibility(View.VISIBLE);
                CountDownTimer countDownTimer = new CountDownTimer(holder.mp.getDuration(),500) {
                    @Override
                    public void onTick(long l) {
                        int i = holder.seekBar.getProgress();
                        holder.seekBar.setProgress(i+500);
                    }

                    @Override
                    public void onFinish() {
                        //Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_SHORT).show();
                        holder.seekBar.setProgress(0);
//                        holder.imgPlay.setVisibility(View.VISIBLE);
//                        holder.imgPause.setVisibility(View.GONE);
                    }
                }.start();
            }
        });

        //event click vào hình ảnh
        holder.imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, InfoImageActivity.class);
                intent.putExtra("url",chat.getLink());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Toast.makeText(context, "hh", Toast.LENGTH_SHORT).show();
                context.showBottomSheet(chat.getMessage(),chat.getId(),chat);
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("INDEX", chat.getType());
            }
        });

        holder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.video.start();
                Intent intent =new Intent(context, InfoVideoActivity.class);
                intent.putExtra("video",chat.getLink());
                intent.putExtra("userid",MessageActivity.userid);
                context.startActivity(intent);
            }
        });

        //Click vào tin nhắn : Nếu tin nhắn chứa file: sẽ hiển thị dialog tải về
        holder.show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chat.getType().equals("file")){
                    //Toast.makeText(context, "DownLoad", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Download File");
                    builder.setMessage("Do you want to download file ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.downloadFile(Uri.parse(chat.getLink()),chat.getMessage());
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;
        public  TextView txt_seen;
        public ImageView imgChat,imgPlay,imgPause,imgEmotion;
        public SeekBar seekBar;
        public VideoView video;
        public MediaPlayer mp;
        public MediaController mediaController =new MediaController(context);
        public ViewGroup wrapperView = new RelativeLayout(context);
        public ViewHolder(View itemView){
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image_chat);
            txt_seen = itemView.findViewById(R.id.txt_send);
            imgChat =itemView.findViewById(R.id.imageViewBody);
            video = itemView.findViewById(R.id.videoViewBody);
            imgEmotion = itemView.findViewById(R.id.imageViewEmotion);
            imgPlay = itemView.findViewById(R.id.imageViewPlay);
            imgPause = itemView.findViewById(R.id.imageViewPause);
            seekBar = itemView.findViewById(R.id.seekBarPlay);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(listChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    //Xem 1 số icon từ phím tắt tin nhắn
    private void showIcon(TextView show_message,ImageView emotion,TextView seen,Integer Intimage){
        show_message.setVisibility(View.GONE);
        emotion.setVisibility(View.VISIBLE);
        emotion.setImageResource(Intimage);
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.imageViewEmotion);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        seen.setLayoutParams(params);
    }
}
