package com.maruf.is.rex.audiostory.Paging;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import com.maruf.is.rex.audiostory.Model.Audio;

public class ItemViewModel extends ViewModel {
    public LiveData<PagedList<Audio>> itemPagedList;
    public MediatorLiveData<PagedList<Audio>> mediatorLiveData;
    LiveData<PageKeyedDataSource<Integer, Audio>> liveDataSource;
    private int cat;


    public ItemViewModel(int cat) {
        Log.d("ItemViewModel", "call");
        mediatorLiveData = new MediatorLiveData<>();
      ItemDataSourceFactory itemDataSourceFactory = new ItemDataSourceFactory(cat);
      liveDataSource = itemDataSourceFactory.getItemLiveDataSource();

        PagedList.Config config =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(true)
                        .setPageSize(ItemDataSource.PAGE_SIZE)
                        .build();

        itemPagedList = (new LivePagedListBuilder(itemDataSourceFactory, config)).build();


    }

     public MediatorLiveData getdata(){
         mediatorLiveData.addSource(itemPagedList, new Observer<PagedList<Audio>>() {
             @Override
             public void onChanged(PagedList<Audio> audio) {
                 mediatorLiveData.setValue(audio);
             }
         });

         return mediatorLiveData;
     }


}
