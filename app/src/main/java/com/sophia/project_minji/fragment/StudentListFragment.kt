package com.sophia.project_minji.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sophia.project_minji.AddStudentActivity
import com.sophia.project_minji.StudentInforActivity
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.databinding.StListFragmentBinding
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.viewmodel.FbViewModelFactory
import com.sophia.project_minji.viewmodel.FbViewModel

class StudentListFragment : Fragment(), StudentLinearAdapter.OnItemClickListener,
    StudentGridAdapter.OnItemClickListener {

    private var _binding: StListFragmentBinding? = null
    val binding: StListFragmentBinding
        get() = _binding!!

    private lateinit var Ladapter: StudentLinearAdapter
    private lateinit var Gadapter: StudentGridAdapter
    private var studentList: ArrayList<StudentEntity> = ArrayList()

    private val viewModel by viewModels<FbViewModel> {
        FbViewModelFactory()
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

        getFirestore()
        initRecyclerLinear()
        setIvBtnLinear()
        setIvBtnGrid()
        setAddStudentBtn()

    }

    //linear버튼 클릭시 Grid버튼으로 바꿔지고 Linear형식으로 나옴
    fun setIvBtnLinear() {
        binding.listBtn.setOnClickListener {
            initRecyclerLinear()
            binding.listBtn.visibility = View.GONE
            binding.grideBtn.visibility = View.VISIBLE
        }
    }

    //Grid버튼 클릭시 Linear버튼으로 바꿔지고 Grid형식으로 바뀜
    fun setIvBtnGrid() {
        binding.grideBtn.setOnClickListener {
            initRecyclerGrid()
            binding.listBtn.visibility = View.VISIBLE
            binding.grideBtn.visibility = View.GONE
        }
    }

    //add버튼을 누르면 학생 추가할 수 있는 화면으로 바뀜
    fun setAddStudentBtn() {
        binding.ivAdd.setOnClickListener {
            var intent = Intent(activity, AddStudentActivity::class.java)
            startActivity(intent)
        }
    }

    //Linear형식 recyclerview
    fun initRecyclerLinear() {


        Ladapter.onItemClickListener = this

        binding.stRecyclerview.let {
            it.adapter = Ladapter
            it.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        }
    }

    //Grid형식 recyclerview
    fun initRecyclerGrid() {
        Gadapter.onItemClickListener = this

        binding.stRecyclerview.let {
            it.adapter = Gadapter
            it.layoutManager = GridLayoutManager(activity,2)
        }
    }

    //firestore에 저장된 학생 데이터를 가져옴
    fun getFirestore() {
        //어댑터 초기화
        Ladapter = StudentLinearAdapter(requireContext(), studentList, viewModel)
        Gadapter = StudentGridAdapter(requireContext(), studentList, viewModel)
        //viewmodel
        viewModel.setFirestore(studentList,Ladapter, Gadapter)
    }

    override fun onItemClickLinear(student: StudentEntity) {
        var intent = Intent(activity, StudentInforActivity::class.java)
        intent.putExtra("id",student.id)
        startActivity(intent)
    }

    override fun onItemClickGrid(student: StudentEntity) {
        var intent = Intent(activity, StudentInforActivity::class.java)
        intent.putExtra("id",student.id)
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}