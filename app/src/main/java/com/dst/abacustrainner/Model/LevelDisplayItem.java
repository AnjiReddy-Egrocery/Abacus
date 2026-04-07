package com.dst.abacustrainner.Model;

public class LevelDisplayItem {
    public static final int TYPE_TOPIC_HEADER = 0;
    public static final int TYPE_TOPIC = 1;
    public static final int TYPE_ASSIGNMENT_HEADER = 2;
    public static final int TYPE_ASSIGNMENT_TOPIC = 3;
    public static final int TYPE_EMPTY_MESSAGE = 4;

    private int type;
    private String title;

    public String getTopicId() {
        return topicId;
    }

    private String topicId;   // ✅ NEW FIELD

    public LevelDisplayItem(int type, String title) {

       this.type = type;
        this.title = title;
    }

    public LevelDisplayItem(int type, String title, String topicId) {

        this.type = type;
        this.title = title;
        this.topicId = topicId;
    }


    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
