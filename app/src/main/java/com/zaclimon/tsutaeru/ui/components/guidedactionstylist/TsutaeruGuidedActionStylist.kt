package com.zaclimon.tsutaeru.ui.components.guidedactionstylist

import androidx.leanback.widget.GuidedActionsStylist
import com.zaclimon.tsutaeru.R

/**
 * Custom [GuidedActionsStylist] adapted for Tsutaeru theme elements.
 *
 * @author zaclimon
 */
class TsutaeruGuidedActionStylist : GuidedActionsStylist() {

    override fun onProvideItemLayoutId(): Int {
        return R.layout.tsutaeru_guidedactions_item
    }

}