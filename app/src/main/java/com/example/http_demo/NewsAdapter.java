package com.example.http_demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Wakesy on 2016/5/28.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{

    private List<NewsBean>datalist;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int mStart,mEnd;//当前可见项的起始位置
    public static  String URLS[];//用于保存所有的图片URL地址
    public NewsAdapter(Context context,List<NewsBean> datalist,ListView lv_news) {
        this.datalist=datalist;
        mInflater=LayoutInflater.from(context);
        mImageLoader=new ImageLoader();
        //保存所有的图片URL地址
        URLS=new String[datalist.size()];
        for (int i = 0; i < URLS.length; i++) {
            URLS[i]=datalist.get(i).NewsIconUrl;
        }
        //实现listview滑动监听接口
//        lv_news.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.item,null);
            viewHolder.bindView(convertView);
            convertView.setTag(viewHolder);

        }
        else{
            viewHolder= (ViewHolder) convertView.getTag();

        }
        viewHolder.item_img.setImageResource(R.mipmap.ic_launcher);//设置默认的图片
        String url="";
        url=datalist.get(position).NewsIconUrl;
        //用tag在ImageView中传递对应url过去，图片和url进行绑定，防止图片重复加载
        viewHolder.item_img.setTag(url);
        //通过多线程的方式加载图片
//        new ImageLoader().loaderImageByThread(viewHolder.item_img,url );

        //通过AsyncTask方式加载图片
        mImageLoader.loaderImageViewByAsyncTask(viewHolder.item_img, url);
        viewHolder.item_title.setText(datalist.get(position).NewsTitle);
        viewHolder.item_content.setText(datalist.get(position).NewsContent);




        return convertView;
    }
    //实现滑动监听接口的两个方法，使ListView在滑动时不加载，停止时加载所见项
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //滚动状态为停止时，加载当前可见的数据项
        if(scrollState==SCROLL_STATE_IDLE){


        }
        else{//滑动没有停止时，停止任务


        }

    }

                                                //第一个可见项目      //可见项目的个数
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mStart=firstVisibleItem;
            mEnd=firstVisibleItem+visibleItemCount;

    }

    public class ViewHolder {
        public ImageView item_img;
        public TextView item_title;
        public TextView item_content;

        public void bindView(View view){
            item_img= (ImageView) view.findViewById(R.id.item_iv );
            item_title= (TextView) view.findViewById(R.id.item_title);
            item_content= (TextView) view.findViewById(R.id.item_content);

        }


    }
}
