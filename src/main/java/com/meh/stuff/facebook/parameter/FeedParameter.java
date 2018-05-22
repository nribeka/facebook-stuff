package com.meh.stuff.facebook.parameter;

import java.util.Date;

public class FeedParameter {
    private int keepCount;
    private Date keepSince;
    private boolean reviewing;
    private boolean autoDelete;

    public FeedParameter(final int keepCount, final Date keepSince,
                         final boolean reviewing, final boolean autoDelete) {
        this.keepCount = keepCount;
        this.keepSince = keepSince;
        this.reviewing = reviewing;
        this.autoDelete = autoDelete;
    }

    public FeedParameter(final int keepCount) {
        this.keepCount = keepCount;
        this.reviewing = false;
        this.autoDelete = true;
    }

    public FeedParameter(final Date keepSince) {
        this.keepSince = keepSince;
        this.reviewing = false;
        this.autoDelete = true;
    }

    public FeedParameter() {
        // Default to keep only last entry in the feed;
        this.keepCount = 1;
        this.reviewing = false;
        this.autoDelete = true;
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

    public boolean isReviewing() {
        return reviewing;
    }

    public void setReviewing(boolean reviewing) {
        this.reviewing = reviewing;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }
}
