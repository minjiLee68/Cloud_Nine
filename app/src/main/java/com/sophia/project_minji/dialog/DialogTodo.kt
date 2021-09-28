package com.sophia.project_minji.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.sophia.project_minji.databinding.TodoDialogBinding

class DialogTodo(context: Context, dialogInterface: MyCustomDialogInterface): Dialog(context) {

    private lateinit var binding: TodoDialogBinding
    private var myCustomDialogInterface = dialogInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TodoDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //배경 투명하게 바꿔준다
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.saveButton.setOnClickListener {
            val content = binding.todoEdit.text.toString()

            myCustomDialogInterface.onOkButtonClicked(content)
            dismiss()
        }
    }

}