package com.yaoyumeng.v2ex2.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class NodeModel extends V2EXModel implements Parcelable{
    private static final long serialVersionUID = 2015050103L;

    public int id;
    public String name;
    public String title;
    public String titleAlternative;
    public String url;
    public int topics;

    public String header;
    public String footer;

    public boolean isCollected = false;

    public void parse(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        name = jsonObject.getString("name");
        title = jsonObject.getString("title");
        url = jsonObject.getString("url");
        topics = jsonObject.getInt("topics");
        if(!jsonObject.optString("title_alternative").equals("null")){
            titleAlternative = jsonObject.optString("title_alternative");
        }
        if(!jsonObject.optString("header").equals("null")){
            header = jsonObject.optString("header");
        }
        if(!jsonObject.optString("footer").equals("null")){
            footer = jsonObject.optString("footer");
        }
    }

    public NodeModel(){}

    private NodeModel(Parcel in){
        int[] ints = new int[2];
        in.readIntArray(ints);
        id = ints[0];
        topics = ints[1];
        String[] strings = new String[6];
        in.readStringArray(strings);
        name = strings[0];
        title = strings[1];
        url = strings[2];
        if((titleAlternative = strings[3]).equals("")){
            titleAlternative = null;
        }
        if((header = strings[4]).equals("")){
            header = null;
        }
        if((footer = strings[5]).equals("")){
            footer = null;
        }

        boolean[] booleans = new boolean[1];
        in.readBooleanArray(booleans);
        isCollected = booleans[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(new int[]{
                id,
                topics
        });
        String[] strings = new String[6];
        strings[0] = name;
        strings[1] = title;
        strings[2] = url;
        if((strings[3] = titleAlternative) == null){
            strings[3] = "";
        }
        if((strings[4] = header) == null){
            strings[4] = "";
        }
        if((strings[5] = footer) == null){
            strings[5] = "";
        }
        dest.writeStringArray(strings);
        dest.writeBooleanArray(new boolean[]{ isCollected });
    }

    public static final Creator<NodeModel> CREATOR = new Creator<NodeModel>() {
        @Override
        public NodeModel createFromParcel(Parcel source) {
            return new NodeModel(source);
        }

        @Override
        public NodeModel[] newArray(int size) {
            return new NodeModel[size];
        }
    };

    public String toString(){
        return String.format("%s / %s", title, name);
    }
}
