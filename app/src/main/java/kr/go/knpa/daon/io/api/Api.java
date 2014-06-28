package kr.go.knpa.daon.io.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import kr.go.knpa.daon.io.model.DepartmentResponse;
import kr.go.knpa.daon.io.model.OfficerReponse;

public class Api {

    private static final OkHttpClient sClient = new OkHttpClient();

    public List<OfficerReponse> officers() throws IOException {

        String url = "http://54.250.242.100/daon/public/api/officers";

        Request req = new Request.Builder().url(url).build();

        Response r = sClient.newCall(req).execute();

        if (!r.isSuccessful()) {
            throw new IOException("response is not successful. "+r);
        }

        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<OfficerReponse>>(){}.getType();
        return gson.fromJson(r.body().string(), collectionType);
    }

    public List<DepartmentResponse> departments() throws IOException {


        String url = "http://54.250.242.100/daon/public/api/departments";

        Request req = new Request.Builder().url(url).build();

        Response r = sClient.newCall(req).execute();

        if (!r.isSuccessful()) {
            throw new IOException("response is not successful. "+r);
        }

        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<DepartmentResponse>>(){}.getType();
        return gson.fromJson(r.body().string(), collectionType);
    }

    public void registerGCM(String regId) throws IOException {
        String url = "http://54.250.242.100/daon/public/api/register-gcm";

        RequestBody reqBody = new FormEncodingBuilder()
                .add("reg_id", regId)
                .build();

        Request req = new Request.Builder().url(url).post(reqBody).build();

        Response r = sClient.newCall(req).execute();
        if (!r.isSuccessful()) {
            throw new IOException("response is not successful. "+r);
        }
    }
}
