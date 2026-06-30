package com.sessionzero.sessionzero.navigation

object AppDestination {
    const val CHARACTER_LIST = "character_list"
    const val SYSTEM_SELECTION = "system_selection"
    const val DECISION_TREE = "decision_tree"
    private const val CHARACTER_SHEET_BASE = "character_sheet"
    const val ARG_CLASS_ID = "classId"
    const val CHARACTER_SHEET = "$CHARACTER_SHEET_BASE/{$ARG_CLASS_ID}"
    fun characterSheetRoute(classId: String) = "$CHARACTER_SHEET_BASE/$classId"
}
