package com.example.root.penulisanilmiah;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UbidotsClient {
    private UbiListener listener;
    protected interface  UbiListener {
        public void onDataReady(List<Value> result);
    }
    protected static class Value {
        float value;
        long timestamp;
    }
    public UbiListener getListener() {
        return listener;
    }
    public void setListener(UbiListener listener) {
        this.listener = listener;
    }
    public void handleUbidots(String varID, String apiKey, final UbiListener listener){
        final List<Value> result = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().addHeader("X-Auth-Token", apiKey)
                .url("http://things.ubidots.com/api/v1.6/variables/" + varID + "/values")
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                try {
                    JSONObject jObj = new JSONObject(body);
                    JSONArray jRes = jObj.getJSONArray("results");
                    for (int i = 0; i < jRes.length(); i++){
                        JSONObject obj = jRes.getJSONObject(i);
                        Value val = new Value();
                        val.timestamp = obj.getLong("timestamp");
                        val.value = (float) obj.getDouble("value");
                        result.add(val);
                    }
                    listener.onDataReady(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
