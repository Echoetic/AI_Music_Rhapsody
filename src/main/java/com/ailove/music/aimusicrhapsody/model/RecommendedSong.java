package com.ailove.music.aimusicrhapsody.model;

public class RecommendedSong {
    private String title;
    private String reason;
    private int score; // 1~5 推荐指数
    private String genreMatch;    // 流派匹配说明
    private String popularity;    // 知名度说明
    private String lyricTheme;    // 歌词主题分析
    private String emotion;       // 情绪风格匹配

    // 无参构造方法（必须用于 JSON 反序列化）
    public RecommendedSong() {
    }

    // 全参构造方法
    public RecommendedSong(String title, String reason, int score,
                           String genreMatch, String popularity,
                           String lyricTheme, String emotion) {
        this.title = title;
        this.reason = reason;
        this.score = score;
        this.genreMatch = genreMatch;
        this.popularity = popularity;
        this.lyricTheme = lyricTheme;
        this.emotion = emotion;
    }

    // Getter 和 Setter 方法
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getGenreMatch() {
        return genreMatch;
    }

    public void setGenreMatch(String genreMatch) {
        this.genreMatch = genreMatch;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getLyricTheme() {
        return lyricTheme;
    }

    public void setLyricTheme(String lyricTheme) {
        this.lyricTheme = lyricTheme;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    // 可选的调试输出
    @Override
    public String toString() {
        return "RecommendedSong{" +
                "title='" + title + '\'' +
                ", reason='" + reason + '\'' +
                ", score=" + score +
                ", genreMatch='" + genreMatch + '\'' +
                ", popularity='" + popularity + '\'' +
                ", lyricTheme='" + lyricTheme + '\'' +
                ", emotion='" + emotion + '\'' +
                '}';
    }
}
