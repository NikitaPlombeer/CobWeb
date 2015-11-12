package cobwebinc.cobweb.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Никита on 12.11.2015.
 */
public class MyTwitter {

    public static final int WEBVIEW_REQUEST_CODE = 1582;

    private ProgressDialog pDialog;

    private boolean isLoggedIn;
    private Twitter twitter;
    private RequestToken requestToken;

    private SharedPreferences mSharedPreferences;

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;

    private Activity activity;

    private ConnectionDetector cd;

    public MyTwitter(Activity activity) {
        this.activity = activity;
        cd = new ConnectionDetector(activity.getApplicationContext());
//        /* initializing twitter parameters from string.xml */
        initTwitterConfigs();

		/* Enabling strict mode */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /* Initialize application preferences */
        mSharedPreferences = activity.getApplicationContext().getSharedPreferences(Const.PREFERENCE_NAME, 0);

        isLoggedIn = mSharedPreferences.getBoolean(Const.PREF_KEY_TWITTER_LOGIN, false);
        if(!isLoggedIn){
            Uri uri = activity.getIntent().getData();
            if (uri != null && uri.toString().startsWith(callbackUrl)) {

                String verifier = uri.getQueryParameter(oAuthVerifier);

                try {
					/* Getting oAuth authentication token */
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					/* Getting user id form access token */
                    long userID = accessToken.getUserId();
                    final User user = twitter.showUser(userID);
                    final String username = user.getName();

					/* save updated token */
                    saveTwitterInfo(accessToken);
                } catch (Exception e) {
                    Log.e("Failed to login Twitter", e.getMessage());
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == WEBVIEW_REQUEST_CODE) {
            String verifier = data.getExtras().getString(oAuthVerifier);
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                long userID = accessToken.getUserId();
                final User user = twitter.showUser(userID);
                String username = user.getName();

                saveTwitterInfo(accessToken);
            } catch (Exception e) {
                Log.e("Twitter Login Failed", e.getMessage());
            }
        }
    }
    /* Reading twitter essential configuration parameters from strings.xml */
    private void initTwitterConfigs() {
        consumerKey = Const.TWITTER_CONSUMER_KEY;
        consumerSecret = Const.TWITTER_CONSUMER_SECRET;//getString(R.string.twitter_consumer_secret);
        callbackUrl = Const.TWITTER_CALLBACK_URL;//getString(R.string.twitter_callback);
        oAuthVerifier = Const.URL_TWITTER_OAUTH_VERIFIER;//getString(R.string.twitter_oauth_verifier);
    }

    /**
     * Saving user information, after user is authenticated for the first time.
     * You don't need to show user to login, until user has a valid access toen
     */
    private void saveTwitterInfo(AccessToken accessToken) {

        long userID = accessToken.getUserId();

        User user;
        try {
            user = twitter.showUser(userID);

            String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putString(Const.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(Const.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(Const.PREF_KEY_TWITTER_LOGIN, true);
            e.putString(Const.PREF_USER_NAME, username);
            e.commit();

        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    public void login(){
        if (!isLoggedIn) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(callbackUrl);

                /**
                 *  Loading twitter login page on webview for authorization
                 *  Once authorized, results are received at onActivityResult
                 *  */
                final Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                activity.startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else{
            Toast.makeText(activity, "You are log in !", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Function to logout from twitter
     * It will just clear the application shared preferences
     * */
    public void logOut(){
        if(isLoggedIn) {
            // Clear the shared preferences
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.remove(Const.PREF_KEY_OAUTH_TOKEN);
            e.remove(Const.PREF_KEY_OAUTH_SECRET);
            e.remove(Const.PREF_KEY_TWITTER_LOGIN);
            e.apply();
        } else{
            Toast.makeText(activity, "You are not log in!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendStatus(String text){
        if(cd.isConnectingToInternet()) {
            if (text.trim().length() > 0)
                new updateTwitterStatus().execute(text);
            else
                Toast.makeText(activity, "Message is empty!!", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    class updateTwitterStatus extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Posting to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {

            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);

                // Access Token
                String access_token = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                StatusUpdate statusUpdate = new StatusUpdate(status);
                twitter4j.Status response = twitter.updateStatus(statusUpdate);

                Log.d("Status", response.getText());

            } catch (TwitterException e) {
                Log.d("Failed to post!", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

			/* Dismiss the progress dialog after sharing */
            pDialog.dismiss();
            Toast.makeText(activity, "Posted to Twitter!", Toast.LENGTH_SHORT).show();
        }

    }

    public Twitter getTwitter(){
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);
        // Access Token
        String access_token = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_TOKEN, "");
        // Access Token Secret
        String access_token_secret = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_SECRET, "");
        AccessToken accessToken = new AccessToken(access_token, access_token_secret);

        return new TwitterFactory(builder.build()).getInstance(accessToken);
    }
    public  ResponseList<Status> getHomeTimeLine(){
        try {

            Twitter twitter = getTwitter();
            ResponseList<Status> statuses =  twitter.getHomeTimeline(new Paging(1));
            return statuses;
//            for (int i = 0; i < statuses.size(); i++) {
//                Log.d("twitter", statuses.get(i).getUser().getName() + ": "+statuses.get(i).getText());
//                statuses.get(i).getUser().getProfileImageURL();
//            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
