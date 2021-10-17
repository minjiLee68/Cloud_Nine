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
import com.sophia.project_minji.R
import com.sophia.project_minji.databinding.RvItemGrideBinding
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.viewmodel.FirebaseViewModel

class StudentGridAdapter(var context: Context, var studentList: ArrayList<StudentEntity>, val viewModel: FirebaseViewModel)
    : ListAdapter<StudentEntity, StudentGridAdapter.StGrideViewHolder>(

        object : DiffUtil.ItemCallback<StudentEntity>() {
            override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean =
                oldItem.name == newItem.name && oldItem.brith == newItem.brith
                        && oldItem.phNumber == newItem.phNumber && oldItem.character == newItem.character
                        && oldItem.image == newItem.image && oldItem.date == newItem.date

        }

){
    var onItemClickListener: OnItemClickListener? = null

    inner class StGrideViewHolder(private val binding: RvItemGrideBinding)
        : RecyclerView.ViewHolder(binding.root) {

        val imageIv = binding.itemImage
        val carBtn = binding.stCardview
//        var requestOption: RequestOptions = RequestOptions()
//        val requestOptions = requestOption.transform(CenterCrop(),RoundedCorners(10))

        private val menus = binding.menuGrid

        fun bind(student: StudentEntity) {
            Glide.with(context).load(student.image).into(imageIv)
            binding.itemName.text = student.name
            binding.itemBirth.text = student.brith
            carBtn.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener?.onItemClickGrid(student)
                }
            }
            menus.setOnClickListener {
                popupMenus(it)
            }
        }

        private fun popupMenus(v: View) {
            val popupMenus = PopupMenu(context.applicationContext,v)
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
        fun onItemClickGrid(student: StudentEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StGrideViewHolder =
        StGrideViewHolder(
            RvItemGrideBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )

    override fun onBindViewHolder(holder: StGrideViewHolder, position: Int) {
        val stInfor = studentList[position]
        holder.bind(stInfor)
    }
}