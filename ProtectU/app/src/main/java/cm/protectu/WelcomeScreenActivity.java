package cm.protectu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class WelcomeScreenActivity extends AppCompatActivity {

    //TODO Add the skip button in the bottom right corner

    //ViewPager
    private ViewPager viewPager;

    //MyViewPager Adapter
    private MyViewPagerAdapter myViewPagerAdapter;

    //LinearLayout
    private LinearLayout dotsLayout;

    //TextView
    private TextView[] dots;

    //PrefManager
    private PrefManager prefManager;

    //Button
    private Button beginBtn;

    //ImageView
    private ImageView nextImageView;

    //TextView
    private TextView nextTextView;

    //Variables
    private int[] layouts;

    //View Page Listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        //On the selected page
        public void onPageSelected(int position) {

            //Add position dots to the layout
            addBottomDots(position);

            //Showing the dots depending the position
            //If its the last page
            if (position == layouts.length - 1) {
                beginBtn.setVisibility(View.VISIBLE);
                nextImageView.setVisibility(View.GONE);
                nextTextView.setVisibility(View.GONE);
            }
            //Any other page
            else {
                beginBtn.setVisibility(View.GONE);
                nextImageView.setVisibility(View.VISIBLE);
                nextTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        //No need to implement
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        //No need to implement
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Link the layout to the activity
        setContentView(R.layout.activity_welcome);

        //If it's not the first launch, launch automatically the Auth Page
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchLoginScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        //Link the view objects with the XML
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        beginBtn = findViewById(R.id.btn_begin);
        nextImageView = findViewById(R.id.btn_next);
        nextTextView = findViewById(R.id.text_next);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        //Add all the pages to the layout
        layouts = new int[]{
                R.layout.welcome1,
                R.layout.welcome1,
                R.layout.welcome1};

        //Add the dots to the page
        addBottomDots(0);

        //Making status bar to transparent
        changeStatusBarColor();

        //On click end the tutorial and start the Auth page
        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if it's the last page
                int current = getItem(+1);
                if (current < layouts.length) {
                    //Go to the next page
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        //On click go to the next page
        nextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if it's the last page
                int current = getItem(+1);
                if (current < layouts.length) {
                    //Go to the next page
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        //On click go to the next page
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if it's the last page
                int current = getItem(+1);
                if (current < layouts.length) {
                    //Go to the next page
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    //Method to add the dots to the bottom of the screen
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    //Returns the position of the screen
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //Launches the home screen
    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeScreenActivity.this, AuthActivity.class));
        finish();
    }

    //Launches the login screen
    public void launchLoginScreen(){
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeScreenActivity.this, AuthActivity.class));
        finish();
    }

    //Transperant status bar
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    //View page adapter
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        //Empty constructor
        public MyViewPagerAdapter() {
        }

        //Returns the object
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        //Returns the number of pages
        @Override
        public int getCount() {
            return layouts.length;
        }

        //Returns if it's a view
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        //Removes the screen from the tutorial
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
