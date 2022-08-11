package com.example.dorazy

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.dorazy.databinding.LayoutMainColorDialogBinding

class ConfirmDialog(text1: String,text2: String,text3: String,) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: LayoutMainColorDialogBinding? = null
    private val binding get() = _binding!!

    private var text1: String? = null
    private var text2: String? = null
    private var text3: String? = null

    init {
        this.text1 = text1
        this.text2 = text2
        this.text3 = text3
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutMainColorDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.title.text = text1
        binding.message.text = text2
        binding.okBtn.text = text3

        // 확인 버튼 클릭
        binding.okBtn.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


//dialog.show(manager: FragmentManager, tag: String) 호출 시, manager로 넘겨줄 값
//activity -> this.supportFragmentManager
//fragment -> activity?.supportFragmentManager!!