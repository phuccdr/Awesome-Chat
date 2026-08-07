package com.project.baseproject.di

import com.project.baseproject.navigation.AppNavigation
import com.project.baseproject.navigation.AppNavigatorImpl
import com.project.baseproject.navigation.HomeNavigation
import com.project.baseproject.navigation.HomeNavigatorImpl
import com.project.core.navigationComponent.BaseNavigator
import com.project.setting.DemoNavigation
import com.rikkeisoft.awesome.AuthNavigation
import com.rikkeisoft.awesome.ConversationNavigation
import com.rikkeisoft.awesome.FriendNavigation
import com.rikkeisoft.awesome.ProfileNavigation
import com.rikkeisoft.awesome.chat.ChatNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class NavigationModule {
    @Binds
    @ActivityScoped
    abstract fun bindBaseNavigation(navigation: AppNavigatorImpl): BaseNavigator

    @Binds
    @ActivityScoped
    abstract fun bindAppNavigation(navigation: AppNavigatorImpl): AppNavigation

    @Binds
    @ActivityScoped
    abstract fun bindDemoNavigation(navigation: AppNavigatorImpl): DemoNavigation

    @Binds
    @ActivityScoped
    abstract fun bindAuthNavigation(navigation: AppNavigatorImpl): AuthNavigation

    @Binds
    @ActivityScoped
    abstract fun bindHomeAppNavigation(navigation: HomeNavigatorImpl): HomeNavigation

    @Binds
    @ActivityScoped
    abstract fun bindConversationNavigation(navigation: HomeNavigatorImpl): ConversationNavigation

    @Binds
    @ActivityScoped
    abstract fun bindFriendsNavigation(navigation: HomeNavigatorImpl): FriendNavigation

    @Binds
    @ActivityScoped
    abstract fun bindProfileNavigation(navigation: HomeNavigatorImpl): ProfileNavigation

    @Binds
    @ActivityScoped
    abstract fun bindChatNavigation(navigation: HomeNavigatorImpl): ChatNavigation

}