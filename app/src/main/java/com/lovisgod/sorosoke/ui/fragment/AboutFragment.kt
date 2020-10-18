package com.lovisgod.sorosoke.ui.fragment

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lovisgod.sorosoke.R
import com.lovisgod.sorosoke.databinding.FragmentAboutBinding
import com.lovisgod.sorosoke.databinding.FragmentPlayerBinding
import com.lovisgod.sorosoke.viewmodel.AppViewModel


class AboutFragment : Fragment() {
    private lateinit var binding: FragmentAboutBinding

    private val viewModel: AppViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, AppViewModel.Factory(activity.application))
            .get(AppViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_about, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.loadHtml("about.html", this.requireContext())

        viewModel.aboutString.observe(viewLifecycleOwner, Observer {
            it.let {
               binding.aboutText.text = Html.fromHtml(it)
            }
        })

        viewModel.priceString.observe(viewLifecycleOwner, Observer {
            it.let {

            }
        })

        return binding.root
    }


}