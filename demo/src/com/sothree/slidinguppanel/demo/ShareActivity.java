package com.sothree.slidinguppanel.demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * time: 16-1-5
 * description:分享的activity
 *
 * @author crab
 */
public class ShareActivity extends Activity {
    public RecyclerView mRecyclerView;
    public static final ArrayList<SiteInfo> SITES=new ArrayList<>();
    static {
        SITES.add(new SiteInfo("facebook","facebook",R.drawable.facebook));
        SITES.add(new SiteInfo("instagram","instagram",R.drawable.instagram));
        SITES.add(new SiteInfo("qq","qq",R.drawable.qq));
        SITES.add(new SiteInfo("qzone","qzone",R.drawable.qzone));
        SITES.add(new SiteInfo("sina","sina",R.drawable.sina));
        SITES.add(new SiteInfo("twitter","twitter",R.drawable.twitter));
        SITES.add(new SiteInfo("wx_friend","wx_friend",R.drawable.wx_friend));
        SITES.add(new SiteInfo("wx_friend_circle","wx_friend_circle",R.drawable.wx_friend_circle));
        SITES.add(new SiteInfo("facebook","facebook",R.drawable.facebook));
        SITES.add(new SiteInfo("instagram","instagram",R.drawable.instagram));
        SITES.add(new SiteInfo("qq","qq",R.drawable.qq));
        SITES.add(new SiteInfo("qzone","qzone",R.drawable.qzone));
        SITES.add(new SiteInfo("sina","sina",R.drawable.sina));
        SITES.add(new SiteInfo("twitter","twitter",R.drawable.twitter));
        SITES.add(new SiteInfo("wx_friend","wx_friend",R.drawable.wx_friend));
        SITES.add(new SiteInfo("wx_friend_circle","wx_friend_circle",R.drawable.wx_friend_circle));
        SITES.add(new SiteInfo("facebook","facebook",R.drawable.facebook));
        SITES.add(new SiteInfo("instagram","instagram",R.drawable.instagram));
        SITES.add(new SiteInfo("qq","qq",R.drawable.qq));
        SITES.add(new SiteInfo("qzone","qzone",R.drawable.qzone));
        SITES.add(new SiteInfo("sina","sina",R.drawable.sina));
        SITES.add(new SiteInfo("twitter","twitter",R.drawable.twitter));
        SITES.add(new SiteInfo("wx_friend","wx_friend",R.drawable.wx_friend));
        SITES.add(new SiteInfo("wx_friend_circle","wx_friend_circle",R.drawable.wx_friend_circle));
        SITES.add(new SiteInfo("facebook","facebook",R.drawable.facebook));
        SITES.add(new SiteInfo("instagram","instagram",R.drawable.instagram));
        SITES.add(new SiteInfo("qq","qq",R.drawable.qq));
        SITES.add(new SiteInfo("qzone","qzone",R.drawable.qzone));
        SITES.add(new SiteInfo("sina","sina",R.drawable.sina));
        SITES.add(new SiteInfo("twitter","twitter",R.drawable.twitter));
        SITES.add(new SiteInfo("wx_friend","wx_friend",R.drawable.wx_friend));
        SITES.add(new SiteInfo("wx_friend_circle","wx_friend_circle",R.drawable.wx_friend_circle));
        SITES.add(new SiteInfo("facebook","facebook",R.drawable.facebook));
        SITES.add(new SiteInfo("instagram","instagram",R.drawable.instagram));
        SITES.add(new SiteInfo("qq","qq",R.drawable.qq));
        SITES.add(new SiteInfo("qzone","qzone",R.drawable.qzone));
        SITES.add(new SiteInfo("sina","sina",R.drawable.sina));
        SITES.add(new SiteInfo("twitter","twitter",R.drawable.twitter));
        SITES.add(new SiteInfo("wx_friend","wx_friend",R.drawable.wx_friend));
        SITES.add(new SiteInfo("wx_friend_circle","wx_friend_circle",R.drawable.wx_friend_circle));
        SITES.add(new SiteInfo("facebook","facebook",R.drawable.facebook));
        SITES.add(new SiteInfo("instagram","instagram",R.drawable.instagram));
        SITES.add(new SiteInfo("qq","qq",R.drawable.qq));
        SITES.add(new SiteInfo("qzone","qzone",R.drawable.qzone));
        SITES.add(new SiteInfo("sina","sina",R.drawable.sina));
        SITES.add(new SiteInfo("twitter","twitter",R.drawable.twitter));
        SITES.add(new SiteInfo("wx_friend","wx_friend",R.drawable.wx_friend));
        SITES.add(new SiteInfo("wx_friend_circle","wx_friend_circle",R.drawable.wx_friend_circle));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resolver_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),
                3,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        final int gap = (int) getResources().getDimension(R.dimen.share_gap);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                Log.e("test", "width=" + view.getWidth());
                ViewGroup viewGroup = (ViewGroup) view;
                View child =  viewGroup.getChildAt(0);
                ImageView iconImage = (ImageView) child.findViewById(R.id.share_site_icon);
                FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) child.getLayoutParams();
                LinearLayout.LayoutParams iconParams= (LinearLayout.LayoutParams) iconImage.getLayoutParams();
                int position = parent.getChildAdapterPosition(view);
                int count = parent.getAdapter().getItemCount();
                if(position%3==0){
                    iconParams.gravity = Gravity.LEFT;
                    params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                }else if(position%3==1){
                    params.gravity = Gravity.CENTER;
                    iconParams.gravity = Gravity.CENTER_HORIZONTAL;
                }else if(position%3==2){
                    iconParams.gravity = Gravity.RIGHT;
                    params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                }
                if(count>3){
                    int rowCount = count / 3;
                    int mod = count % 3;
                    if(mod==0){
                        if(position/3<rowCount){
                            outRect.set(0,0,0,gap);
                        }
                    }else{
                        if(position/3<=rowCount){
                            outRect.set(0,0,0,gap);
                        }
                    }
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        ShareSiteAdapter adapter = new ShareSiteAdapter(SITES);
        mRecyclerView.setAdapter(adapter);
    }

    private static class ShareSiteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<SiteInfo> mSites;
        public ShareSiteAdapter(List<SiteInfo> sites) {
            mSites = sites;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater=LayoutInflater.from(context);
            View view=inflater.inflate(R.layout.vw_share_site_item,parent,false);
            return new SiteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SiteViewHolder viewHolder= (SiteViewHolder) holder;
            SiteInfo siteInfo = mSites.get(position);
            viewHolder.siteTitle.setText(siteInfo.siteTitle);
            viewHolder.siteIcon.setImageResource(siteInfo.siteIconRes);
//            if(position%3==0){
//                if(position==3){
//                    viewHolder.itemView.setBackgroundColor(Color.GRAY);
//                }else{
//                    viewHolder.itemView.setBackgroundColor(Color.RED);
//                }
//            }else if(position%3==1){
//                viewHolder.itemView.setBackgroundColor(Color.BLUE);
//            }else if(position%3==2){
//                viewHolder.itemView.setBackgroundColor(Color.GREEN);
//            }
        }

        @Override
        public int getItemCount() {
            return mSites == null ? 0 : mSites.size();
        }
    }
    private static class SiteViewHolder extends RecyclerView.ViewHolder{
        public ImageView siteIcon;
        public TextView siteTitle;
        public SiteViewHolder(View itemView) {
            super(itemView);
            siteIcon = (ImageView) this.itemView.findViewById(R.id.share_site_icon);
            siteTitle = (TextView) this.itemView.findViewById(R.id.share_site_title);
        }
    }
    private static final class SiteInfo {
        public String siteTitle;
        public int siteIconRes;
        public String siteCode;
        public SiteInfo(String siteCode,String siteTitle,int siteIconRes){
            this.siteTitle = siteTitle;
            this.siteIconRes = siteIconRes;
            this.siteCode = siteCode;
        }
        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof SiteInfo) {
                SiteInfo other = (SiteInfo) o;
                String otherSiteName = other.siteCode;
                if (TextUtils.isEmpty(otherSiteName)) {
                    return false;
                }
                if (!otherSiteName.equals(siteCode)) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return 11 + siteCode.hashCode() * 31;
        }
    }
}
