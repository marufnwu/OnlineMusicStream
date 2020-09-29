package com.maruf.is.rex.audiostory.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Categories implements Parcelable {
    public int id;
    public String name, fmName, image;

    public Categories() {
    }

    public Categories(int id, String name, String fmName, String image) {
        this.id = id;
        this.name = name;
        this.fmName = fmName;
        this.image = image;
    }

    protected Categories(Parcel in) {
        id = in.readInt();
        name = in.readString();
        fmName = in.readString();
        image = in.readString();
    }

    public static final Creator<Categories> CREATOR = new Creator<Categories>() {
        @Override
        public Categories createFromParcel(Parcel in) {
            return new Categories(in);
        }

        @Override
        public Categories[] newArray(int size) {
            return new Categories[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFmName() {
        return fmName;
    }

    public void setFmName(String fmName) {
        this.fmName = fmName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(fmName);
        parcel.writeString(image);
    }
}
