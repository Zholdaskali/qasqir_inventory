package kz.qasqir.qasqirinventory.api.model.request;

public class RegisterRequest {
    private String userName;
    private String password;
    private String email;
    private String userNumber;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String number) {
        this.userNumber = number;
    }
}

