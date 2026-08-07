package com.project.baseproject.navigation

import android.os.Bundle
import com.project.baseproject.R
import com.project.core.navigationComponent.BaseNavigatorImpl
import com.rikkeisoft.awesome.ConversationNavigation
import com.rikkeisoft.awesome.FriendNavigation
import com.rikkeisoft.awesome.ProfileNavigation
import com.rikkeisoft.awesome.chat.ChatNavigation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class HomeNavigatorImpl @Inject constructor() : BaseNavigatorImpl(), ConversationNavigation,
    HomeNavigation, FriendNavigation, ProfileNavigation, ChatNavigation {
    override fun openListConversationToChat(bundle: Bundle?) {
        openScreen(R.id.action_listConversationFragment_to_chatFragment, bundle)

    }

    override fun back() {
        navigateUp()
    }

    override fun openFriendsToChat(bundle: Bundle?) {
        openScreen(R.id.action_friendsFragment_to_chatFragment, bundle)
    }

    override fun openProfileToEditProfile(bundle: Bundle?) {
        openScreen(R.id.action_profileFragment_to_editProfileFragment)
    }
}