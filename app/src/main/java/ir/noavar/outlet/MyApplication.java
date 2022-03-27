package ir.noavar.outlet;

import android.app.Application;
import android.graphics.Typeface;

public class MyApplication extends Application {

    public static Typeface sans, light, brand, regular, solid;

    @Override
    public void onCreate() {
        super.onCreate();
        light = Typeface.createFromAsset(getAssets(), "fonts/falight.ttf");
        brand = Typeface.createFromAsset(getAssets(), "fonts/fabrands.ttf");
        regular = Typeface.createFromAsset(getAssets(), "fonts/faregular.ttf");
        solid = Typeface.createFromAsset(getAssets(), "fonts/fasolid.ttf");
        sans = Typeface.createFromAsset(getAssets(), "fonts/sans.ttf");
    }

    public Typeface getLight() {
        return light;
    }

    public Typeface getBrand() {
        return brand;
    }

    public Typeface getRegular() {
        return regular;
    }

    public Typeface getSolid() {
        return solid;
    }

    public Typeface getSans() {
        return sans;
    }
}
