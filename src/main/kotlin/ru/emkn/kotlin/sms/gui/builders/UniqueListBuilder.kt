package ru.emkn.kotlin.sms.gui.builders

/**
 * Builds a list of elements, guaranteed to be unique.
 */
class UniqueListBuilder<T>(
    private val list: MutableList<T> = mutableListOf(),
    private val equals: (T, T) -> Boolean = { lhs, rhs -> lhs === rhs }, // must form an equivalence relation
) {
    companion object {
        fun <T> checkListUniqueness(
            list: MutableList<T>,
            equals: (T, T) -> Boolean = { lhs, rhs -> lhs === rhs }, // must form an equivalence relation
        ) : Boolean {
            TODO()
        }

        /**
         * Create a builder from the [list].
         *
         * @throws [IllegalArgumentException] if the [list] had equal elements.
         */
        fun <T> fromListAndEquals(
            list: MutableList<T>,
            equals: (T, T) -> Boolean = { lhs, rhs -> lhs === rhs }, // must form an equivalence relation
        ) : UniqueListBuilder<T> {
            if (!checkListUniqueness(list, equals)) {
                throw IllegalArgumentException("Some elements of the list are equal.")
            }
            TODO()
        }
    }

    /**
     * Builds the list.
     */
    fun build(): List<T> {
        return list.toMutableList() // creates a copy
    }

    /**
     * Replaces this.list with a given [list].
     * Checks that in [list] all elements are unique.
     *
     * @return true, if the replacement was successful,
     * false if the given [list] had equal elements
     */
    fun replaceList(list: List<T>) : Boolean {
        TODO()
    }

    /**
     * Tries to add an element the list,
     * checks that it is unique to all the others.
     *
     * @return true if the element was added successfully, false otherwise
     */
    fun add(element: T) : Boolean {
        TODO()
    }

    /**
     * Tries to remove an element from the list.
     *
     * @return true if the element was removed successfully,
     * false in case the element was not present in the list
     */
    fun remove(element: T) : Boolean {
        TODO()
    }
}