package by.babanin.service;

public enum RecordType {
    MJPEG("avi"), H264("mp4");

    private String format;

    RecordType(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
