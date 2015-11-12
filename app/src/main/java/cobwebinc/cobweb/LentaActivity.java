package cobwebinc.cobweb;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cobwebinc.cobweb.twitter.MyTwitter;
import cobwebinc.cobweb.twitter.TwitterFragment;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

public class LentaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenta);

        MyTwitter twitter = new MyTwitter(this);
        ResponseList<Status> statuses = twitter.getHomeTimeLine();

//        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.mainLineatLayout);
//        LinearLayout.LayoutParams lp =
//                (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
//        lp.setMargins(0, 2, 0, 2);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (int i = 0; i < statuses.size(); i++) {
            TwitterFragment fragment = TwitterFragment.newInstanse(statuses.get(i));
            fragmentTransaction.add(R.id.mainLineatLayout , fragment, "tf" + String.valueOf(i));
        }
        fragmentTransaction.commit();

    }

}
