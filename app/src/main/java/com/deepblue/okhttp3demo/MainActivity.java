package com.deepblue.okhttp3demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textview;
    private Button button;
    private OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
    }

    /**
     * initWidget
     */
    private void initWidget() {
        textview = (TextView) findViewById(R.id.tv);
        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        setTextContent("http://blog.csdn.net/bruceyangjie/article/details/51351864");
        sendJsonToServer();
    }

    private void sendJsonToServer() {
        String json = bowlingJson("Jesse", "Jake");
        try {
            String result = post("http://blog.csdn.net/lmj623565791/article/details/47911083", json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url)
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .post(body).build();
        //
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String bowlingJson(String player1, String player2) {
        return "{'winCondition':'HIGH_SCORE',"
                + "'name':'Bowling',"
                + "'round':4,"
                + "'lastSaved':1367702411696,"
                + "'dateStarted':1367702378785,"
                + "'players':["
                + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                + "]}";
    }

    /**
     * 设置内容到tv
     *
     * @param url
     */
    private void setTextContent(String url) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                return getResultString(strings[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    textview.setText(s);
                }
            }
        }.execute(url);

    }

    /**
     * 同步
     *
     * @param urls
     */
    private String getResultString(String urls) {
        String string = null;
        String requestUrl = urls.trim();
        if (TextUtils.isEmpty(requestUrl)) {
            Request request = new Request.Builder().url(urls).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() == 200 && response.isSuccessful()) {
                    string = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return string;
    }
}
