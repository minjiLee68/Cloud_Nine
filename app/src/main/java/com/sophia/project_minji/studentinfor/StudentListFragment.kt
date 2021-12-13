package com.sophia.project_minji.studentinfor

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.project_minji.ProfileSetUpActivity
import com.sophia.project_minji.R
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.databinding.StListFragmentBinding
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.listeners.OnItemClickListener
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory
import com.sophia.project_minji.viewmodel.FirebaseViewModel

class StudentListFragment : Fragment(), OnItemClickListener {

    private var _binding: StListFragmentBinding? = null
    val binding: StListFragmentBinding
        get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String

    private lateinit var linearAdapter: StudentLinearAdapter
    private lateinit var gridAdapter: StudentGridAdapter
    private var studentList: MutableList<StudentEntity> = mutableListOf()

    private lateinit var preferenceManager: PreferenceManager

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initRecyclerLinear()
        imageButtonClick()
        setAddStudentBtn()
        loadUserDetails()
        setStudentInFor()
    }

    private fun init() {
        preferenceManager = PreferenceManager(requireContext())
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        linearAdapter = StudentLinearAdapter(studentList, viewModel, this)
        gridAdapter = StudentGridAdapter(studentList, viewModel, this)
    }

    private fun loadUserDetails() {
        firestore.collection("Users").document(uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val image = task.result!!.getString("image")
                        Glide.with(requireContext()).load(image).into(binding.profile)
                    }
                }
            }
        binding.profile.setOnClickListener {
            val intent = Intent(requireContext().applicationContext, ProfileSetUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun imageButtonClick() {
        binding.listBtn.setOnClickListener {
            initRecyclerLinear()
            binding.listBtn.visibility = View.GONE
            binding.grideBtn.visibility = View.VISIBLE
        }

        binding.grideBtn.setOnClickListener {
            initRecyclerGrid()
            binding.listBtn.visibility = View.VISIBLE
            binding.grideBtn.visibility = View.GONE
        }
    }

    //add버튼을 누르면 학생 추가할 수 있는 화면으로 바뀜
    private fun setAddStudentBtn() {
        binding.ivAdd.setOnClickListener {
            val intent = Intent(activity, AddStudentActivity::class.java)
            startActivity(intent)
        }
    }

    //Linear형식 recyclerview
    private fun initRecyclerLinear() {
        binding.stRecyclerview.let {
            it.adapter = linearAdapter
            it.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        }
    }

    //Grid형식 recyclerview
    private fun initRecyclerGrid() {
        binding.stRecyclerview.let {
            it.adapter = gridAdapter
            it.layoutManager = GridLayoutManager(activity,2)
        }
    }

    private fun setStudentInFor() {
        viewModel.setStudentInFor(studentList).observe(viewLifecycleOwner, {
            linearAdapter.submitList(it)
            gridAdapter.submitList(it)
        })
    }

    override fun onItemClick(id: StudentEntity) {
        val intent = Intent(activity, StudentInforActivity::class.java)
        intent.putExtra("id", id.id)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}