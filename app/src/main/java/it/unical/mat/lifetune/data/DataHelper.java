package it.unical.mat.lifetune.data;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataHelper {

    private static final String COLORS_FILE_NAME = "colors.json";

    private static List<ColorWrapper> sColorWrappers = new ArrayList<>();

    private static List<ColorSuggestion> sColorSuggestions =
            new ArrayList<>(Arrays.asList(
                    new ColorSuggestion("green"),
                    new ColorSuggestion("blue"),
                    new ColorSuggestion("pink"),
                    new ColorSuggestion("purple"),
                    new ColorSuggestion("brown"),
                    new ColorSuggestion("gray"),
                    new ColorSuggestion("Granny Smith Apple"),
                    new ColorSuggestion("Indigo"),
                    new ColorSuggestion("Periwinkle"),
                    new ColorSuggestion("Mahogany"),
                    new ColorSuggestion("Maize"),
                    new ColorSuggestion("Mahogany"),
                    new ColorSuggestion("Outer Space"),
                    new ColorSuggestion("Melon"),
                    new ColorSuggestion("Yellow"),
                    new ColorSuggestion("Orange"),
                    new ColorSuggestion("Red"),
                    new ColorSuggestion("Orchid")));

    public interface OnFindColorsListener {
        void onResults(List<ColorWrapper> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<ColorSuggestion> results);
    }

    public static List<ColorSuggestion> getHistory(Context context, int count) {

        List<ColorSuggestion> suggestionList = new ArrayList<>();
        ColorSuggestion colorSuggestion;
        for (int i = 0; i < sColorSuggestions.size(); i++) {
            colorSuggestion = sColorSuggestions.get(i);
            colorSuggestion.setIsHistory(true);
            suggestionList.add(colorSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (ColorSuggestion colorSuggestion : sColorSuggestions) {
            colorSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                List<ColorSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (ColorSuggestion suggestion : sColorSuggestions) {
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
                Collections.sort(suggestionList, new Comparator<ColorSuggestion>() {
                    @Override
                    public int compare(ColorSuggestion lhs, ColorSuggestion rhs) {
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
                    listener.onResults((List<ColorSuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    public static void findColors(Context context, String query, final OnFindColorsListener listener) {
        initColorWrapperList(context);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                List<ColorWrapper> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {

                    for (ColorWrapper color : sColorWrappers) {

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
                    listener.onResults((List<ColorWrapper>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initColorWrapperList(Context context) {

//        if (sColorWrappers.isEmpty()) {
//            String jsonString = loadJson(context);
//            sColorWrappers = deserializeColors(jsonString);
//        }
    }

}