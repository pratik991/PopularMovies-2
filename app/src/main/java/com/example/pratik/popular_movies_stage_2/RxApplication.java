package com.example.pratik.popular_movies_stage_2;

import android.app.Application;
import com.example.pratik.popular_movies_stage_2.NetworkService;

/**
 * Created by Pratik on 11/9/17.
 */

public class RxApplication extends Application {
    private NetworkService mNetworkService;

    @Override
    public void onCreate() {
        super.onCreate();
        mNetworkService = new NetworkService();
        mNetworkService.clearCache();
    }

    public NetworkService getNetorkService()
    {
        return mNetworkService;
    }
}
