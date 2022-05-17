package cm.protectu.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import cm.protectu.MissingBoard.MissingBoardFragment;
import cm.protectu.R;

public class ViewPagerFragment extends Fragment {
    ViewPagerAdapter viewPagerAdapter;
    ViewPager2 viewPager;
    ArrayList<Fragment> list;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        list = new ArrayList<>();
        list.add(new CommunityFragment(null));
        list.add(new MissingBoardFragment());

        viewPagerAdapter = new ViewPagerAdapter(getParentFragmentManager(),getLifecycle(),list);

        viewPager = view.findViewById(R.id.view_pagerCommunity);
        viewPager.setAdapter(viewPagerAdapter);

        return view;
    }

    public void changeToMissing(){
        list.set(1,new MissingBoardFragment());
        viewPagerAdapter = new ViewPagerAdapter(getParentFragmentManager(),getLifecycle(),list);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
    }

    public void changeToCommunity(){
        list.set(0,new CommunityFragment(null));
        viewPagerAdapter = new ViewPagerAdapter(getParentFragmentManager(),getLifecycle(),list);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
    }
}
