package io.github.hmzi67.securezone.Modals;





public class Users {
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userProfileImg;
    private String userCountry;
    private String userDateOfBirth;
    private String userGender;
    private String userPurposeOfUse;

    public Users() {}

    public Users(String userName, String userEmail, String userPhone, String userProfileImg, String userCountry, String userDateOfBirth, String userGender, String userPurposeOfUse) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userProfileImg = userProfileImg;
        this.userCountry = userCountry;
        this.userDateOfBirth = userDateOfBirth;
        this.userGender = userGender;
        this.userPurposeOfUse = userPurposeOfUse;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserProfileImg() {
        return userProfileImg;
    }

    public void setUserProfileImg(String userProfileImg) {
        this.userProfileImg = userProfileImg;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserDateOfBirth() {
        return userDateOfBirth;
    }

    public void setUserDateOfBirth(String userDateOfBirth) {
        this.userDateOfBirth = userDateOfBirth;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserPurposeOfUse() {
        return userPurposeOfUse;
    }

    public void setUserPurposeOfUse(String userPurposeOfUse) {
        this.userPurposeOfUse = userPurposeOfUse;
    }
}