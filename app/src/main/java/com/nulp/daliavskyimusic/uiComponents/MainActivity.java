package com.nulp.daliavskyimusic.uiComponents;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nulp.daliavskyimusic.InfoMusicActivity;
import com.nulp.daliavskyimusic.logicComponents.MediaPlayerList;
import com.nulp.daliavskyimusic.logicComponents.PageLoader;
import com.nulp.daliavskyimusic.R;

import steelkiwi.com.library.DotsLoaderView;

public class MainActivity extends AppCompatActivity {

    MediaPlayerList mpl;
    DotsLoaderView dotsLoaderView;
    Fragment fragment;

    PageLoader pl = null;

    boolean isPause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        visibleButtonFragment(false);

        refreshMediaPlayerList();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationItemClickListener());

        dotsLoaderView = (DotsLoaderView)findViewById(R.id.dotsLoaderView);

        ObservableListView lw = findViewById(R.id.list);
        lw.setOnItemClickListener(new ListItemClickListener());
        lw.setOnScrollListener(new ListViewScrollingAction());

        ImageButton btnPrev = (ImageButton) findViewById(R.id.button_back);
        ImageButton btnNext = (ImageButton) findViewById(R.id.button_next);
        ImageButton btnPlay = (ImageButton) findViewById(R.id.button_play);
        ImageButton btnShare = (ImageButton) findViewById(R.id.button_share);
        ImageButton btnInfo = (ImageButton) findViewById(R.id.button_info);
        ButtonListeners bl = new ButtonListeners(mpl);
        btnPrev.setOnClickListener(bl);
        btnNext.setOnClickListener(bl);
        btnPlay.setOnClickListener(bl);
        btnShare.setOnClickListener(bl);
        btnInfo.setOnClickListener(bl);

        pl = new PageLoader("https://m.z1.fm/",new String[] {"sort","views"});
        new RetrieveFeedTask().execute(pl);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Натисніть ’Назад’ ще раз для виходу", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search_item = menu.findItem(R.id.mi_search);

        final SearchView searchView = (SearchView) search_item.getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                refreshMediaPlayerList();
                pl = new PageLoader("https://m.z1.fm/mp3/search/",new String[]{"keywords",s});
                refreshView();
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    private void visibleButtonFragment(boolean isVisible) {
        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment1);
        FragmentTransaction ft = fm.beginTransaction();
        if(!isVisible)ft.hide(fragment); else {ft.show(fragment);}
        ft.commit();
    }

    private void visibleListFragment(boolean isVisible) {
        ObservableListView lv = findViewById(R.id.list);
        if(isVisible)lv.setVisibility(View.VISIBLE);else lv.setVisibility(View.INVISIBLE);
    }

    private void refreshMediaPlayerList() {
        mpl = new MediaPlayerList();
    }

    private void refreshView(){
        visibleListFragment(false);
        new RetrieveFeedTask().execute(pl);
    }

    private void changeSelections(){
        try{
            MusicItemAdapter adapter = mpl.getAdapter();
            adapter.setSelectedHref(mpl.getCurrentPlayItem().getSong_href());
            adapter.notifyDataSetChanged();
        } catch (ArrayIndexOutOfBoundsException e){
            mpl.setCurrentPlay(0);
            changeSelections();
        }
    }

    private class NavigationItemClickListener implements NavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            int id = item.getItemId();
            if(id==R.id.popular_day){
                refreshMediaPlayerList();
                pl = new PageLoader("https://m.z1.fm/",new String[]{"sort","day"});
                refreshView();
            }
            if(id==R.id.popular_week){
                refreshMediaPlayerList();
                pl = new PageLoader("https://m.z1.fm/",new String[]{"sort","week"});
                refreshView();
            }
            if(id==R.id.popular_month){
                refreshMediaPlayerList();
                pl = new PageLoader("https://m.z1.fm/",new String[]{"sort","month"});
                refreshView();
            }
            if(id==R.id.week_rating){
                refreshMediaPlayerList();
                pl = new PageLoader("https://m.z1.fm/new",new String[]{"sort","date_views"});
                refreshView();
            }
            if(id==R.id.date_rating){
                refreshMediaPlayerList();
                pl = new PageLoader("https://m.z1.fm/new",new String[]{"sort","date"});
                refreshView();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    private class ListItemClickListener implements ObservableListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mpl.setCurrentPlay(position);
            changeSelections();
        }
    }

    private class RetrieveFeedTask extends AsyncTask<Object, Integer, Integer> {

        @Override
        protected Integer doInBackground(Object... args) {
            PageLoader pl = ((PageLoader) args[0]);
            int _size = pl.loadTo(mpl.getList());
            return _size;
        }

        @Override
        protected void onPreExecute() {
            dotsLoaderView.show();
        }

        @Override
        protected void onPostExecute(final Integer aLong) {
            if(aLong == -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Завантаження контенту")
                        .setMessage("Запит не дав результатів.")
                        .setCancelable(false)
                        .setPositiveButton("Повторити",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        new RetrieveFeedTask().execute(pl);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Ок",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        pl = new PageLoader("https://m.z1.fm/",new String[]{"sort","day"});
                                        refreshMediaPlayerList();
                                        new RetrieveFeedTask().execute(pl);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                if(mpl.adapterIsNull()) {
                    mpl.createAdapter(getApplicationContext());
                    ((ObservableListView)findViewById(R.id.list)).setAdapter(mpl.getAdapter());
                    visibleListFragment(true);
                    visibleButtonFragment(true);
                } else {
                    mpl.getAdapter().notifyDataSetChanged();
                }
            }
            dotsLoaderView.hide();
        }
    }

    private class ListViewScrollingAction implements AbsListView.OnScrollListener{
        private int currentVisibleItemCount;
        private int currentScrollState;
        private int currentFirstVisibleItem;
        private int totalItem;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            this.currentScrollState = scrollState;
            this.isScrollCompleted();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            this.currentFirstVisibleItem = firstVisibleItem;
            this.currentVisibleItemCount = visibleItemCount;
            this.totalItem = totalItemCount;
        }

        private void isScrollCompleted() {
            if(!pl.isEnd()) {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE
                        && totalItem!=0) {
                    new RetrieveFeedTask().execute(pl);
                    dotsLoaderView.show();
                } else {
                    dotsLoaderView.hide();
                }
            }
        }
    }
    private class ButtonListeners implements View.OnClickListener {
        private MediaPlayerList mpl;

        public ButtonListeners(MediaPlayerList mpl) {
            this.mpl = mpl;
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if(id==R.id.button_next){
                mpl.setCurrentPlay(mpl.getCurrentPlayIndex()+1);
                changeSelections();

            }
            if(id==R.id.button_back){
                mpl.setCurrentPlay(mpl.getCurrentPlayIndex()-1);
                changeSelections();
            }
            if(id==R.id.button_play){
                if(isPause) ((ImageButton) view).setImageResource(R.drawable.pause);
                else ((ImageButton) view).setImageResource(R.drawable.play);
                isPause=!isPause;
            }
            if(id==R.id.button_info){
                Intent i = new Intent(MainActivity.this, InfoMusicActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.first_anim,R.anim.second_anim_out);
            }
            if(id==R.id.button_share){
                throw new RuntimeException("App is crashed");
                //share(mpl.getCurrentPlayItem().toString());
            }
        }

        private void share(String s){
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            if (waIntent != null) {
                waIntent.putExtra(Intent.EXTRA_TEXT, s);//
                startActivity(Intent.createChooser(waIntent, "Поширити посилання з"));
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Застосувань для поширення не знайдено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
