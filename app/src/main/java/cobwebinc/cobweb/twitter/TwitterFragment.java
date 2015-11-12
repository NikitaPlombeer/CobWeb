package cobwebinc.cobweb.twitter;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;

import cobwebinc.cobweb.R;
import twitter4j.Status;

/**
 * Created by Никита on 06.11.2015.
 */
public class TwitterFragment extends Fragment{


    public static TwitterFragment newInstanse(Status status){
        TwitterFragment f = new TwitterFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", status.getUser().getName());
        bundle.putString("username_hash", status.getUser().getScreenName());
        bundle.putString("image_url", status.getUser().getProfileImageURL());
        bundle.putString("time", "9ч");
        bundle.putString("text", status.getText());

        f.setArguments(bundle);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_twitter, container, false);
        TextView textView = (TextView)v.findViewById(R.id.textViewPost);
        TextView timeTextView = (TextView)v.findViewById(R.id.textViewTime);
        TextView nameTextView = (TextView)v.findViewById(R.id.textViewName);
        TextView hashNameTextView = (TextView)v.findViewById(R.id.textViewUserHash);
        ImageView imageView = (ImageView)v.findViewById(R.id.userImageView);

        String text = getArguments().getString("text");
        String username = getArguments().getString("username");
        String username_hash = getArguments().getString("username_hash");
        String time = getArguments().getString("time");
        String image_url = getArguments().getString("image_url");

        textView.setText(text);
        timeTextView.setText(time);
        nameTextView.setText(username);
        hashNameTextView.setText("@" + username_hash);

        new DownloadImageTask(imageView)
                .execute(image_url);
        return v;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}