package com.yaoyumeng.v2ex2.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MemberModel extends V2EXModel implements Parcelable {
    public static final Creator<MemberModel> CREATOR = new Creator<MemberModel>() {
        @Override
        public MemberModel createFromParcel(Parcel source) {
            return new MemberModel(source);
        }

        @Override
        public MemberModel[] newArray(int size) {
            return new MemberModel[size];
        }
    };
    private static final long serialVersionUID = 2015050102L;
    public int id;
    public String username;
    public String tagline;
    public String avatar;      //73*73
    public String website;
    public String github;
    public String twitter;
    public String location;

    public MemberModel() {
    }

    private MemberModel(Parcel in) {
        id = in.readInt();
        String[] strings = new String[7];
        in.readStringArray(strings);
        username = strings[0];
        tagline = strings[1];
        avatar = strings[2];
        website = strings[3];
        github = strings[4];
        twitter = strings[5];
        location = strings[6];
    }

    public void parse(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        username = jsonObject.getString("username");
        tagline = jsonObject.getString("tagline");
        website = jsonObject.optString("website");
        github = jsonObject.optString("github");
        twitter = jsonObject.optString("twitter");
        location = jsonObject.optString("location");
        if (!website.isEmpty()
                && !website.startsWith("http://")
                && !website.startsWith("https://"))
            website = "http://" + website;
        avatar = jsonObject.getString("avatar_large");
        if (avatar.startsWith("//")) {
            avatar = "http:" + avatar;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeStringArray(new String[]{
                username,
                tagline,
                avatar,
                website,
                github,
                twitter,
                location
        });
    }
}
