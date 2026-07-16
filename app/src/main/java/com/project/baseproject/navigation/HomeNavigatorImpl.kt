package com.project.baseproject.navigation

import android.os.Bundle
import com.project.baseproject.R
import com.project.core.navigationComponent.BaseNavigatorImpl
import com.rikkeisoft.awesome.ConversationNavigation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class HomeNavigatorImpl @Inject constructor() : BaseNavigatorImpl(), ConversationNavigation,
    HomeNavigation {
    override fun openListConversationToChat(bundle: Bundle?) {
        openScreen(R.id.action_listConversationFragment_to_chatFragment, bundle)

    }

    override fun back() {
        navigateUp()
    }
}