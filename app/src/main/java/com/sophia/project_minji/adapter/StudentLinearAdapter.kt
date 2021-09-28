package com.sophia.project_minji.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sophia.project_minji.R
import com.sophia.project_minji.databinding.RvItemLinearBinding
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.fragment.StudentListFragment
import com.sophia.project_minji.viewmodel.FbViewModel

class StudentLinearAdapter(var context: Context, var studentList: ArrayList<StudentEntity>,val viewModel: FbViewModel) :
    ListAdapter<StudentEntity, StudentLinearAdapter.StLinearViewHolder>(

        object : DiffUtil.ItemCallback<StudentEntity>() {
            override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean =
                oldItem.name == newItem.name && oldItem.brith == newItem.brith
                        && oldItem.phNumber == newItem.phNumber && oldItem.character == newItem.character
                        && oldItem.image == newItem.image && oldItem.date == newItem.date

        }

    ) {

    var onItemClickListener: OnItemClickListener? = null

    inner class StLinearViewHolder(private val binding: RvItemLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var imageIv = binding.itemImageHz
        var carBtn = binding.stCardview2
        var requestOption: RequestOptions = RequestOptions()
        val requestOptions = requestOption.transform(CenterCrop(), RoundedCorners(50))

        val menus = binding.menuList

        fun bind(student: StudentEntity) {
            Glide.with(context).load(student.image).apply(requestOptions).into(imageIv)
            binding.itemNameHz.text = student.name
            binding.itemBirthHz.text = student.brith
            binding.itemPhnumberHz.text = student.phNumber
            carBtn.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener?.onItemClickLinear(student)
                }
            }
            menus.setOnClickListener {
                popupMenu(it)
            }
        }

        fun popupMenu(v: View) {
            val popupMenus = PopupMenu(context,v)
            popupMenus.inflate(R.menu.popup_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.update_tv -> { true }
                    R.id.delete -> {
                        viewModel.deleteStudent(adapterPosition)
                        studentList.remove(studentList.get(adapterPosition))
                        notifyDataSetChanged()
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java).invoke(menu,true)
        }
    }

    interface OnItemClickListener {
        fun onItemClickLinear(student: StudentEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StLinearViewHolder =
        StLinearViewHolder(
            RvItemLinearBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )


    override fun onBindViewHolder(holder: StLinearViewHolder, position: Int) {
        var stinfor = studentList[position]
        holder.bind(stinfor)
    }
}