package cm.protectu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import cm.protectu.Community.CommunityFragment;
import cm.protectu.MissingBoard.MissingBoardFragment;

public class ViewPagerFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        ArrayList<Fragment> list = new ArrayList<>();
        list.add(new CommunityFragment(null));
        list.add(new MissingBoardFragment());

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getParentFragmentManager(),getLifecycle(),list);

        ViewPager2 viewPager = view.findViewById(R.id.view_pagerCommunity);
        viewPager.setAdapter(viewPagerAdapter);

        return view;
    }
}
