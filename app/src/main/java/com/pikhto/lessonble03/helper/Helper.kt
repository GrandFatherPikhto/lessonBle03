package com.pikhto.lessonble03.helper

import android.app.Activity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment

fun linkMenu(menuHost: MenuHost, link: Boolean, menuProvider: MenuProvider) {
    if (link) {
        menuHost.addMenuProvider(menuProvider)
    } else {
        menuHost.removeMenuProvider(menuProvider)
    }
}

fun Activity.linkMenu(link: Boolean, menuProvider: MenuProvider)
        = linkMenu(this as MenuHost, link, menuProvider)

fun Fragment.linkMenu(link: Boolean, menuProvider: MenuProvider)
        = linkMenu(requireActivity() as MenuHost, link, menuProvider)