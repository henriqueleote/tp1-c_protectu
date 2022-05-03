package cm.protectu.Community;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> fragmentArrayList;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,ArrayList<Fragment> fragmentArrayList) {
        super(fragmentManager, lifecycle);
        this.fragmentArrayList = new ArrayList<>(fragmentArrayList);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentArrayList.size();
    }
}
