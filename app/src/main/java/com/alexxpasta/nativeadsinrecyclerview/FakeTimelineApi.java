package com.alexxpasta.nativeadsinrecyclerview;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alexxpasta on 2017/4/15.
 */

public class FakeTimelineApi {
    public static List<PostItem> fetchTimeline() {
        return Arrays.asList(
            new PostItem("Alan", "Morning~", R.drawable.boy_avatar, R.drawable.pic0),
            new PostItem("Alex", "Awesome!!!", R.drawable.boy_avatar, R.drawable.pic1),
            new PostItem("Anna", "@@", R.drawable.girl_avatar, R.drawable.pic2),
            new PostItem("Ariel", "234 Say I love you", R.drawable.girl_avatar, R.drawable.pic3),
            new PostItem("Billy", "Hello everybody :)", R.drawable.boy_avatar, R.drawable.pic4),
            new PostItem("Catherine", "Ooooops!", R.drawable.girl_avatar, R.drawable.pic5),
            new PostItem("Claire", "What an amazing night!!", R.drawable.girl_avatar, R.drawable.pic6),
            new PostItem("Do Re Mi", "Mi Fa So", R.drawable.girl_avatar, R.drawable.pic7),
            new PostItem("Elvis", "Good article", R.drawable.boy_avatar, R.drawable.pic8),
            new PostItem("YOU-CHING", "Timing is everything", R.drawable.girl_avatar, R.drawable.pic9)
        );
    }
}
