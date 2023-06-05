package com.emirari.linkylinkk;

import java.security.Timestamp;
import java.util.Date;

public class Post {
    public String userMail;
    public String title;
    public String link;
    public String tag;
    public String strTime;

    public Post(String userMail, String title, String link, String tag, String strTime) {
        this.userMail = userMail;
        this.title = title;
        this.link = link;
        this.tag = tag;
        this.strTime = strTime;
    }

    public String getLink() {
        return link;
    }

    public String getTag() { return tag; }
}
