package xstar.top.myphotos

import android.app.Application

/**
 * @author: xstar
 * @since: 2017-09-18.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Const.init(this)
    }
}