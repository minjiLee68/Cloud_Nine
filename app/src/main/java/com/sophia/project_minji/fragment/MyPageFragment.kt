package com.sophia.project_minji.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sophia.project_minji.databinding.MypageFragmentBinding
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager

class MyPageFragment: Fragment() {

    private var _binding: MypageFragmentBinding ?= null
    val binding: MypageFragmentBinding
    get() = _binding!!

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MypageFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = PreferenceManager(requireContext())

        loadUserDetails()
    }

    private fun loadUserDetails() {
        preferenceManager = PreferenceManager(requireContext())

        binding.tvMyName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.myProfile.setImageBitmap(bitmap)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}