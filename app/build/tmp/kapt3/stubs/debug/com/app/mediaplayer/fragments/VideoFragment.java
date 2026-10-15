package com.app.mediaplayer.fragments;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\u0018\u0000 \'2\u00020\u0001:\u0001\'B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0016\u001a\u00020\u0017H\u0002J\u0010\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\b\u0010\u001b\u001a\u00020\u0017H\u0002J&\u0010\u001c\u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010 2\b\u0010!\u001a\u0004\u0018\u00010\"H\u0016J\u000e\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00150$H\u0002J\b\u0010%\u001a\u00020\u0017H\u0002J\b\u0010&\u001a\u00020\u0017H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0010\u0012\f\u0012\n \f*\u0004\u0018\u00010\u000b0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/app/mediaplayer/fragments/VideoFragment;", "Landroidx/fragment/app/Fragment;", "()V", "btnGrantPermission", "Landroid/widget/Button;", "emptyStateContainer", "Landroid/widget/LinearLayout;", "emptyStateText", "Landroid/widget/TextView;", "permissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "kotlin.jvm.PlatformType", "progressBar", "Landroid/widget/ProgressBar;", "rvVideos", "Landroidx/recyclerview/widget/RecyclerView;", "videoAdapter", "Lcom/app/mediaplayer/adapters/VideoAdapter;", "videoList", "", "Lcom/app/mediaplayer/models/MediaItem;", "checkAndRequestPermission", "", "initViews", "view", "Landroid/view/View;", "loadVideos", "onCreateView", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "scanLocalVideos", "", "setupRecyclerView", "showPermissionDeniedState", "Companion", "app_debug"})
public final class VideoFragment extends androidx.fragment.app.Fragment {
    private androidx.recyclerview.widget.RecyclerView rvVideos;
    private android.widget.ProgressBar progressBar;
    private android.widget.LinearLayout emptyStateContainer;
    private android.widget.TextView emptyStateText;
    private android.widget.Button btnGrantPermission;
    @org.jetbrains.annotations.Nullable()
    private com.app.mediaplayer.adapters.VideoAdapter videoAdapter;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.app.mediaplayer.models.MediaItem> videoList = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> permissionLauncher = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.app.mediaplayer.fragments.VideoFragment.Companion Companion = null;
    
    public VideoFragment() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    private final void initViews(android.view.View view) {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void checkAndRequestPermission() {
    }
    
    private final void loadVideos() {
    }
    
    private final java.util.List<com.app.mediaplayer.models.MediaItem> scanLocalVideos() {
        return null;
    }
    
    private final void showPermissionDeniedState() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/app/mediaplayer/fragments/VideoFragment$Companion;", "", "()V", "newInstance", "Lcom/app/mediaplayer/fragments/VideoFragment;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.mediaplayer.fragments.VideoFragment newInstance() {
            return null;
        }
    }
}