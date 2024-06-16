package io.github.hmzi67.securezone.Modals;

public class AllMessagesModel {

    private String message;
    private String sentBy;

    public AllMessagesModel() {
        // empty constructor
    }

    public AllMessagesModel(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}
