package com.dreamhunterztech.media_player;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.dreamhunterztech.media_player.searchelements.DataHelper;
import com.dreamhunterztech.media_player.searchelements.DataSuggestion;

import java.util.List;

/**
 * Created by Dreamer on 27-02-2018.
 */

public class Mainframe extends AppCompatActivity {
    FragmentManager manager;
    FloatingSearchView vidsearch;
    ImageButton style_btn,urlinput_btn;
    private String mLastQuery = "";
    int style =0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* style_btn = findViewById(R.id.style_btn);
        urlinput_btn = findViewById(R.id.url_btn);*/
        vidsearch = findViewById(R.id.floating_search_view);
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, new Mainframe_style3());
        transaction.commit();


       vidsearch.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
           @SuppressLint("RestrictedApi")
           @Override
           public void onActionMenuItemSelected(MenuItem item) {

               if (item instanceof MenuBuilder) {
                   ((MenuBuilder) item).setOptionalIconsVisible(true);
               }

               if(item.getItemId() == R.id.menu_style)
               {
                   if(style==0) {
                       FragmentTransaction transaction = manager.beginTransaction();
                       transaction.replace(R.id.container, new Mainframe_style1());
                       transaction.commit();
                       style=1;
                   }
                   else
                   {
                       FragmentTransaction transaction = manager.beginTransaction();
                       transaction.replace(R.id.container, new Mainframe_style3());
                       transaction.commit();
                       style=0;
                   }


               }

               if(item.getItemId() == R.id.menu_reload)
               {
                   if(style==0) {
                       FragmentTransaction transaction = manager.beginTransaction();
                       transaction.replace(R.id.container, new Mainframe_style3());
                       transaction.commit();;
                   }
                   else
                   {
                       FragmentTransaction transaction = manager.beginTransaction();
                       transaction.replace(R.id.container, new Mainframe_style1());
                       transaction.commit();
                   }

               }

               if(item.getItemId() == R.id.menu_urlinput)
               {
                   final Dialog urlinputpannel = new Dialog(Mainframe.this);
                   urlinputpannel.requestWindowFeature(Window.FEATURE_NO_TITLE);
                   urlinputpannel.getWindow().setWindowAnimations(R.style.animateddialog);
                   urlinputpannel.setCanceledOnTouchOutside(false);
                   urlinputpannel.setCancelable(false);
                   urlinputpannel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                   urlinputpannel.setContentView(R.layout.videourldailog);
                   ImageView urlinputpannelclose = urlinputpannel.findViewById(R.id.urlinputdialogclose);
                   urlinputpannelclose.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           urlinputpannel.dismiss();
                       }
                   });
                   EditText urilinputtxt = urlinputpannel.findViewById(R.id.urlinput);
                   Button gobtn = urlinputpannel.findViewById(R.id.go_btn);
                   gobtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           startActivity(new Intent(Mainframe.this, testplayer.class).putExtra("video", "test,>" +"http://playertest.longtailvideo.com/adaptive/bbbfull/bbbfull.m3u8"));
                       }
                   });
                   urlinputpannel.show();
               }

               if(item.getItemId() == R.id.menu_setting)
               {
                   startActivity(new Intent(Mainframe.this , Onelinevideolist.class));
               }
           }
       });

        vidsearch.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals(""))
                {
                    vidsearch.clearSuggestions();
                }

                else
                {

                    vidsearch.showProgress();
                    DataHelper.findSuggestions(Mainframe.this, newQuery, 5,
                            250, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<DataSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    vidsearch.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    vidsearch.hideProgress();
                                }
                            });

                }

            }
        });


        vidsearch.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                mLastQuery = searchSuggestion.getBody();
                openvideofile(mLastQuery);
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                if(query.isEmpty())
                {
                    Toast.makeText(Mainframe.this,"Video name is necessary",Toast.LENGTH_SHORT).show();
                }

                else {
                    openvideofile(query);
                }


            }
        });

        vidsearch.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                vidsearch.swapSuggestions(DataHelper.getHistory(Mainframe.this, 3));
            }

            @Override
            public void onFocusCleared() {

            }

        });

        vidsearch.setSuggestionsAnimDuration(500);

        }

    @Override
    protected void onStart() {
        super.onStart();
        /*style_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }


    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }


    private void openvideofile(final String videoname)
    {
        String str_video_path =DataHelper.getvideourl(videoname);
        if(str_video_path == null) {

            Toast.makeText(Mainframe.this , " searched video Not avialable",Toast.LENGTH_SHORT).show();
        }

        else {
            final String video_data = videoname + ",>" + str_video_path;
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(vidsearch.getWindowToken(), 0);
            startActivity(new Intent(Mainframe.this, testplayer.class).putExtra("video", video_data));
        }

    }

}



