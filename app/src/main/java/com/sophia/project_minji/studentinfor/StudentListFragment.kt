package com.sophia.project_minji.studentinfor

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.databinding.StListFragmentBinding
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.listeners.OnItemClickListener
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory
import com.sophia.project_minji.viewmodel.FirebaseViewModel

class StudentListFragment : Fragment(), OnItemClickListener{

    private var _binding: StListFragmentBinding? = null
    val binding: StListFragmentBinding
        get() = _binding!!

    private lateinit var linearAdapter: StudentLinearAdapter
    private lateinit var gridAdapter: StudentGridAdapter
    private var studentList: ArrayList<StudentEntity> = ArrayList()

    private lateinit var preferenceManager: PreferenceManager

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory()
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

        preferenceManager = PreferenceManager(requireContext())

        init()
        setStudentInFor()
        initRecyclerLinear()
        imageButtonClick()
        setAddStudentBtn()
        loadUserDetails()

    }

    private fun loadUserDetails() {
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.profile.setImageBitmap(bitmap)
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

    private fun init() {
        linearAdapter = StudentLinearAdapter(requireContext(), studentList, viewModel,this)
        gridAdapter = StudentGridAdapter(requireContext(), studentList, viewModel,this)
    }

    //firestore에 저장된 학생 데이터를 가져옴
    private fun setStudentInFor() {
//        linearAdapter = StudentLinearAdapter(requireContext(), studentList, viewModel)
        viewModel.setStudentInFor(studentList,linearAdapter, gridAdapter)
    }

    override fun onItemClick(student: StudentEntity) {
        val intent = Intent(activity, StudentInforActivity::class.java)
        intent.putExtra("id",student.id)
        startActivity(intent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}