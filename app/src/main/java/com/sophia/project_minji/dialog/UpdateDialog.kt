package com.sophia.project_minji.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.sophia.project_minji.databinding.TodoDialogBinding

class UpdateDialog(context: Context, updateDialogInterface: UpdateDialogInterface): Dialog(context) {

    //fragment에서 interface를 받아옴
    private var updateDialogInterface = updateDialogInterface
    private lateinit var binding: TodoDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TodoDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //배경 투명하게 바꿔준다
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.saveButton.setOnClickListener {
            val content = binding.todoEdit.text.toString()

            updateDialogInterface.onOkButtonClicked(content)
            dismiss()
        }
    }
}