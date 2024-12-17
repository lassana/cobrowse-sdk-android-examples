package io.cobrowse.sample.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import io.cobrowse.sample.BR

/**
 * A dummy model to be used in the Data Binding sample.
 */
class DummyUser(firstName: String, lastName: String) : BaseObservable() {

    @get:Bindable
    var firstName: String = firstName
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstName)
        }


    @get:Bindable
    var lastName: String = lastName
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastName)
        }
}