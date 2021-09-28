package com.sophia.project_minji.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sophia.project_minji.adapter.TodoAdapter
import com.sophia.project_minji.databinding.CalendarFragmentBinding
import com.sophia.project_minji.dialog.DialogTodo
import com.sophia.project_minji.dialog.MyCustomDialogInterface
import com.sophia.project_minji.entity.TodoEntity
import com.sophia.project_minji.viewmodel.FbViewModel
import com.sophia.project_minji.viewmodel.FbViewModelFactory
import com.sophia.project_minji.viewmodel.TdViewModel
import com.sophia.project_minji.viewmodel.TdViewModelFactory

class CalendarFragment: Fragment(), MyCustomDialogInterface {

    private var _binding: CalendarFragmentBinding? = null
    val binding: CalendarFragmentBinding
    get() = _binding!!

    private lateinit var adapter: TodoAdapter

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    private val viewModel by viewModels<TdViewModel> {
        TdViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CalendarFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarDateData()
        initRecyclerview()
        setBtnAdd()
    }

    fun calendarDateData() {
        binding?.calendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
            this.year = year
            this.month = month
            this.day = dayOfMonth

            binding?.todayDate?.text = "${this.year}/${this.month}/${this.day}"

            viewModel.readDateData(this.year,this.month,this.day).observe(
                viewLifecycleOwner,{
                    (binding.todoRecycleview.adapter as TodoAdapter).submitList(it)
                }
            )
        }
    }

    fun initRecyclerview() {
        adapter = TodoAdapter(viewModel)
        binding.todoRecycleview.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            it.setHasFixedSize(true)
        }
    }

    fun setBtnAdd() {
        binding.addButton.setOnClickListener {
            if (year == 0) {
                Toast.makeText(activity, "날짜를 선택해주세요.",Toast.LENGTH_SHORT).show()
            } else {
                onFabClicked()
            }
        }
    }

    fun onFabClicked() {
        val myCustomDialog = DialogTodo(requireActivity(), this)
        myCustomDialog.show()
    }

    override fun onOkButtonClicked(content: String) {
        val memo = TodoEntity(content, year, month, day)
        viewModel.insert(memo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}