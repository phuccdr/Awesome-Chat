# Implementation Plan - Gallery Image Grid Picker

This plan outlines the implementation of an inline gallery image picker in the chat screen, allowing users to select multiple images from their device and send them.

## User Review Required

> [!IMPORTANT]
> - **Permission Handling**: We will use the existing `:libraries:permission` helper. If permission is denied, a Snackbar will be shown as per the guide.
> - **UI Placement**: The gallery grid will appear below the message input row, pushing it up or appearing as an overlay depending on the layout constraints. Based on `fragment_chat.xml`, it will be inserted between `rvMessages` and `layout_input` or at the bottom.
> - **Keyboard Interaction**: The panel will be hidden when the keyboard is shown, and vice versa.

## Proposed Changes

### [Component] :features:conversation

#### [NEW] [GalleryImage.kt](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/java/com/rikkeisoft/awesome/model/GalleryImage.kt)
Data model for gallery images.

#### [NEW] [GalleryRepository.kt](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/java/com/rikkeisoft/awesome/repository/GalleryRepository.kt)
Handles querying `MediaStore.Images` for device photos.

#### [MODIFY] [ChatViewModel.kt](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/java/com/rikkeisoft/awesome/ui/chat/ChatViewModel.kt)
Add state for gallery images, selected URIs, and panel visibility. Implement selection logic and panel toggle.

#### [NEW] [GalleryAdapter.kt](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/java/com/rikkeisoft/awesome/adapter/gallery/GalleryAdapter.kt)
Adapter for the 3-column image grid. Includes selection badges and overlays.

#### [NEW] [item_gallery_image.xml](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/res/layout/item_gallery_image.xml)
Layout for individual gallery items.

#### [NEW] [bg_selection_badge.xml](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/res/drawable/bg_selection_badge.xml)
Circular background for the selection sequence number.

#### [MODIFY] [fragment_chat.xml](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/res/layout/fragment_chat.xml)
Add the gallery panel container below the input row.

#### [MODIFY] [ChatFragment.kt](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/java/com/rikkeisoft/awesome/ui/chat/ChatFragment.kt)
Initialize the gallery adapter, observe ViewModel state, handle permissions, and manage keyboard/panel visibility transitions.

### [Component] :libraries:core

#### [MODIFY] [strings.xml](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/libraries/core/src/main/res/values/strings.xml)
Add `gallery_permission_needed` string.

## Verification Plan

### Automated Tests
- Unit test `ChatViewModel` for selection logic:
  - Toggling an image adds it to the list.
  - Toggling an already selected image removes it.
  - Selection order is preserved (1-based index).
  - Maximum selection limit (e.g., 10) is enforced.

### Manual Verification
1. Open a chat conversation.
2. Tap the image icon.
3. Verify permission request (allow/deny).
4. Verify the gallery panel appears below the message box.
5. Select multiple images and verify the sequence numbers (1, 2, 3...).
6. Deselect a middle image and verify others re-number correctly.
7. Verify the "Send" button appears only when at least one image is selected.
8. Verify the panel closes when tapping the "Send" button (or the confirm action).
9. Verify rotation preserves the selection state.
10. Verify keyboard hides when panel opens and vice versa.
