package com.alexxpasta.nativeadsinrecyclerview;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alexxpasta on 2017/4/15.
 */

public class FakeTimelineApi {
    public static List<PostItem> fetchTimeline() {
        return Arrays.asList(
            new PostItem("Alan", "Morning~"),
            new PostItem("Alex", "Awesome!!!"),
            new PostItem("Anna", "@@"),
            new PostItem("Ariel", "234 Say I love you"),
            new PostItem("Billy", "Hello everybody :)"),
            new PostItem("Catherine", "Ooooops!"),
            new PostItem("Claire", "What an amazing night!!"),
            new PostItem("Do Re Mi", "Mi Fa So"),
            new PostItem("Elvis", "Good article"),
            new PostItem("Frank", ":o"),
            new PostItem("YOU-CHING", "Timing is everything")
        );
    }
}
