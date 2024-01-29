package com.hauser.dochausersrv.model;

import java.util.Objects;

public class Analysis {

    private String senderAddress;
    private String receiverAddress;
    private String intent;
    private String filename;
    private String category_level1;
    private String category_level2;

    public Analysis() {
        // empty constructor
    };

    public Analysis(String senderAddress, String receiverAddress, String intent, String filename, String category_level1, String category_level2) {
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.intent = intent;
        this.filename = filename;
        this.category_level1 = category_level1;
        this.category_level2 = category_level2;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCategory_level1() {
        return category_level1;
    }

    public void setCategory_level1(String category_level1) {
        this.category_level1 = category_level1;
    }

    public String getCategory_level2() {
        return category_level2;
    }

    public void setCategory_level2(String category_level2) {
        this.category_level2 = category_level2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Analysis analysis = (Analysis) o;
        return Objects.equals(senderAddress, analysis.senderAddress) && Objects.equals(receiverAddress, analysis.receiverAddress) && Objects.equals(intent, analysis.intent) && Objects.equals(filename, analysis.filename) && Objects.equals(category_level1, analysis.category_level1) && Objects.equals(category_level2, analysis.category_level2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderAddress, receiverAddress, intent, filename, category_level1, category_level2);
    }

    @Override
    public String toString() {
        return "Analysis{" +
                "senderAddress='" + senderAddress + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", intent='" + intent + '\'' +
                ", filename='" + filename + '\'' +
                ", category_level1='" + category_level1 + '\'' +
                ", category_level2='" + category_level2 + '\'' +
                '}';
    }
}
