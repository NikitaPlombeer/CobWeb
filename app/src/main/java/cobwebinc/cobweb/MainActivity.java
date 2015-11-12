package cobwebinc.cobweb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import cobwebinc.cobweb.twitter.Const;
import cobwebinc.cobweb.twitter.MyTwitter;

public class MainActivity extends AppCompatActivity {

    Animation anim;
    Animation anim2;
    ImageButton imageButton;
    ImageButton imageButton2;
    MyTwitter twitter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Const.init(this);

        Typeface face = Typeface.createFromAsset(getAssets(), "HelveticaNeueCyr-Light.otf");
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setTypeface(face);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        tv2.setTypeface(face);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        tv3.setTypeface(face);
        anim = AnimationUtils.loadAnimation(this, R.anim.anim);
        anim2 = AnimationUtils.loadAnimation(this, R.anim.anim2);
        tv.startAnimation(anim2);
        tv2.startAnimation(anim);
        tv3.startAnimation(anim);
        imageButton = (ImageButton)findViewById(R.id.imageButton);
        imageButton.startAnimation(anim);
        imageButton2 = (ImageButton)findViewById(R.id.imageButton2);
        imageButton2.startAnimation(anim);

        twitter = new MyTwitter(this);
        twitter.login();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        twitter.onActivityResult(requestCode, resultCode, data);
    }

    public void onLogin (View view) {
//        twitter.getHomeTimeLine();
        Intent intent = new Intent(MainActivity.this, LentaActivity.class);
        startActivity(intent);
    }

    public void onRegister (View view) {
        Intent intent = new Intent(MainActivity.this, Registration.class);
        startActivity(intent);
    }


}
