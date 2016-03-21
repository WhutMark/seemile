package com.seemile.launcher.domain;

/**
 * Created by whuthm on 2016/3/10.
 * VO
 */
public final class Download {

    private static final Download PENDING = new Download(Status.PENDING);
    private static final Download SUCCESSFUL = new Download(Status.SUCCESSFUL, 100);
    private static final Download FAILED = new Download(Status.FAILED);

    private final int progress;
    private final Status status;

    private Download(Status status) {
        this(status, 0);
    }

    private Download(Status status, int progress) {
        this.status = status;
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public Status getStatus() {
        return status;
    }

    public static Download PENDING() {
        return PENDING;
    }

    public static Download RUNNING(int progress) {
        return new Download(Status.RUNNING, progress);
    }

    public static Download SUCCESSFUL() {
        return SUCCESSFUL;
    }

    public static Download FAILED() {
        return FAILED;
    }


    public enum Status {
        PENDING,
        RUNNING,
        SUCCESSFUL,
        FAILED
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Download) {
            Download other = (Download) o;
            return progress == other.progress && status == other.status;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return progress + status.hashCode();
    }

    @Override
    public String toString() {
        return "Download : Status = " + status + ", progress = " + progress;
    }
}
