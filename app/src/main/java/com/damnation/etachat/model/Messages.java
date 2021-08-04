package com.damnation.etachat.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Objects;

@Entity
public class Messages implements Parcelable {

    @PrimaryKey
    @NonNull
    private String _id;
    private String from;
    private String to;
    private String date;
    private String body;

    public Messages(@NonNull String _id, String from, String to, String date, String body) {
        this._id = _id;
        this.from = from;
        this.to = to;
        this.date = date;
        this.body = body;
    }

    protected Messages(Parcel in) {
        _id = in.readString();
        from = in.readString();
        to = in.readString();
        date = in.readString();
        body = in.readString();
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

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
