package cobwebinc.cobweb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Verify extends AppCompatActivity {
    EditText editText;
    Animation anim;
    String s1;
    String email;
    String login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        Typeface face = Typeface.createFromAsset(getAssets(), "HelveticaNeueCyr-Light.otf");
        editText = (EditText) findViewById(R.id.editText);
        editText.setTypeface(face);
        TextView tv = (TextView) findViewById(R.id.textView5);
        tv.setTypeface(face);
        TextView tv2 = (TextView) findViewById(R.id.textView6);
        tv2.setTypeface(face);
        anim = AnimationUtils.loadAnimation(this, R.anim.anim3);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        email = intent.getStringExtra("email");



    }

    public void Verify (View view) {
        editText = (EditText)findViewById(R.id.editText);
        anim = AnimationUtils.loadAnimation(this, R.anim.anim3);
        s1 = editText.getText().toString().trim();
        if(s1.isEmpty()){
            editText.startAnimation(anim);
        }
        else {
            new RequestTask().execute("http://teremok.k236.net/verify.php");
        }
    }





    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))
        {

            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    public static void hideKeyboard(Verify activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }

    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String st="Nice!";
            try {
//создаем запрос на сервер
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
//он у нас будет посылать post запрос
                HttpPost postMethod = new HttpPost(params[0]);
//будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
//передаем параметры из наших текстбоксов
//лоигн

                nameValuePairs.add(new BasicNameValuePair("email",email));
                nameValuePairs.add(new BasicNameValuePair("login",login));

                nameValuePairs.add(new BasicNameValuePair("verify",s1));
//собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//получаем ответ от сервера
                String response = hc.execute(postMethod, res);
//посылаем на вторую активность полученные параметры
                Log.v("Verify", response);
                if (response.equals(st)){
                    Intent intent=new Intent(Verify.this,Login.class);
                    startActivity(intent);
                } else {
                    runOnUiThread(runn1);
                    TimeUnit.SECONDS.sleep(3);
                    runOnUiThread(runn2);
                }





            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }


    }
    Runnable runn1 = new Runnable() {
        @Override
        public void run() {
            TextView tv = (TextView) findViewById(R.id.textView5);
            tv.setText("Неверный проверочный код");
        }
    };

    Runnable runn2 = new Runnable() {
        @Override
        public void run() {
            TextView tv = (TextView) findViewById(R.id.textView5);
            tv.setText("Введите, пожалуйста, проверочный код, который пришел Вам на почту...");
        }
    };




}
