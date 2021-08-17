package com.damnation.etachat.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Messages implements Parcelable {

    @PrimaryKey
    @NonNull
    private final String _id;
    private final String from;
    private final String to;
    private final String date;
    private final String body;
    @Embedded(prefix = "user_")
    private final User user;

    public Messages(@NonNull String _id, String from, String to, String date, String body, User user) {
        this._id = _id;
        this.from = from;
        this.to = to;
        this.date = date;
        this.body = body;
        this.user = user;
    }

    protected Messages(Parcel in) {
        _id = in.readString();
        from = in.readString();
        to = in.readString();
        date = in.readString();
        body = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Messages> CREATOR = new Creator<Messages>() {
        @Override
        public Messages createFromParcel(Parcel in) {
            return new Messages(in);
        }

        @Override
        public Messages[] newArray(int size) {
            return new Messages[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(date);
        dest.writeString(body);
        dest.writeParcelable(user, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Messages messages = (Messages) o;
        return _id.equals(messages._id) && Objects.equals(from, messages.from) && Objects.equals(to, messages.to) && Objects.equals(date, messages.date) && Objects.equals(body, messages.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, from, to, date, body);
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
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
}
