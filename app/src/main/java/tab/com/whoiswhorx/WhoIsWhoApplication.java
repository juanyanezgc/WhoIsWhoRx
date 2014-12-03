package tab.com.whoiswhorx;

import android.app.Application;

import com.squareup.picasso.Picasso;

public class WhoIsWhoApplication extends Application {

    private static WhoIsWhoApplication sApp;
    private static Picasso sImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }

    public static Picasso getImageLoader() {
        if (sImageLoader == null) {
            sImageLoader = new Picasso.Builder(sApp).build();
        }

        return sImageLoader;
    }

}
