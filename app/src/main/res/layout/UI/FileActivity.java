package hcmute.nhom1.chatapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import hcmute.nhom1.chatapp.R;
import hcmute.nhom1.chatapp.adapter.ViewPagerAdapter;

public class FileActivity extends AppCompatActivity {
    TabLayout mTabLayout;
    ViewPager2 mViewPage;
    ArrayList<String> tabLayoutList;
    ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        tabLayoutList =new ArrayList<>();
        tabLayoutList.add("Image");
        tabLayoutList.add("Video");
        tabLayoutList.add("File");
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPage = findViewById(R.id.viewPage);
        imgBack = findViewById(R.id.imageView7);
        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle());
        mViewPage.setAdapter(viewPagerAdapter);


        new TabLayoutMediator(mTabLayout, mViewPage,
                (tab, position) -> tab.setText(tabLayoutList.get(position))
        ).attach();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(FileActivity.this,MessageActivity.class);
                intent1.putExtra("userid",userid);
                startActivity(intent1);
            }
        });
    }
}