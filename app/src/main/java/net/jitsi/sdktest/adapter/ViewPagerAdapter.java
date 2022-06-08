package net.jitsi.sdktest.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import net.jitsi.sdktest.fragment.FileFragment;
import net.jitsi.sdktest.fragment.ImageFileFragment;
import net.jitsi.sdktest.fragment.VideoFileFragment;


public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ImageFileFragment();
            case 1:
                return new VideoFileFragment();
            case 2:
                return new FileFragment();

            default:
                return new ImageFileFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }




}
