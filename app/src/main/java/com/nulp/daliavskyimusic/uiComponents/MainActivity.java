package com.nulp.daliavskyimusic.uiComponents;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nulp.daliavskyimusic.InfoMusicActivity;
import com.nulp.daliavskyimusic.MusicPlayer;
import com.nulp.daliavskyimusic.logicComponents.MediaPlayerList;
import com.nulp.daliavskyimusic.logicComponents.PageLoader;
import com.nulp.daliavskyimusic.R;

import steelkiwi.com.library.DotsLoaderView;

public class MainActivity extends AppCompatActivity {
    
    DotsLoaderView dotsLoaderView;
    Fragment fragment;

    PageLoader pl = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicPlayer.mainActivity = MainActivity.this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        visibleButtonFragment(false);

        refreshMediaPlayerList();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationItemClickListener());
        dotsLoaderView = findViewById(R.id.dotsLoaderView);

        LinearLayout fl = findViewById(R.id.button_fragment_button);

        ObservableListView lw = findViewById(R.id.list);
        lw.setOnItemClickListener(new ListItemClickListener());
        lw.setOnScrollListener(new ListViewScrollingAction());

        ImageButton btnPrev =  findViewById(R.id.button_back);
        ImageButton btnNext =  findViewById(R.id.button_next);
        ImageButton btnPlay =  findViewById(R.id.button_play);
        ButtonListeners bl = new ButtonListeners();
        fl.setOnClickListener(bl);
        btnPrev.setOnClickListener(bl);
        btnNext.setOnClickListener(bl);
        btnPlay.setOnClickListener(bl);

        pl = new PageLoader("https://m.z1.fm/",new String[] {"sort","views"});
        new RetrieveFeedTask().execute(pl);
    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Натисніть ’Назад’ ще раз для виходу", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        changeSelections();
        setPlayerInfo();
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
        MusicPlayer.mpl = new MediaPlayerList();
    }

    public void refreshView(){
        visibleListFragment(false);
        new RetrieveFeedTask().execute(pl);
    }

    public void changeSelections(){
        try{
            MusicItemAdapter adapter = MusicPlayer.mpl.getAdapter();
            adapter.setSelectedHref(MusicPlayer.mpl.getCurrentPlayItem().getSong_href());
            adapter.notifyDataSetChanged();
        } catch (ArrayIndexOutOfBoundsException e){
            MusicPlayer.mpl.setCurrentPlay(0);
            changeSelections();
        }
    }

    public void setPlayerInfo(){
        RoundedImageView image = findViewById(R.id.select_item_image);
        TextView auth = findViewById(R.id.item_select_author);
        TextView song = findViewById(R.id.item_select_song);
        Glide
                .with(getApplicationContext())
                .load(MusicPlayer.mpl.getCurrentPlayItem().getImage_href())
                .into(image);
        int len = Math.min(MusicPlayer.mpl.getCurrentPlayItem().getAuthor_name().length(),25);
        song.setText(MusicPlayer.mpl.getCurrentPlayItem().getAuthor_name().substring(0,len)+"...");
        len = Math.min(MusicPlayer.mpl.getCurrentPlayItem().getSong_name().length(),25);
        auth.setText(MusicPlayer.mpl.getCurrentPlayItem().getSong_name().substring(0,len)+"...");
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
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    private class ListItemClickListener implements ObservableListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MusicPlayer.mpl.setCurrentPlay(position);
            changeSelections();
            setPlayerInfo();
            MusicPlayer.updateMediaPlayer();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RetrieveFeedTask extends AsyncTask<Object, Integer, Integer> {

        @Override
        protected Integer doInBackground(Object... args) {
            PageLoader pl = ((PageLoader) args[0]);
            return pl.loadTo(MusicPlayer.mpl.getList());
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
                                (dialog, i) -> {
                                    new RetrieveFeedTask().execute(pl);
                                    dialog.cancel();
                                })
                        .setNegativeButton("Ок",
                                (dialog, id) -> {
                                    pl = new PageLoader("https://m.z1.fm/",new String[]{"sort","day"});
                                    refreshMediaPlayerList();
                                    new RetrieveFeedTask().execute(pl);
                                    dialog.cancel();
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                if(MusicPlayer.mpl.adapterIsNull()) {
                    MusicPlayer.mpl.createAdapter(getApplicationContext());
                    ((ObservableListView)findViewById(R.id.list)).setAdapter(MusicPlayer.mpl.getAdapter());
                    visibleListFragment(true);
                    visibleButtonFragment(true);
                } else {
                    MusicPlayer.mpl.getAdapter().notifyDataSetChanged();
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

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if(id==R.id.button_next){
                MusicPlayer.mpl.nextPlay();
                changeSelections();
                setPlayerInfo();
                MusicPlayer.updateMediaPlayer();
            }
            if(id==R.id.button_back){
                MusicPlayer.mpl.prevPlay();
                changeSelections();
                setPlayerInfo();
                MusicPlayer.updateMediaPlayer();
            }
            if(id==R.id.button_play){
                if(MusicPlayer.isPause){
                    ((ImageButton) view).setImageResource(R.drawable.pause);
                    MusicPlayer.playMediaPlayer();
                }
                else {
                    ((ImageButton) view).setImageResource(R.drawable.play);
                    MusicPlayer.pauseMediaPlayer();
                }
                MusicPlayer.isPause=!MusicPlayer.isPause;
            }
            if(id==R.id.button_fragment_button){
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, (View)findViewById(R.id.select_item_image), "music_picture_item");
                startActivityForResult(InfoMusicActivity.getIntent(MainActivity.this),0, options.toBundle());
            }
        }
    }
}
