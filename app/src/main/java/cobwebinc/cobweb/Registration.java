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


public class Registration extends AppCompatActivity {


    EditText ed;
    EditText ed2;
    EditText ed3;
    EditText ed4;
    Animation anim;
    String s1;
    String s2;
    String s3;
    String s4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        Typeface face = Typeface.createFromAsset(getAssets(), "HelveticaNeueCyr-Light.otf");
        ed = (EditText) findViewById(R.id.editText);
        ed.setTypeface(face);
        ed2 = (EditText) findViewById(R.id.editText2);
        ed2.setTypeface(face);
        ed3 = (EditText) findViewById(R.id.editText3);
        ed3.setTypeface(face);
        ed4 = (EditText) findViewById(R.id.editText4);
        ed4.setTypeface(face);
        TextView tv = (TextView) findViewById(R.id.textView3);
        tv.setTypeface(face);
        TextView tv2 = (TextView) findViewById(R.id.textView8);
        tv2.setTypeface(face);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
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


    public static void hideKeyboard(Registration activity){
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null){
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }


    public void Register (View view) {
        anim = AnimationUtils.loadAnimation(this, R.anim.anim3);
        s1 = ed.getText().toString().trim();
        s2 = ed2.getText().toString().trim();
        s3 = ed3.getText().toString().trim();
        s4 = ed4.getText().toString().trim();
        if(s1.isEmpty()){
            ed.startAnimation(anim);
        }else {
            if(s2.isEmpty()||s3.isEmpty() || (!s2.equals(s3)))  {
                ed2.startAnimation(anim);
                ed3.startAnimation(anim);
            }
            else {
                if(s4.isEmpty())  {
                    ed4.startAnimation(anim);
                }
                else {
                    new RequestTask().execute("http://teremok.k236.net/register.php");
                }
            }
        }
    }


    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String st="Nice!";
            String st2="Error-Login";
            try {
                //создаем запрос на сервер
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();

                //он у нас будет посылать post запрос
                HttpPost postMethod = new HttpPost(params[0]);

                //будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                //передаем параметры из наших текстбоксов

                //логин
                nameValuePairs.add(new BasicNameValuePair("login", s1));

                //пароль
                nameValuePairs.add(new BasicNameValuePair("password",s2));

                nameValuePairs.add(new BasicNameValuePair("email",s4));
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //получаем ответ от сервера
                String response = hc.execute(postMethod, res);
                //посылаем на вторую активность полученные параметры
                Log.v("Registration", response);
                if (response.equals(st)){
                    Intent intent=new Intent(Registration.this,Verify.class);
                    intent.putExtra("email", ed4.getText().toString());
                    startActivity(intent);
                }else {
                    if (response.equals(st2)) {
                        runOnUiThread(runn1);
                        TimeUnit.SECONDS.sleep(5);
                        runOnUiThread(runn2);
                    }else {
                        runOnUiThread(runn3);
                        TimeUnit.SECONDS.sleep(5);
                        runOnUiThread(runn2);
                    }
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
            TextView tv2 = (TextView) findViewById(R.id.textView8);
            tv2.setText("Такой логин уже зарегистрирован..");
        }
    };

    Runnable runn2 = new Runnable() {
        @Override
        public void run() {
            TextView tv2 = (TextView) findViewById(R.id.textView8);
            tv2.setText("");
        }
    };

    Runnable runn3 = new Runnable() {
        @Override
        public void run() {
            TextView tv2 = (TextView) findViewById(R.id.textView8);
            tv2.setText("Такая почта уже зарегистрирована..");
        }
    };


}
