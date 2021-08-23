package info.legeay.meteo.util;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;

public class Network {

    public static boolean isInternetAvailable(Context context) {

        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if(connectivityManager.getActiveNetworkInfo() == null || !connectivityManager.getActiveNetworkInfo().isConnected()) return false;
        //return true;
        String command = "ping -c 1 google.com";
        try {
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException | IOException e) {
            return false;
        }
    }
}
