package com.opsc.opsc7312.model.data.offline

interface DatabaseChangeListener {
    fun onDatabaseChanged()
    fun onTransactionsChanged()
    fun onGoalsChanged()
}