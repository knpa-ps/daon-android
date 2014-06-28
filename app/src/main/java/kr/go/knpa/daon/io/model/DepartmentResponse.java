package kr.go.knpa.daon.io.model;

public class DepartmentResponse {
    private int id;
    private String name;
    private Integer parent_id;
    private String full_name;

    private String created_at;
    private String updated_at;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getParentId() {
        return parent_id;
    }

    public String getFullName() {
        return full_name;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }
}
