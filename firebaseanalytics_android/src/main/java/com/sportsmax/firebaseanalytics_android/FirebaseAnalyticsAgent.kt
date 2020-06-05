package com.sportsmax.firebaseanalytics_android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.applicaster.analytics.BaseAnalyticsAgent
import com.applicaster.util.StringUtil
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders.EventBuilder
import com.google.android.gms.analytics.Tracker
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*

class FirebaseAnalyticsAgent : BaseAnalyticsAgent() {

    private val TAG: String = FirebaseAnalyticsAgent::class.java.simpleName

    private val MAX_SCREEN_NAME_LONG = 35
    private val MAX_PARAM_NAME_LONG = 40
    private val MAX_PARAM_VALUE_LONG = 100
    /**
     * This variables are created for Google Analytics purposes.
     * You can delete all this variables when you doing your plugin.
     */
    // region vars
    @Transient
    private var firebaseAnalytics: FirebaseAnalytics? = null

    // custom events
    private val PLAY_EVENT = "Play_video"
    private val PAUSE_EVENT = "Pause_video"
    private val STOP_EVENT = "Stop_video"

    /**
     * Initialization of your Analytics provider.
     * @param context
     */
    override fun initializeAnalyticsAgent(context: Context?) {
        super.initializeAnalyticsAgent(context)
        Log.wtf("** initializeAnalyticsAgent name", "is initializeAnalyticsAgent")
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
    }

    override fun setParams(params: Map<*, *>) {
        super.setParams(params)
        val stringMap = params as Map<String, String>
        PluginConfigurationHelper.setConfigurationMap(stringMap)
    }

    /**
     * Get the value of the key present in plugin_configurations.json
     * @param params parameters
     * @param key key of the parameter
     * @return correspondent value of the parameter with key `key`
     */
    private fun getValue(
        params: Map<*, *>,
        key: String
    ): String? {
        var returnVal = ""
        if (params[key] != null) {
            returnVal = params[key].toString()
        }
        return returnVal
    }

    /**
     * It is a good practice to make the parameters of the plugin available with this method
     * @return a hash map of the configuration of the plugin
     */
    override fun getConfiguration(): Map<String, String>? {
        return super.getConfiguration()
    }

    override fun startTrackingSession(context: Context?) {
        super.startTrackingSession(context)
        Log.wtf("** startTrackingSession name", "is startTrackingSession")
    }

    override fun stopTrackingSession(context: Context?) {
        super.stopTrackingSession(context)
        if (firebaseAnalytics != null) {
            firebaseAnalytics = null
        }
    }

    override fun analyticsSwitch(enabled: Boolean) {
        super.analyticsSwitch(enabled)
    }

    override fun logEvent(eventName: String?) {
        super.logEvent(eventName)
        eventName?.let { it ->
            firebaseAnalytics?.logEvent(it.alphaNumericOnly().cutToMaxLength(MAX_PARAM_NAME_LONG), null)
        }
    }

    /**
     * Log event with extra data
     * @param eventName name of the event logged
     * @param params extra data
     */
    override fun logEvent(eventName: String?, params: TreeMap<String, String>?) {
        super.logEvent(eventName, params)
        params?.let { it ->
            val bundle = Bundle()
            for ((key, value) in it.entries) {
                bundle.putString(
                    key.alphaNumericOnly().cutToMaxLength(MAX_PARAM_NAME_LONG),
                    value.alphaNumericOnly().cutToMaxLength(MAX_PARAM_VALUE_LONG)
                )
            }
            eventName?.let { it ->
                firebaseAnalytics?.logEvent(it.alphaNumericOnly().cutToMaxLength(MAX_PARAM_NAME_LONG), bundle)
            }
        }
    }

    override fun startTimedEvent(eventName: String?) {
        super.startTimedEvent(eventName)
        logEvent(eventName)
    }

    override fun startTimedEvent(eventName: String?, params: TreeMap<String, String>) {
        super.startTimedEvent(eventName, params)
        logEvent(eventName, params)
    }

    override fun endTimedEvent(eventName: String?) {
        super.endTimedEvent(eventName)
        logEvent(eventName)
    }

    override fun endTimedEvent(eventName: String?, params: TreeMap<String, String>) {
        super.endTimedEvent(eventName, params)
        logEvent(eventName, params)
    }

    override fun logPlayEvent(currentPosition: Long) {
        super.logPlayEvent(currentPosition)
        logEvent(PLAY_EVENT)
    }

    /**
     * Set the User Id (UUID) on the Analytics Agent
     *
     * @param userId
     */
    override fun sendUserID(userId: String?) {
        super.sendUserID(userId)
    }

    override fun logVideoEvent(eventName: String?, params: TreeMap<String, String>) {
        super.logVideoEvent(eventName, params)
        logEvent(eventName, params)
    }

    /**
     * Track a when player paused.
     *
     * @param currentPosition
     */
    override fun logPauseEvent(currentPosition: Long) {
        super.logPauseEvent(currentPosition)
        logEvent(PAUSE_EVENT)
    }

    /**
     * Track when player stop.
     *
     * @param currentPosition
     */
    override fun logStopEvent(currentPosition: Long) {
        super.logStopEvent(currentPosition)
        logEvent(STOP_EVENT)
    }

    override fun setScreenView(activity: Activity?, screenView: String) {
        super.setScreenView(activity, screenView)
        val screenName:String
        if (screenView.contains("ATOM Article", ignoreCase = true)){
            val title = screenView.replace("ATOM Article", "Article").trim()
            screenName =  title
        }else{
            screenName = screenView
        }
        Log.wtf("** Screen name", "is $screenName")
        activity?.let { it ->
            firebaseAnalytics?.setCurrentScreen(it, screenName.cutToMaxLength(MAX_SCREEN_NAME_LONG), null)
        }
    }
}

fun String.cutToMaxLength(maxLength: Int): String{
    return if (this.length > maxLength){
        this.substring(0, maxLength)
    }else{
        this
    }
}

fun String.alphaNumericOnly(): String{
    val regex = Regex("[^A-Za-z0-9 ]")
    return regex.replace(this, "").replace(" ","_")
}