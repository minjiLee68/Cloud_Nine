package com.sophia.project_minji.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sophia.project_minji.R
import com.sophia.project_minji.databinding.RvItemLinearBinding
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.viewmodel.FirebaseViewModel

class StudentLinearAdapter(var context: Context, var studentList: ArrayList<StudentEntity>,val viewModel: FirebaseViewModel) :
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

        var carBtn = binding.stCardview2

        private val menus = binding.menuList

        fun bind(student: StudentEntity) {
            binding.itemNameHz.text = student.name
            binding.itemBirthHz.text = student.brith
            binding.itemPhnumberHz.text = student.phNumber
            binding.itemImageHz.setImageBitmap(getUserImage(student.image!!))
            carBtn.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener?.onItemClickLinear(student)
                }
            }
            menus.setOnClickListener {
                popupMenu(it)
            }
        }

        private fun getUserImage(encodedImage: String): Bitmap {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        }

        @SuppressLint("NotifyDataSetChanged", "DiscouragedPrivateApi")
        private fun popupMenu(v: View) {
            val popupMenus = PopupMenu(context,v)
            popupMenus.inflate(R.menu.popup_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.update_tv -> { true }
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
        val stinfor = studentList[position]
        holder.bind(stinfor)
    }
}