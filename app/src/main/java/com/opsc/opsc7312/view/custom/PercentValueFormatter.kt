package com.opsc.opsc7312.view.custom

import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class PercentValueFormatter : ValueFormatter() {
    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return String.format("%.1f%%", value) // Format to one decimal place
    }
}