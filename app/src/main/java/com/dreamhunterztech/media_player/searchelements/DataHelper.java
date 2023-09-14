package com.dreamhunterztech.media_player.searchelements;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Filter;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class DataHelper {

    static Boolean searchactivate=false;
    static HashMap<String,String> urldatalist=new HashMap<String, String>();

    private static final String COLORS_FILE_NAME = "videoname.json";

    private static List<DataWrapper> sColorWrappers = new ArrayList<>();

    private static List<DataSuggestion> sDataSuggestions =
            new ArrayList<>();

    public interface OnFindColorsListener {
        void onResults(List<DataWrapper> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<DataSuggestion> results);
    }

    public static List<DataSuggestion> getHistory(Context context, int count) {

        List<DataSuggestion> suggestionList = new ArrayList<>();
        DataSuggestion dataSuggestion;
        for (int i = 0; i < sDataSuggestions.size(); i++) {
            dataSuggestion = sDataSuggestions.get(i);
            dataSuggestion.setIsHistory(true);
            suggestionList.add(dataSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (DataSuggestion dataSuggestion : sDataSuggestions) {
            dataSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Activity activity, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
if(searchactivate==false) {
    sDataSuggestions.clear();

          Getdatalist list = new Getdatalist(activity);

            for ( int i = 0;i<list.getcount();i++) {
                sDataSuggestions.add(new DataSuggestion(list.getdata(i)));
                setvideourl(list.getdata(i),list.getdataurl(i));
            }

    searchactivate=true;

}

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                List<DataSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (DataSuggestion suggestion : sDataSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<DataSuggestion>() {
                    @Override
                    public int compare(DataSuggestion lhs, DataSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<DataSuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    public  static void  setvideourl(String key , String url)
    {
       urldatalist.put(key,url);
    }

    public static String getvideourl(String key)
    {
        String url;

        url = urldatalist.get(key);

        return url;
    }


    public static void findColors(Context context, String query, final OnFindColorsListener listener) {
        initColorWrapperList(context);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                List<DataWrapper> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {

                    for (DataWrapper color : sColorWrappers) {
                        if (color.getName().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(color);
                        }
                    }

                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<DataWrapper>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initColorWrapperList(Context context) {

        if (sColorWrappers.isEmpty()) {
            String jsonString = loadJson(context);
            sColorWrappers = deserializeColors(jsonString);
        }
    }

    private static String loadJson(Context context) {

        String jsonString;

        try {
            InputStream is = context.getAssets().open(COLORS_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return jsonString;
    }

    private static List<DataWrapper> deserializeColors(String jsonString) {

        Gson gson = new Gson();

        Type collectionType = new TypeToken<List<DataWrapper>>() {
        }.getType();
        return gson.fromJson(jsonString, collectionType);
    }


}
