package net.jitsi.sdktest.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import net.jitsi.sdktest.fragment.AllUsersFragment;
import net.jitsi.sdktest.fragment.ContactsFragment;

//TablAYOUT với Contacts : Các User từ danh bạ
//All User chứa tất cả các User
public class ViewPagerUserAdapter extends FragmentStateAdapter  {

    public ViewPagerUserAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ContactsFragment();
            case 1:
                return new AllUsersFragment();

            default:
                return new ContactsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
