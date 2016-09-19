package com.yaoyumeng.v2ex2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.yaoyumeng.v2ex2.utils.ContentUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class TopicModel extends V2EXModel implements Parcelable{
    private static final long serialVersionUID = 2015050105L;

    public int id;
    public String title;
    public String url;
    public String content;
    public String contentRendered;
    public int replies;
    public MemberModel member;
    public NodeModel node;
    public long created;
    public long lastModified;
    public long lastTouched;

    public void parse(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        title = jsonObject.getString("title");
        url = jsonObject.getString("url");
        content = jsonObject.getString("content");
        contentRendered = ContentUtils.formatContent(jsonObject.getString("content_rendered"));
        replies = jsonObject.getInt("replies");
        member = new MemberModel();
        member.parse(jsonObject.getJSONObject("member"));
        node = new NodeModel();
        node.parse(jsonObject.getJSONObject("node"));
        created = jsonObject.getLong("created");
        lastModified = jsonObject.getLong("last_modified");
        lastTouched = jsonObject.getLong("last_touched");
    }

    public TopicModel(){}

    private TopicModel(Parcel in){
        int[] ints= new int[2];
        in.readIntArray(ints);
        id = ints[0];
        replies = ints[1];
        String[] strings = new String[4];
        in.readStringArray(strings);
        title = strings[0];
        url = strings[1];
        content = strings[2];
        contentRendered = strings[3];
        long[] longs = new long[3];
        in.readLongArray(longs);
        created = longs[0];
        lastModified = longs[1];
        lastTouched = longs[2];
        member = (MemberModel) in.readValue(MemberModel.class.getClassLoader());
        node = (NodeModel) in.readValue(NodeModel.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(new int[]{
                id,
                replies
        });
        dest.writeStringArray(new String[]{
                title,
                url,
                content,
                contentRendered
        });
        dest.writeLongArray(new long[]{
                created,
                lastModified,
                lastTouched
        });
        dest.writeValue(member);
        dest.writeValue(node);
    }

    public static final Creator<TopicModel> CREATOR = new Creator<TopicModel>() {
        @Override
        public TopicModel createFromParcel(Parcel source) {
            return new TopicModel(source);
        }

        @Override
        public TopicModel[] newArray(int size) {
            return new TopicModel[size];
        }
    };
}
