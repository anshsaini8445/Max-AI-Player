package com.app.mediaplayer.services;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\t\u001a\u00020\nH\u0002J\b\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0014\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0016J\b\u0010\u0012\u001a\u00020\fH\u0016J\b\u0010\u0013\u001a\u00020\fH\u0016J\"\u0010\u0014\u001a\u00020\u00062\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u0006H\u0016J\b\u0010\u0017\u001a\u00020\fH\u0002J\u0010\u0010\u0018\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\u0004H\u0002J\b\u0010\u001a\u001a\u00020\fH\u0002J\b\u0010\u001b\u001a\u00020\fH\u0002J\b\u0010\u001c\u001a\u00020\fH\u0002J\b\u0010\u001d\u001a\u00020\fH\u0002J\b\u0010\u001e\u001a\u00020\fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/app/mediaplayer/services/MediaPlaybackService;", "Landroid/app/Service;", "()V", "CHANNEL_ID", "", "NOTIFICATION_ID", "", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "createNotification", "Landroid/app/Notification;", "createNotificationChannel", "", "handleIntent", "intent", "Landroid/content/Intent;", "onBind", "Landroid/os/IBinder;", "onCreate", "onDestroy", "onStartCommand", "flags", "startId", "pauseMedia", "playMedia", "uri", "playNext", "playPrevious", "showNotification", "stopMedia", "updateNotification", "Companion", "app_debug"})
public final class MediaPlaybackService extends android.app.Service {
    @org.jetbrains.annotations.Nullable()
    private androidx.media3.exoplayer.ExoPlayer player;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String CHANNEL_ID = "media_playback_channel";
    private final int NOTIFICATION_ID = 1;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_PLAY = "com.app.mediaplayer.ACTION_PLAY";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_PAUSE = "com.app.mediaplayer.ACTION_PAUSE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_STOP = "com.app.mediaplayer.ACTION_STOP";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_NEXT = "com.app.mediaplayer.ACTION_NEXT";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_PREV = "com.app.mediaplayer.ACTION_PREV";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_URI = "com.app.mediaplayer.EXTRA_URI";
    @org.jetbrains.annotations.NotNull()
    public static final com.app.mediaplayer.services.MediaPlaybackService.Companion Companion = null;
    
    public MediaPlaybackService() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    private final void handleIntent(android.content.Intent intent) {
    }
    
    private final void playMedia(java.lang.String uri) {
    }
    
    private final void pauseMedia() {
    }
    
    private final void stopMedia() {
    }
    
    private final void playNext() {
    }
    
    private final void playPrevious() {
    }
    
    private final void showNotification() {
    }
    
    private final void updateNotification() {
    }
    
    private final android.app.Notification createNotification() {
        return null;
    }
    
    private final void createNotificationChannel() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/app/mediaplayer/services/MediaPlaybackService$Companion;", "", "()V", "ACTION_NEXT", "", "ACTION_PAUSE", "ACTION_PLAY", "ACTION_PREV", "ACTION_STOP", "EXTRA_URI", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}