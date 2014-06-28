package kr.go.knpa.daon.io.model;

public class OfficerReponse {
    private String name;
    private String rank;
    private String role;
    private String phone;
    private String cellphone;
    private int id;
    private int department_id;
    private String created_at;
    private String updated_at;

    public String getName() {
        return name;
    }

    public String getRank() {
        return rank;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public int getId() {
        return id;
    }

    public int getDepartmentId() {
        return department_id;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

}
