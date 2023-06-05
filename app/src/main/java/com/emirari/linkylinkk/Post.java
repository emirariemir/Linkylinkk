package com.emirari.linkylinkk;

public class Post {
    public String userMail;
    public String title;
    public String link;

    public String tag;

    public Post(String userMail, String title, String link, String tag) {
        this.userMail = userMail;
        this.title = title;
        this.link = link;
        this.tag = tag;
    }

    public String getLink() {
        return link;
    }

    public String getTag() { return tag; }
}
