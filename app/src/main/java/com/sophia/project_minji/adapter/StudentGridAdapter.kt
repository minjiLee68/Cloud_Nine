package com.sophia.project_minji.adapter

import android.annotation.SuppressLint
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
import com.sophia.project_minji.dataclass.StudentEntity
import com.sophia.project_minji.listeners.OnItemClickListener
import com.sophia.project_minji.viewmodel.FirebaseViewModel

class StudentGridAdapter(
    var studentList: MutableList<StudentEntity>,
    val viewModel: FirebaseViewModel,
    val listener: OnItemClickListener

) : ListAdapter<StudentEntity, StudentGridAdapter.StGrideViewHolder>(

    object : DiffUtil.ItemCallback<StudentEntity>() {
        override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean =
            oldItem.name == newItem.name && oldItem.birth == newItem.birth
                    && oldItem.phNumber == newItem.phNumber && oldItem.character == newItem.character
                    && oldItem.image == newItem.image

    }

) {

    inner class StGrideViewHolder(private val binding: RvItemGrideBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val menus = binding.menuGrid

        fun bind(student: StudentEntity) {
            Glide.with(itemView.context).load(student.image).into(binding.itemImage)

            binding.itemName.text = student.name
            binding.itemBirth.text = student.birth
            binding.root.setOnClickListener {
                listener.onItemClick(student)
            }
            menus.setOnClickListener {
                popupMenus(it)
            }
        }

        @SuppressLint("NotifyDataSetChanged", "DiscouragedPrivateApi")
        private fun popupMenus(v: View) {
            val popupMenus = PopupMenu(itemView.context, v)
            popupMenus.inflate(R.menu.popup_menu)
            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.update_tv -> {
                        true
                    }
                    R.id.delete -> {
                        viewModel.deleteStudent(adapterPosition)
                        studentList.remove(studentList[adapterPosition])
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
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StGrideViewHolder =
        StGrideViewHolder(
            RvItemGrideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: StGrideViewHolder, position: Int) {
        val stInfor = studentList[position]
        holder.bind(stInfor)
    }
}