package io.cobrowse.sample.ui.databinding

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import io.cobrowse.CobrowseIO
import io.cobrowse.sample.R
import io.cobrowse.sample.databinding.ActivityDataBindingSampleBinding
import io.cobrowse.sample.ui.CobrowseViewModelFactory

/**
 * Usage example of Data Binding library.
 */
class DataBindingSampleActivity : AppCompatActivity(), CobrowseIO.Redacted {

    private lateinit var binding: ActivityDataBindingSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm = ViewModelProvider(this, CobrowseViewModelFactory())
            .get(DataBindingSampleViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding_sample)
        binding.vm = vm

        val adapter = DummyUserAdapter()
        binding.activityDataBindingSampleRecyclerview.adapter = adapter
    }

    override fun redactedViews(): MutableList<View> {
        if (this::binding.isInitialized) {
            val redacted = mutableListOf<View>()
            binding.activityDataBindingSampleRecyclerview.adapter.let {
                if (it is DummyUserAdapter) {
                    redacted.addAll(it.redactedViews())
                }
            }
            redacted.add(binding.activityDataBindingSampleEdittextLastName)
            redacted.add(binding.activityDataBindingSampleTextviewLastName)
            return redacted
        }
        return mutableListOf()
    }
}