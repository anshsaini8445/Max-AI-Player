package com.app.mediaplayer;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u0000 %2\u00020\u0001:\u0001%B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0014J\b\u0010\u001e\u001a\u00020\u001bH\u0002J\b\u0010\u001f\u001a\u00020\u001bH\u0002J\u0018\u0010 \u001a\u00020\u001b2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0002R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\b\u001a\u0004\b\r\u0010\u000eR\u001b\u0010\u0010\u001a\u00020\u00118BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0014\u0010\b\u001a\u0004\b\u0012\u0010\u0013R\u001b\u0010\u0015\u001a\u00020\u00168BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0019\u0010\b\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006&"}, d2 = {"Lcom/app/mediaplayer/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "audioFragment", "Lcom/app/mediaplayer/fragments/AudioFragment;", "getAudioFragment", "()Lcom/app/mediaplayer/fragments/AudioFragment;", "audioFragment$delegate", "Lkotlin/Lazy;", "bottomNav", "Lcom/google/android/material/bottomnavigation/BottomNavigationView;", "meFragment", "Lcom/app/mediaplayer/fragments/MeFragment;", "getMeFragment", "()Lcom/app/mediaplayer/fragments/MeFragment;", "meFragment$delegate", "searchFragment", "Lcom/app/mediaplayer/fragments/SearchFragment;", "getSearchFragment", "()Lcom/app/mediaplayer/fragments/SearchFragment;", "searchFragment$delegate", "videoFragment", "Lcom/app/mediaplayer/fragments/VideoFragment;", "getVideoFragment", "()Lcom/app/mediaplayer/fragments/VideoFragment;", "videoFragment$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "setupBackPress", "setupBottomNav", "switchFragment", "fragment", "Landroidx/fragment/app/Fragment;", "tag", "", "Companion", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.google.android.material.bottomnavigation.BottomNavigationView bottomNav;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy videoFragment$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy audioFragment$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy searchFragment$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy meFragment$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG_VIDEO = "TAG_VIDEO";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG_AUDIO = "TAG_AUDIO";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG_SEARCH = "TAG_SEARCH";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG_ME = "TAG_ME";
    @org.jetbrains.annotations.NotNull()
    public static final com.app.mediaplayer.MainActivity.Companion Companion = null;
    
    public MainActivity() {
        super();
    }
    
    private final com.app.mediaplayer.fragments.VideoFragment getVideoFragment() {
        return null;
    }
    
    private final com.app.mediaplayer.fragments.AudioFragment getAudioFragment() {
        return null;
    }
    
    private final com.app.mediaplayer.fragments.SearchFragment getSearchFragment() {
        return null;
    }
    
    private final com.app.mediaplayer.fragments.MeFragment getMeFragment() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupBottomNav() {
    }
    
    private final void switchFragment(androidx.fragment.app.Fragment fragment, java.lang.String tag) {
    }
    
    private final void setupBackPress() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/app/mediaplayer/MainActivity$Companion;", "", "()V", "TAG_AUDIO", "", "TAG_ME", "TAG_SEARCH", "TAG_VIDEO", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}