package com.bugu.things.storage.bean;

public class MqttMessage {
    private long time;
    private int type;
    private String content;
    private long id;
    private String desc;
    private String title;
    private String name;

    public MqttMessage() {
    }

    public MqttMessage(long time, int type, String content, long id) {
        this.time = time;
        this.type = type;
        this.content = content;
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MqttMessage(long time, int type, String content, long id, String desc, String title, String name) {
        this.time = time;
        this.type = type;
        this.content = content;
        this.id = id;
        this.desc = desc;
        this.title = title;
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MqttMessage{" +
                "time=" + time +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", id=" + id +
                ", desc='" + desc + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
