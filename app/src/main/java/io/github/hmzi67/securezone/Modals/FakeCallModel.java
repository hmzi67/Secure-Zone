package io.github.hmzi67.securezone.Modals;

public class FakeCallModel {
    private String callImage;
    private String callName;
    private String callNumber;

    public FakeCallModel() {}

    public FakeCallModel(String callImage, String callName, String callNumber) {
        this.callImage = callImage;
        this.callName = callName;
        this.callNumber = callNumber;
    }


    public String getCallImage() {
        return callImage;
    }

    public void setCallImage(String callImage) {
        this.callImage = callImage;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }
}

