package com.example.proyectoupet.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain =  true)
public class Parada implements Parcelable {

    private Double latitude;
    private Double longitude;
    private String title;

    public Parada(Double latitude, Double longitude, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(title);
    }

    public Parada(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<Parada> CREATOR = new Parcelable.Creator<Parada>() {
        public Parada createFromParcel(Parcel source) {
            return new Parada(source);
        }

        public Parada[] newArray(int size) {
            return new Parada[size];
        }
    };
}
