package com.example.newtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class imgr_disp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent this_intent = getIntent();
        String placeId = this_intent.getStringExtra("PlaceID");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgr_disp);

        LinearLayout imgr_ll = (LinearLayout)findViewById(R.id.imgr_sv_ll);
        ImageView imageView = new ImageView(getApplicationContext());

        PlacesClient placesClient = Places.createClient(this);

        List<Place.Field> place_fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, place_fields);
        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            Place res_place = response.getPlace();
            PhotoMetadata photoMetadata = res_place.getPhotoMetadatas().get(0);

            String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
                imgr_ll.addView(imageView);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e(DEVICE_POLICY_SERVICE, "Place not found: " + exception.getMessage());
                }
            });
        }).addOnFailureListener((exception) -> {
            Log.e(DEVICE_POLICY_SERVICE, "Place not found: " + exception.getMessage());
        });
    }
}
