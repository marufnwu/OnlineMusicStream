package com.maruf.is.rex.audiostory.Paging;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.maruf.is.rex.audiostory.Model.Audio;

public class ItemDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, Audio>> itemLiveDataSource
            = new MutableLiveData<>();
    private int cat;

    public ItemDataSourceFactory(int cat) {
        this.cat = cat;
    }

    @Override
    public DataSource create() {

        ItemDataSource itemDataSource = new ItemDataSource(cat);
        itemLiveDataSource.postValue(itemDataSource);
        return itemDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, Audio>> getItemLiveDataSource() {
        ItemDataSource itemDataSource = new ItemDataSource(cat);
        itemLiveDataSource.postValue(itemDataSource);

        return itemLiveDataSource;
    }
}
