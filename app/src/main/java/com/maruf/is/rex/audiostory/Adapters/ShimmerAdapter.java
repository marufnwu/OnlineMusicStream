package com.maruf.is.rex.audiostory.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maruf.is.rex.audiostory.Common.Common;
import com.maruf.is.rex.audiostory.R;



public class ShimmerAdapter extends RecyclerView.Adapter<ShimmerAdapter.ViewHolder> {

    Context context;
    String layoutType;

    public ShimmerAdapter(Context context, String layoutType) {
        this.context = context;
        this.layoutType = layoutType;
    }

    @NonNull
    @Override
    public ShimmerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        if (layoutType.equals(Common.SHIMMER_FOR_LATEST_UPDATED)){
             view = LayoutInflater.from(context).inflate(R.layout.layout_shimmer_placeholder, null);

        }else if(layoutType.equals(Common.SHIMMER_FOR_AUDIO_LIST)){
             view = LayoutInflater.from(context).inflate(R.layout.layout_shimmer_audio_list,  parent, false);

        }
        assert view != null;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShimmerAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
