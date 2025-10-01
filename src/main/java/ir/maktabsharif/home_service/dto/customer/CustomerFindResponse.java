package ir.maktabsharif.home_service.dto.customer;

public class CustomerFindResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private Boolean isEmailVerified;

    public CustomerFindResponse() {
    }

    public CustomerFindResponse(Integer id, String firstName, String lastName, Boolean isEmailVerified) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isEmailVerified = isEmailVerified;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
}
