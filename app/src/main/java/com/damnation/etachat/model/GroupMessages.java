package com.damnation.etachat.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class GroupMessages implements Parcelable {

    @PrimaryKey
    @NonNull
    private final String _id;
    private final String from;
    private final String group;
    private final String date;
    private final String body;
    @Embedded(prefix = "user_")
    private final User user;

    protected GroupMessages(Parcel in) {
        _id = in.readString();
        from = in.readString();
        group = in.readString();
        date = in.readString();
        body = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public GroupMessages(@NonNull String _id, String from, String group, String date, String body, User user) {
        this._id = _id;
        this.from = from;
        this.group = group;
        this.date = date;
        this.body = body;
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(from);
        dest.writeString(group);
        dest.writeString(date);
        dest.writeString(body);
        dest.writeParcelable(user, 0);
    }

    public static final Creator<GroupMessages> CREATOR = new Creator<GroupMessages>() {
        @Override
        public GroupMessages createFromParcel(Parcel in) {
            return new GroupMessages(in);
        }

        @Override
        public GroupMessages[] newArray(int size) {
            return new GroupMessages[size];
        }
    };

    @NonNull
    public String get_id() {
        return _id;
    }

    public String getFrom() {
        return from;
    }

    public String getGroup() {
        return group;
    }

    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMessages that = (GroupMessages) o;
        return _id.equals(that._id) && Objects.equals(from, that.from) && Objects.equals(group, that.group) && Objects.equals(date, that.date) && Objects.equals(body, that.body) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, from, group, date, body, user);
    }
}
