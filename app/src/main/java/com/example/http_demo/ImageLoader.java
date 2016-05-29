package com.example.http_demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Wakesy on 2016/5/28.
 * <p/>
 * 用来加载图片的类
 */
public class ImageLoader {

    private ImageView mImageView;
    private String mUrl;

    private LruCache<String,Bitmap>mCaches;//内存

    //创建内存
    public ImageLoader() {
        int maxSize = (int) Runtime.getRuntime().maxMemory();//得到最大内存
        int cacheSize = maxSize / 4;//分配用于缓存的内存

        //创建内存
        mCaches = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                //每次存入缓存时调用，返回存入的对象大小
                return value.getByteCount();
            }
        };
    }

        //添加Bitmap到缓存
        public void addBitmapToCache(String url,Bitmap bitmap){//键值对
        if(getBitmapFromCache(url)==null){//如果cache中没有就添加到Cache中
            mCaches.put(url,bitmap);//类似map

        }


    }

    //得到缓存中的数据
    public Bitmap getBitmapFromCache(String mUrl){//传入键值

        return mCaches.get(mUrl);//返回对应的Bitmap值
    }

    private Handler handler = new Handler() {

        //在这个方法中更新UI

        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            //只有当当前的图片URL和adapter传递过来的相同时才加载图片，防止反复加载
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };


    //方法一：用多线程的方法加载图片
    public void loaderImageByThread(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;

        new Thread() {

            @Override
            public void run() {
                Bitmap bitmap = getBitmapByUrl(url);//子线程中不能直接更新UI，使用handler发送消息
                Message message = Message.obtain();//从消息队列中获取一个消息
                message.obj = bitmap;//把bitmap赋值给message.obj
                handler.sendMessage(message);//发送消息给handler

            }
        }.start();

    }

    //方法二：用AsyncTask加载图片
    public void loaderImageViewByAsyncTask(ImageView imageView, String mUrl) {
        //得到缓存中的Bitmap
        Bitmap bitmap=getBitmapFromCache(mUrl);
        //bitmap为空就下载，非空就直接使用
        if(bitmap==null){

            new NewsAsyncTask(imageView,mUrl).execute(mUrl);
        }
        else{
            imageView.setImageBitmap(bitmap);
        }



    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImageView;
        private String mUrl;


        public NewsAsyncTask(ImageView imageView,String url) {
            this.mImageView = imageView;
            this.mUrl=url;
        }



        @Override
        protected Bitmap doInBackground(String... params) {
            String url=params[0];
            //从缓存中读取对应的图片
            Bitmap bitmap=getBitmapByUrl(url);
            if(bitmap!=null){
            //避免下次使用还要下载,下载后就存入缓存,
           addBitmapToCache(url,bitmap);}
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //只有当当前的图片URL和adapter传递过来的相同时才加载图片，防止反复加载
            if(mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

/*
    //加载部分可见的图片
    public void LoadPartImageView(int start,int end){

        for (int i = start; i <end ; i++) {
            String url=NewsAdapter.URLS[i];//得到可见项目起始中的各个url
            //得到缓存中的Bitmap
            Bitmap bitmap=getBitmapFromCache(mUrl);
            //bitmap为空就下载，非空就直接使用
            if(bitmap==null){

                new NewsAsyncTask(imageView,mUrl).execute(mUrl);
            }
            else{
                imageView.setImageBitmap(bitmap);
            }

        }



    }*/
    //通过图片的URL得到Bitmap
    public Bitmap getBitmapByUrl(String urlString) {
        InputStream is = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            is.close();
//            Thread.sleep(1000);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


}

