package io.github.hmzi67.securezone.Modals;

public class FakeCallModel {
    private String callId;
    private String callImage;
    private String callName;
    private String callNumber;

    public FakeCallModel() {}

    public FakeCallModel(String callId, String callImage, String callName, String callNumber) {
        this.callId = callId;
        this.callImage = callImage;
        this.callName = callName;
        this.callNumber = callNumber;
    }


    public String getCallId() {return callId;}

    public void setCallId(String callId) {this.callId = callId;}

    public String getCallImage() {
        return callImage;
    }

    public String getCallName() {
        return callName;
    }

    public String getCallNumber() {
        return callNumber;
    }
}

