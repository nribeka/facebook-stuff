package com.meh.stuff.facebook.parameter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class FeedParameter {
    private int keepCount;
    private Date startFrom;
    private boolean reviewing;
    private Date keepSince;
    private boolean autoDelete;
    private boolean takeScreenshot;

    private boolean delayBetweenPost;
    private long delayBetweenPostInSecond;

    public FeedParameter() {
        // Default to keep only last entry in the feed;
        this.keepCount = -1;
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

    public boolean isTakeScreenshot() {
        return takeScreenshot;
    }

    public void setTakeScreenshot(boolean takeScreenshot) {
        this.takeScreenshot = takeScreenshot;
    }

    public boolean isDelayBetweenPost() {
        return delayBetweenPost;
    }

    public void setDelayBetweenPost(boolean delayBetweenPost) {
        this.delayBetweenPost = delayBetweenPost;
    }

    public long getDelayBetweenPostInSecond() {
        return delayBetweenPostInSecond;
    }

    public void setDelayBetweenPostInSecond(long delayBetweenPostInSecond) {
        this.delayBetweenPostInSecond = delayBetweenPostInSecond;
    }

    public void loadFromProperties(Properties properties) {
        setReviewing("true".equalsIgnoreCase(properties.getProperty("mode.reviewing")));
        setTakeScreenshot("true".equalsIgnoreCase(properties.getProperty("takeScreenshot")));
        setDelayBetweenPost("true".equalsIgnoreCase(properties.getProperty("delay.betweenPost")));

        String delayBetweenPostInSecond = properties.getProperty("delay.betweenPostInSecond");
        if (delayBetweenPostInSecond != null) {
            setDelayBetweenPostInSecond(Long.parseLong(delayBetweenPostInSecond));
        }

        setAutoDelete("true".equalsIgnoreCase(properties.getProperty("autoDelete")));

        String keepSince = properties.getProperty("keep.since");
        if (keepSince != null) {
            setKeepSince(parseDate(keepSince));
        }

        String keepCount = properties.getProperty("keep.count");
        if (keepCount != null) {
            setKeepCount(Integer.parseInt(keepCount));
        }

        String startFrom = properties.getProperty("startFrom");
        if (startFrom != null) {
            setStartFrom(parseDate(startFrom));
        }
    }

    public Date getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(Date startFrom) {
        this.startFrom = startFrom;
    }

    private Date parseDate(String stringDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return simpleDateFormat.parse(stringDate);
        } catch (Exception e) {
            // silently ignore the date format error.
        }
        return null;
    }
}
