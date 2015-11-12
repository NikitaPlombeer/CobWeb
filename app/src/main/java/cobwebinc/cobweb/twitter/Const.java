package cobwebinc.cobweb.twitter;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

/**
 * Created by Никита on 11.11.2015.
 */
public class Const {
    public static String TWITTER_CONSUMER_KEY = "fK98B1T6QAhlJWVYsOKARNnVY";
    public static String TWITTER_CONSUMER_SECRET = "xHIagEvTqjf9sn1lpYoo9nA5iVi48HrEgisdaXmrhX0UGwUZWI";

    // Preference Constants
    public static String PREFERENCE_NAME = "twitter_oauth";
    public static final String PREF_USER_NAME = "twitter_user_name";
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    public static final String TWITTER_CALLBACK_URL = "http://webcob.app";

    // Twitter oauth urls
    public static final String URL_TWITTER_AUTH = "auth_url";
    public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public  static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static void init(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
    }
}