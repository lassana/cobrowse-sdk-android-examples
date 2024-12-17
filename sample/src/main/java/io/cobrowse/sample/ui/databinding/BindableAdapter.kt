package io.cobrowse.sample.ui.databinding

/**
 * Implement in an adapters that uses Data Binding.
 */
interface BindableAdapter<T> {
    fun setData(items: List<T>)
}