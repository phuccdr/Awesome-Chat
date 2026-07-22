# Fix Gallery Panel Animation

The user reported that `binding.pbGallery.showWithAnimation()` does not make the view visible. Based on the code analysis, there are two main issues:
1. The `showWithAnimation` extension function uses `view.height` to determine the starting `translationY`. If the view is `GONE`, its height is `0`, causing the animation to start and end at the same position (effectively only a fade-in).
2. In `ChatFragment.kt`, the `isPanelVisible` state is being applied to `pbGallery` (a ProgressBar) instead of `galleryPanel` (the actual container for the gallery).

## Proposed Changes

### [Animation Utility]

#### [MODIFY] [animationExt.kt](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/java/com/rikkeisoft/awesome/ui/chat/animationExt.kt)
- Improve `showWithAnimation` to handle views that are initially `GONE` or have `0` height by attempting to measure them before starting the animation.

### [Conversation Feature]

#### [MODIFY] [ChatFragment.kt](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/java/com/rikkeisoft/awesome/ui/chat/ChatFragment.kt)
- Update the `isPanelVisible` observer to animate `binding.galleryPanel` instead of `binding.pbGallery`.

#### [MODIFY] [fragment_chat.xml](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/res/layout/fragment_chat.xml)
- Set `android:visibility="gone"` for `galleryPanel` by default so that the animation has a clear start state.

## Verification Plan

### Automated Tests
- N/A (UI Animation logic)

### Manual Verification
- Deploy the app to a device/emulator.
- Navigate to the Chat screen.
- Click the "Add Image" button (plus icon).
- Verify that the gallery panel slides up from the bottom.
- Click the "Add Image" button again (or toggle it) and verify that it slides down.
