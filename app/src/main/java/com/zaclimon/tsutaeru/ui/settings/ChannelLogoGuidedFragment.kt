package com.zaclimon.tsutaeru.ui.settings

import android.content.ComponentName
import android.content.Context
import android.media.tv.TvContract
import android.os.AsyncTask
import android.os.Bundle
import android.support.v17.leanback.app.GuidedStepSupportFragment
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import com.google.android.media.tv.companionlibrary.model.ModelUtils
import com.google.android.media.tv.companionlibrary.sync.EpgSyncJobService
import com.google.android.media.tv.companionlibrary.utils.TvContractUtils
import com.zaclimon.tsutaeru.R
import com.zaclimon.tsutaeru.service.TsutaeruJobService
import com.zaclimon.tsutaeru.util.Constants

/**
 * Setting fragment which will either enable or disable the channel logos as seen on
 * the Live Channels application
 *
 * @author zaclimon
 */
class ChannelLogoGuidedFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        val sharedPreferences = context?.getSharedPreferences(Constants.TSUTAERU_PREFERENCES, Context.MODE_PRIVATE)
        val hasChannelLogo = sharedPreferences?.getBoolean(Constants.CHANNEL_LOGO_PREFERENCE, true) ?: true
        val title = getString(R.string.channel_logo_title)
        val description = getString(R.string.channel_logo_description)
        val breadcrumb = if (hasChannelLogo) {
            getString(R.string.activated_text)
        } else {
            getString(R.string.deactivated_text)
        }

        return GuidanceStylist.Guidance(title, description, breadcrumb, null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val yesAction = GuidedAction.Builder(context)
        val noAction = GuidedAction.Builder(context)

        yesAction.title(R.string.yes_text)
        noAction.title(R.string.no_text)
        yesAction.id(GuidedAction.ACTION_ID_YES)
        noAction.id(GuidedAction.ACTION_ID_NO)

        actions.add(yesAction.build())
        actions.add(noAction.build())
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        val id = action?.id

        val sharedPreferences = context?.getSharedPreferences(Constants.TSUTAERU_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val initialAction = sharedPreferences?.getBoolean(Constants.CHANNEL_LOGO_PREFERENCE, true)

        if (id == GuidedAction.ACTION_ID_YES) {
            editor?.putBoolean(Constants.CHANNEL_LOGO_PREFERENCE, true)
        } else {
            editor?.putBoolean(Constants.CHANNEL_LOGO_PREFERENCE, false)
        }

        editor?.apply()

        val modifiedAction = sharedPreferences?.getBoolean(Constants.CHANNEL_LOGO_PREFERENCE, true) ?: true

        /*
          Sync the channels to reflect the latest changes only if the stream type is different from
          before. Remove only the logos if the user doesn't want them. In that case, do an AsyncTask
          since it might freeze the user experience.
          */

        if (initialAction != modifiedAction) {
            if (!modifiedAction) {
                AsyncRemoveLogos().execute()
            }

            val inputId = TvContract.buildInputId(Constants.TV_INPUT_SERVICE_COMPONENT)
            EpgSyncJobService.requestImmediateSync(context, inputId, ComponentName(context, TsutaeruJobService::class.java))
            add(fragmentManager, EpgSyncLoadingGuidedFragment())
        } else {
            activity?.finish()
        }
    }

     inner class AsyncRemoveLogos : AsyncTask<Void?, Void?, Void?>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            if (isAdded) {
                val contentResolver = activity?.contentResolver
                val channels = ModelUtils.getChannels(contentResolver)
                for (channel in channels) {
                    val logoUri = TvContract.buildChannelLogoUri(channel.id)
                    contentResolver?.delete(logoUri, null, null)
                }
            }
            return (null)
        }

    }
}