package com.sessionzero.sessionzero.navigation

object AppDestination {
    const val CHARACTER_LIST = "character_list"
    const val SYSTEM_SELECTION = "system_selection"
    const val CREATION_MODE = "creation_mode"
    const val STORY_AI = "story_ai"
    const val DECISION_TREE = "decision_tree"
    private const val CHARACTER_SHEET_BASE = "character_sheet"
    const val ARG_CLASS_ID = "classId"
    const val CHARACTER_SHEET = "$CHARACTER_SHEET_BASE/{$ARG_CLASS_ID}"
    fun characterSheetRoute(classId: String) = "$CHARACTER_SHEET_BASE/$classId"

    private const val CHARACTER_DETAIL_BASE = "character_detail"
    const val ARG_CHARACTER_ID = "characterId"
    const val CHARACTER_DETAIL = "$CHARACTER_DETAIL_BASE/{$ARG_CHARACTER_ID}"
    fun characterDetailRoute(characterId: Long) = "$CHARACTER_DETAIL_BASE/$characterId"
}
