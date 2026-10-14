package com.app.mediaplayer.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u000eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u000eX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/app/mediaplayer/utils/Constants;", "", "()V", "BEATS_FOLDER_NAME", "", "DATABASE_NAME", "KEY_AUTO_PLAY", "KEY_BACKGROUND_PLAY", "KEY_DARK_THEME", "KEY_HARDWARE_ACCEL", "KEY_HIDE_NOTIFICATION", "KEY_LOCK_SCREEN_CONTROLS", "KEY_PRIVATE_FOLDER", "MEDIA_TYPE_AUDIO", "", "MEDIA_TYPE_BEATS", "MEDIA_TYPE_VIDEO", "NOTIFICATION_CHANNEL_ID", "NOTIFICATION_ID", "PREF_NAME", "PRIVACY_FOLDER_NAME", "REQUEST_CODE_AUDIO", "REQUEST_CODE_STORAGE", "REQUEST_CODE_VIDEO", "app_debug"})
public final class Constants {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PRIVACY_FOLDER_NAME = "MaxAI_Private";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BEATS_FOLDER_NAME = "Beats";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DATABASE_NAME = "max_ai_player_db";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_NAME = "max_ai_player_prefs";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_DARK_THEME = "dark_theme";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_BACKGROUND_PLAY = "background_play";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_HARDWARE_ACCEL = "hardware_accel";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_AUTO_PLAY = "auto_play";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_PRIVATE_FOLDER = "private_folder";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_HIDE_NOTIFICATION = "hide_notification";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_LOCK_SCREEN_CONTROLS = "lock_screen_controls";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String NOTIFICATION_CHANNEL_ID = "max_ai_player_channel";
    public static final int NOTIFICATION_ID = 1001;
    public static final int REQUEST_CODE_STORAGE = 100;
    public static final int REQUEST_CODE_AUDIO = 101;
    public static final int REQUEST_CODE_VIDEO = 102;
    public static final int MEDIA_TYPE_AUDIO = 0;
    public static final int MEDIA_TYPE_VIDEO = 1;
    public static final int MEDIA_TYPE_BEATS = 2;
    @org.jetbrains.annotations.NotNull()
    public static final com.app.mediaplayer.utils.Constants INSTANCE = null;
    
    private Constants() {
        super();
    }
}