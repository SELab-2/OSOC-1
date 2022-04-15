package be.osoc.team1.backend.util

import be.osoc.team1.backend.entities.StatusSuggestion

/**
 * This class is used to serialize a list of [StatusSuggestion] objects when it is used as a reference.
 * This makes sure it gets turned into a list containing their corresponding rest API urls.
 */
class StatusSuggestionListSerializer : ListSerializer<StatusSuggestion>({ "/statusSuggestions/" + it.id.toString() })
