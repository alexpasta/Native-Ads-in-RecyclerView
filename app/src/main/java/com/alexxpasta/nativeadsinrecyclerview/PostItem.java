package com.alexxpasta.nativeadsinrecyclerview;

/**
 * Created by alexxpasta on 2017/4/15.
 */

public class PostItem {
    public String name;
    public String message;
    public int avatarResId;
    public int imageResId;

    public PostItem(String name, String message, int avatarResId, int imageResId) {
        this.name = name;
        this.message = message;
        this.avatarResId = avatarResId;
        this.imageResId = imageResId;
    }
}
