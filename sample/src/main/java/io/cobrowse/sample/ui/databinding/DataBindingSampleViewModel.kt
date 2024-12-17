package io.cobrowse.sample.ui.databinding

import io.cobrowse.sample.data.DummyUser
import io.cobrowse.sample.ui.BaseViewModel

/**
 * View-Model for [DataBindingSampleActivity].
 */
class DataBindingSampleViewModel : BaseViewModel() {

    val user: DummyUser = DummyUser("John", "Doe")

    val users: List<DummyUser> = listOf(
        DummyUser("Jake", "Weary"),
        DummyUser("Norman", "Gordon"),
        DummyUser("Will", "Barrow"),
        DummyUser("Spruce", "Springclean"),
        DummyUser("Burgundy", "Flemming"),
        DummyUser("Piff", "Jenkins"),
        DummyUser("Fleece", "Marigold"),
        DummyUser("Barry", "Tone"),
        DummyUser("Giles", "Posture"),
        DummyUser("Abraham", "Pigeon"),
        DummyUser("Gustav", "Purpleson"),
        DummyUser("Jim", "Séchen"),
        DummyUser("Barry", "Tone"),
        DummyUser("Ruby", "Von Rails"),
        DummyUser("Manuel", "Internetiquette"),
        DummyUser("Anna", "Tomica-Leigh"),
        DummyUser("Joss", "Sticks"),
        DummyUser("Indigo", "Violet"),
        DummyUser("Chauffina", "Carr"),
        DummyUser("Ursula", "Gurnmeister"),
        DummyUser("Sir", "Cumference"),
        DummyUser("Giles", "Posture"),
        DummyUser("Nathaneal", "Down"),
        DummyUser("Hugh", "Saturation"),
        DummyUser("Chaplain", "Mondover"),
        DummyUser("Gunther", "Beard"),
        DummyUser("Archibald", "Northbottom"),
        DummyUser("Phillip", "Anthropy"),
        DummyUser("Hans", "Down"),
    )
}