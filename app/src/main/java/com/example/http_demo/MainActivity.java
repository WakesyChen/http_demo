package com.example.http_demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lv_news;

    private String url="http://www.imooc.com/api/teacher?type=4&num=30";
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
       lv_news= (ListView) findViewById(R.id.lv_news);
        //执行异步任务
        new NewsAsyncTask().execute(url);

    }


    /**
     * Created by Wakesy on 2016/5/28.
     *
     * 通过异步任务加载ListView,实现网络的异步访问
     * 第一个参数，URL，第二个，进度值，第三个，返回值
     */
    public class NewsAsyncTask extends AsyncTask<String ,Void,List<NewsBean>> {
        @Override



        protected List<NewsBean> doInBackground(String... params) {

            return getJsonData(params[0]);//根据第一个参数，URL得到Json数据的方法
        }



        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) {
            NewsAdapter newsAdapter=new NewsAdapter(MainActivity.this,newsBeans,lv_news);
            lv_news.setAdapter(newsAdapter);


        }
    }


    //根据第一个参数，URL得到Json数据的方法,并存入list<NewsBean>中
    private List<NewsBean> getJsonData(String url) {
        List<NewsBean>datalist=new ArrayList<>();
        try {
            String JsonString=readStream(new URL(url).openStream());//得到Json的输入流，并读取出字符串
            Log.i("wakesy",JsonString);

            /*把Json中的值传给datalist*/

            JSONObject jsonObject;//Json对象
            NewsBean newsBean;
        jsonObject=new JSONObject(JsonString);//传入Json字符串的参数的构造方法
            JSONArray jsonArray=jsonObject.getJSONArray("data");//得到Json数组
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject=jsonArray.getJSONObject(i);//得到当前Json对象，然后赋值给NewBean对象
                newsBean=new NewsBean();
                newsBean.NewsIconUrl=jsonObject.getString("picSmall");
                newsBean.NewsTitle=jsonObject.getString("name");
                newsBean.NewsContent=jsonObject.getString("description");
                datalist.add(newsBean);


            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return datalist;
    }


    //读取Json数据
    private String readStream(InputStream is){
        StringBuffer result=new StringBuffer();
        try {
            InputStreamReader isr=new InputStreamReader(is,"utf-8");//把字节流转化为字符流，并指定编码格式
            BufferedReader br=new BufferedReader(isr);//字符缓冲流


            String line="";
            while((line=br.readLine())!=null){
                result.append(line);//把读取的每一行添加到result中

            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

            return result.toString();
    }


}
