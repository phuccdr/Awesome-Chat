package com.rikkeisoft.awesome

import android.os.Bundle

interface ConversationNavigation {
    fun openListConversationToChat(bundle: Bundle? = null)

    fun back()
}