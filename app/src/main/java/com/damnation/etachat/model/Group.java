package com.damnation.etachat.model;


import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Group implements Parcelable {

    @PrimaryKey
    @NonNull
    private String _id;
    private String username;

    public Group(@NonNull String _id, String username) {
        this._id = _id;
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(_id, group._id) &&
                Objects.equals(username, group.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, username);
    }

    protected Group(Parcel in) {
        _id = in.readString();
        username = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(username);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @NonNull
    public String get_id() {
        return _id;
    }

    public String getUsername() {
        return username;
    }
}
