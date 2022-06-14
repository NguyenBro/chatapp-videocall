package net.jitsi.sdktest.adapter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.jitsi.sdktest.Model.Chat;
import net.jitsi.sdktest.Model.SizeImage;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.InfoImageActivity;
import net.jitsi.sdktest.UI.InfoVideoActivity;
import net.jitsi.sdktest.UI.MessageActivity;

import java.io.IOException;
import java.util.List;



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT=0;        //Tin nhắn nằm bên trái
    public static final int MSG_TYPE_RIGHT=1;       //Tin nhắn nằm bên phải
    private MessageActivity context;
    private List<Chat> listChat;
    private String imageurl;
    FirebaseUser firebaseUser;                      //Lấy User khi đăng nhập thành công từ FireBase
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
            //Nếu tin nhắn từ chính mình ==> Nằm bên phải
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
        else{
            //Nhược lại tin nhắn nhận đc, thì nằm bên trái
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CountDownTimer countDownTimer;
        Chat chat = listChat.get(position);         //Lấy vị trí tin nhắn từ Array
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

                //Kiểm tra và hiện thi Các Icon
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
                //Lấy kích thước màn hình, để fix kích thước cho image
                DisplayMetrics displayMetrics = new DisplayMetrics();
                context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int heightScreen = displayMetrics.heightPixels;
                int widthScreen = displayMetrics.widthPixels;
                //Load image và lấy kích thước hiện tại của ImageView , xử dựng Bất Đồng Bộ để trả về kết quả
                Target target;
                Picasso.get().load(chat.getLink())
                    .into(target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            //Lấy kích thước hình ảnh
                            int height = bitmap.getHeight();
                            int width = bitmap.getWidth();
                            Log.d("LOAD: ", height+" "+width);

                            //Xử lý tỉ lệ hình ảnh khi hiện ra màn hình
                            float percent = height/(float)width;
                            int widthScale = (int) Math.round(widthScreen * 0.7);       //Chiều rộng tấm hình sau khi được điều chỉnh
                            int heightScale =(int) Math.round(widthScreen * 0.7 * percent * 0.95);      //Chiều cao sau khi được điều chỉnh

                            //Sửa lại Rules
                            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.addRule(RelativeLayout.BELOW, R.id.imageViewBody);
                            params.addRule(RelativeLayout.ALIGN_PARENT_END);
                            holder.txt_seen.setLayoutParams(params);
                            //Chỉnh lại các rules cho hình ảnh, với kích thước đã được tính từ trên
                            RelativeLayout.LayoutParams params1= new RelativeLayout.LayoutParams(widthScale,heightScale);
                            //holder.imgChat.setImageBitmap(bitmap);

                            if(chat.getMessage().equals("")){
                                holder.show_message.setVisibility(View.GONE);
                                //SizeImage sizeImage = getSizeImage(holder.imgChat);
                                //Log.d("SIZEIAMGE",sizeImage.getHeight() + " " + sizeImage.getWidth());
                                if(firebaseUser.getUid().equals(chat.getSender())) {
                                    params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);       //Top
                                    params1.addRule(RelativeLayout.ALIGN_PARENT_END);       //Right
                                }else{
                                    params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);       //Top
                                    params1.addRule(RelativeLayout.RIGHT_OF,R.id.profile_image_chat);   //Nằm bên phải Avatar
                                    params1.setMargins(10,0,0,0);

                                }

                            }else{
                                if(firebaseUser.getUid().equals(chat.getSender())) {
                                    params1.addRule(RelativeLayout.BELOW,R.id.show_message);
                                    params1.addRule(RelativeLayout.ALIGN_PARENT_END);
                                    params1.setMargins(0,10,0,0);
                                }else{
                                    params1.addRule(RelativeLayout.BELOW,R.id.show_message);
                                    params1.addRule(RelativeLayout.RIGHT_OF,R.id.profile_image_chat);
                                    params1.setMargins(10,10,0,0);

                                }
                            }
                            holder.imgChat.setLayoutParams(params1);
                            //Chỉnh cho imageView được bo 4 góc
                            holder.imgChat.setBackground(context.getResources().getDrawable(R.drawable.custom_image));
                            holder.imgChat.setClipToOutline(true);

                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Log.d("FAILE", "Load");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.d("Pre", "Load");
                        }
                    });
                Picasso.get().load(chat.getLink()).into(holder.imgChat);
                holder.imgChat.setTag(target);

                break;

            case "video":
                RelativeLayout.LayoutParams paramss= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                paramss.addRule(RelativeLayout.BELOW, R.id.videoViewBody);
                paramss.addRule(RelativeLayout.ALIGN_PARENT_END);
                holder.txt_seen.setLayoutParams(paramss);

                //Chỉnh lại rules
                if(chat.getMessage().equals("")){
                    //Với Video không kèm theo tin nhắn text
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
                    //Gửi video kèm với text
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

                //Chạy video, từ Urri
                holder.video.setVideoURI(Uri.parse(chat.getLink()));
                holder.video.requestFocus();
                holder.video.setVisibility(View.VISIBLE);
                //holder.video.start();

                break;
            case "file":
                //Chỉnh lại giao diện khi tin nhắn có gửi File tài liệu
                holder.show_message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_archive_24, 0, 0, 0);
                break;
            case "audio":
                holder.imgChat.setVisibility(View.GONE);
                holder.show_message.setVisibility(View.GONE);
                holder.seekBar.setVisibility(View.VISIBLE);
                holder.imgPlay.setVisibility(View.VISIBLE);
                //holder.imgPause.setVisibility(View.VISIBLE);
                //Neo Seen vao SeekBar
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
            if(chat.getSender().equals(firebaseUser.getUid())){
                holder.txt_seen.setVisibility(View.VISIBLE);
            }
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


        //event click vào audio
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
                    builder.setTitle(context.getResources().getString(R.string.download_file));
                    builder.setMessage(context.getResources().getString(R.string.do_you_want));
                    builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.downloadFile(Uri.parse(chat.getLink()),chat.getMessage());
                        }
                    });

                    builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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
        public TextView show_message;       //Tin nhắn text
        public ImageView profile_image;     //Image Âvatar
        public  TextView txt_seen;          //Tin nhắn thông báo 'xem' hay 'chưa xem'
        public ImageView imgChat,imgPlay,imgPause,imgEmotion;
        public SeekBar seekBar;             //Seek Bar để chạy Audio
        public VideoView video;             //Video
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

    //lOC TIN NHAN, AI GỬI , AI NHẬN
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
