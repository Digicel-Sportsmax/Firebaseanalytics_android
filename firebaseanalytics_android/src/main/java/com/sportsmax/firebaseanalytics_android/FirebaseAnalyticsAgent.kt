package com.sportsmax.firebaseanalytics_android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.applicaster.analytics.BaseAnalyticsAgent
import com.applicaster.util.APLogger
import com.applicaster.util.StringUtil
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*

class FirebaseAnalyticsAgent : BaseAnalyticsAgent() {

    private val TAG: String = FirebaseAnalyticsAgent::class.java.simpleName

    /**
     * This variables are created for Google Analytics purposes.
     * You can delete all this variables when you doing your plugin.
     */
    // region vars
    @Transient
    private val MOBILE_APP_ACCOUNT_ID_IDENTIFIER = "mobile_app_account_id"
    private val ANONYMIZE_USER_IP_IDENTIFIER = "anonymize_user_ip"
    private val SCREEN_VIEWS_IDENTIFIER = "screen_views"
    private val DO_NOT_SET_CLIENT_ID = "do_not_set_client_id"
    //private var mobileAppAccountId: String? = null
    //private var anonymizeUserIp = false
    //private var screenViews = false
    //private var shouldSetClientId = false
    private var firebaseAnalytics: FirebaseAnalytics? = null

    // custom events
    private val PLAY_EVENT = "Play video"
    private val PAUSE_EVENT = "Pause video"
    private val STOP_EVENT = "Stop video"

    /**
     * Initialization of your Analytics provider.
     * @param context
     */
    override fun initializeAnalyticsAgent(context: Context?) {
        super.initializeAnalyticsAgent(context)
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
            firebaseAnalytics?.logEvent(it, Bundle())
        }
    }

    /**
     * Log event with extra data
     * @param eventName name of the event logged
     * @param params extra data
     */
    override fun logEvent(eventName: String?, params: TreeMap<String, String>?) {
        super.logEvent(eventName, params)
        Log.wtf("** eventName", "is " + eventName)
        val label: String = params?.let { it ->
            getLabel(it)
        } ?: ""
        Log.wtf("** params", "is " + params)
        val bundle = Bundle()
        bundle.putString("params", label)
        eventName?.let { it ->
            firebaseAnalytics?.logEvent(it,bundle)
        }
    }

    private fun getLabel(map: TreeMap<String, String>): String? {
        val notAvailableString = "N/A"
        // Build the labels param.
        var labelsString: String? = null
        if (map != null) {
            val labels = StringBuilder()
            for (key in map.keys) {
                var value = map[key]
                if (StringUtil.isEmpty(value)) {
                    value = notAvailableString
                }
                val label = String.format("%s=%s;", key, value)
                labels.append(label)
            }
            if (labels.length > 0) {
                // If it's not empty, we need to remove the last ';'
                labels.setLength(labels.length - 1)
            }
            labelsString = labels.toString()
        }
        return labelsString
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
        logEvent("Screen Visit."+screenView)
    }
}