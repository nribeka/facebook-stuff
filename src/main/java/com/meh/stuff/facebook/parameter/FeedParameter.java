package com.meh.stuff.facebook.parameter;

import java.util.Date;

public class FeedParameter {
    private int keepCount;
    private Date keepSince;

    public FeedParameter(final int keepCount, final Date keepSince) {
        this.keepCount = keepCount;
        this.keepSince = keepSince;
    }

    public FeedParameter(final int keepCount) {
        this.keepCount = keepCount;
    }

    public FeedParameter(final Date keepSince) {
        this.keepSince = keepSince;
    }

    public FeedParameter() {
        // Default to keep only last entry in the feed;
        this.keepCount = 1;
    }

    public int getKeepCount() {
        return keepCount;
    }

    public void setKeepCount(int keepCount) {
        this.keepCount = keepCount;
    }

    public Date getKeepSince() {
        return keepSince;
    }

    public void setKeepSince(Date keepSince) {
        this.keepSince = keepSince;
    }
}
