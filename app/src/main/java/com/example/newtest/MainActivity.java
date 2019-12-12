package com.example.newtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.here.sdk.core.GeoCircle;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.TextFormat;
import com.here.sdk.core.errors.EngineInstantiationException;
import com.here.sdk.search.CategoryId;
import com.here.sdk.search.GeocodingEngine;
import com.here.sdk.search.ReverseGeocodingCallback;
import com.here.sdk.search.ReverseGeocodingEngine;
import com.here.sdk.search.SearchCallback;
import com.here.sdk.search.SearchEngine;
import com.here.sdk.search.SearchError;
import com.here.sdk.search.SearchOptions;
import com.here.sdk.search.SearchResult;
import com.here.sdk.searchcommon.Address;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.here.sdk.search.CategoryId.ACCOMMODATION;
import static com.here.sdk.search.CategoryId.AIRPORT;
import static com.here.sdk.search.CategoryId.COFFEE_TEA;
import static com.here.sdk.search.CategoryId.GOING_OUT;
import static com.here.sdk.search.CategoryId.LEISURE_OUTDOOR;
import static com.here.sdk.search.CategoryId.NATURAL_GEOGRAPHICAL;
import static com.here.sdk.search.CategoryId.PETROL_STATION;

public class MainActivity extends AppCompatActivity {

    GeoCoordinates yosemite = new GeoCoordinates(37.865101, -119.538330, 0);
    GeoCircle poi = new GeoCircle(yosemite, 5000);
    List<CategoryId> categoryIdlist = Arrays.asList(LEISURE_OUTDOOR, NATURAL_GEOGRAPHICAL);
    int maxSearchResults = 15;
    SearchOptions searchOptions = new SearchOptions(
            LanguageCode.EN_US,
            TextFormat.PLAIN,
            maxSearchResults);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Places.initialize(getApplicationContext(), "AIzaSyDcuYKOnoj6l8uFM8mhZihdtmmdGzjBiJQ");

        Button search_btn = (Button)findViewById(R.id.search_button);
        search_btn.setOnClickListener(search_listener);
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        Context i_context = getApplicationContext();
        CharSequence i_text = "NW status: " +  isConnected;
        int i_duration = Toast.LENGTH_LONG;

        Toast newtoast = Toast.makeText(i_context, i_text, i_duration);
        newtoast.show();

    };

    private View.OnClickListener search_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                SearchEngine searchEngine = new SearchEngine();
                searchEngine.search(poi, categoryIdlist, searchOptions, new SearchCallback() {
                    @Override
                    public void onSearchCompleted(@Nullable SearchError searchError, @Nullable List<SearchResult> list) {
                        if (searchError != null) {
                            Context context = getApplicationContext();
                            CharSequence text = "Search Error: " + searchError.toString();
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            return;
                        }
                        if (list.isEmpty()) {
                            Context context = getApplicationContext();
                            CharSequence text = "Search: List Empty";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            Context context = getApplicationContext();
                            CharSequence text = "List size :" + list.size();
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            ScrollView scrollView = (ScrollView) findViewById(R.id.r_sv);
                            LinearLayout sv_ll = (LinearLayout) findViewById(R.id.sv_ll);
                            for (SearchResult list_item: list) {
                                sv_ll.addView(buildCard(list_item, getApplicationContext()));
                            }
                        }

                    }
                });
            } catch (EngineInstantiationException e) {
                new RuntimeException("Initialization of SearchEngine failed: " + e.error.name());
            }
        }
    };

    private CardView buildCard(SearchResult item, Context context) {
      CardView newCard = new CardView(context);
      newCard.setMinimumHeight(190);
      RelativeLayout.LayoutParams layoutParams0 = new RelativeLayout.LayoutParams(
              RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
      layoutParams0.setMargins(15, 15, 15, 15);
      newCard.setLayoutParams(layoutParams0);

      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
              RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
      layoutParams.setMargins(15, 15, 0, 0);

      TextView text = new TextView(context);
      text.setText(item.title);
      text.setLayoutParams(layoutParams);
      newCard.addView(text, 0);
      RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
              RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
      layoutParams1.setMargins(15, 60, 0, 0);
      layoutParams1.addRule(RelativeLayout.BELOW, text.getId());

      TextView text2 = new TextView(context);
      text2.setText(item.category.toString());
      text2.setLayoutParams(layoutParams1);
      newCard.addView(text2, 1);

      RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
              RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
      layoutParams2.setMargins(15, 105, 0, 0);
      layoutParams2.addRule(RelativeLayout.BELOW, text2.getId());

      TextView text3 = new TextView(context);
      text3.setText("Latitude " + item.coordinates.latitude + ", Longitude " + item.coordinates.longitude);
      text3.setLayoutParams(layoutParams2);
      newCard.addView(text3, 2);

      GeoApiContext geoApiContext = new GeoApiContext.Builder()
              .apiKey("AIzaSyDcuYKOnoj6l8uFM8mhZihdtmmdGzjBiJQ")
              .build();
      try {
          GeocodingResult[] results = GeocodingApi.newRequest(geoApiContext)
                  .latlng(new LatLng(item.coordinates.latitude, item.coordinates.longitude)).await();

          RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(
                  RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
          layoutParams3.setMargins(15, 160, 0, 0);
          layoutParams3.addRule(RelativeLayout.BELOW, text2.getId());

          TextView text4 = new TextView(context);
          text4.setText("PlaceID: " + results[0].formattedAddress);
          text4.setLayoutParams(layoutParams3);
          newCard.addView(text4, 2);
          newCard.setClickable(true);
          newCard.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent imgr_intent = new Intent(MainActivity.this, imgr_disp.class);
                  imgr_intent.putExtra("PlaceID", results[0].placeId);
                  MainActivity.this.startActivity(imgr_intent);
              }
          });
      }
      catch(InterruptedException e) {
          new RuntimeException("Reverse GeoCode request failed: " + e.toString());
      }
      catch(ApiException e) {
          new RuntimeException("Reverse GeoCode request failed: " + e.toString());
      }
      catch(IOException e) {
          new RuntimeException("Reverse GeoCode request failed: " + e.toString());
      }
      return newCard;
    };

    private void showImageResults() {

    }
}