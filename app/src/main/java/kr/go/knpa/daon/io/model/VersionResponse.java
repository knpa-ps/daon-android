package kr.go.knpa.daon.io.model;

public class VersionResponse {
    int code;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return download_url;
    }

    String name;
    String download_url;
}
