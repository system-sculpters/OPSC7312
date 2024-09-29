package com.opsc.opsc7312.view.custom

import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class PercentValueFormatter : ValueFormatter() {
    // This class was adapted from YouTube
    // https://youtu.be/-TGUV_LbcmE?si=VItXcDdnX_I8CsiE
    // Admin Grabs Media
    // https://www.youtube.com/@AdminGrabsMedia
    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return String.format("%.1f%%", value) // Format to one decimal place
    }
}