package h.com.mylibrary;

import android.os.Parcel;
import android.os.Parcelable;

public class Beauty implements Parcelable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public Beauty() {
    }

    protected Beauty(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<Beauty> CREATOR = new Creator<Beauty>() {
        @Override
        public Beauty createFromParcel(Parcel source) {
            return new Beauty(source);
        }

        @Override
        public Beauty[] newArray(int size) {
            return new Beauty[size];
        }
    };

    public void readFromParcel(Parcel data){

    }
}
