package cm.protectu.Map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationAddress {
    public static String getLocation(Context context, GeoPoint location){
        String result;
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addressesList = new ArrayList<>();
        try {
            addressesList = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressesList.size() > 0){
            if(addressesList.get(0).getLocality() == null)
                result = addressesList.get(0).getCountryName();
            else
                result = addressesList.get(0).getLocality() + ", " + addressesList.get(0).getCountryName();
        }
        else
            result = "Unknown";
        return result;
    }
}
