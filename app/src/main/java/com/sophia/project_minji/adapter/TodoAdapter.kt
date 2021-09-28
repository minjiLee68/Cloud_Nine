package com.sophia.project_minji.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sophia.project_minji.databinding.TodoItemBinding
import com.sophia.project_minji.dialog.UpdateDialog
import com.sophia.project_minji.dialog.UpdateDialogInterface
import com.sophia.project_minji.entity.TodoEntity
import com.sophia.project_minji.viewmodel.TdViewModel

class TodoAdapter(val viewModel: TdViewModel): ListAdapter<TodoEntity, TodoViewHolder>(

    object : DiffUtil.ItemCallback<TodoEntity>() {
        override fun areItemsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean =
            oldItem.year == newItem.year && oldItem.month == newItem.month
                    && oldItem.day == newItem.day && oldItem.content == newItem.content

    }

) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder =
        TodoViewHolder(
            TodoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            viewModel
        )


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}

class TodoViewHolder(
    val binding: TodoItemBinding,
    val viewModel: TdViewModel
): RecyclerView.ViewHolder(binding.root), UpdateDialogInterface {

    private lateinit var todomemo: TodoEntity

    fun bind(todo: TodoEntity) {
        binding.itemTodo.text = todo.content

        binding.deleteTodo.setOnClickListener {
            viewModel.delete(todo)
        }

        //수정 버튼 클릭 시 다이얼로그를 띄운다
        binding.updateTodo.setOnClickListener {
            todomemo = todo
            val myCustomDialog = UpdateDialog(binding.updateTodo.context,this)
            myCustomDialog.show()
        }
    }

    //다이얼로그의 결과값으로 업데이트 해줌
    override fun onOkButtonClicked(content: String) {
        val updateTodoMemo = TodoEntity(content, todomemo.year, todomemo.month, todomemo.day, todomemo.id)
        viewModel.update(updateTodoMemo)
    }

}