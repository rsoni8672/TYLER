package com.example.android.tyler;

import com.google.gson.JsonObject;

import org.json.JSONObject;

public class Audio {
JsonObject data;

    public Audio() {
    }

    public JsonObject getData() {
        return this.data;
    }

    public void setData(JsonObject jsonObject) {
        this.data = jsonObject;
    }
}
