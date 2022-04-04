package be.osoc.team1.backend.entities

/**
 * This class represents a few views which can be used by entities. A field marked as [Hidden] will not be displayed
 * when writerWithView [Public] is used, this allows us to hide certain fields such as the password of a user when
 * serializing this object to json. If writerWithView [Hidden] is used both the [Public] and [Hidden] fields will be
 * displayed, this because [Hidden] inherits [Public].
 */
class EntityViews {
    open class Public
    open class Hidden : Public()
}
