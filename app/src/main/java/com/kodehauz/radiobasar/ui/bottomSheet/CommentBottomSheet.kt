package com.kodehauz.radiobasar.ui.bottomSheet

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.kodehauz.radiobasar.R
import com.kodehauz.radiobasar.models.AppEvent
import com.kodehauz.radiobasar.models.Comment
import com.kodehauz.radiobasar.models.ErrorEvent
import com.kodehauz.radiobasar.ui.adapter.CommentAdapter
import com.kodehauz.radiobasar.viewmodel.AppViewModel
import com.mindorks.editdrawabletext.DrawablePosition
import com.mindorks.editdrawabletext.EditDrawableText
import com.mindorks.editdrawabletext.onDrawableClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CommentBottomSheet(var layout: Int, var viewmodel: AppViewModel): BottomSheetDialogFragment() {
    private lateinit var adapter :CommentAdapter
    var commentList = ArrayList<Comment>()

    companion object{
        fun newInstance( layout:Int, viewmodel: AppViewModel): CommentBottomSheet? {
            val bottomSheet = CommentBottomSheet(layout = layout, viewmodel = viewmodel)
            return bottomSheet
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(layout, container, false)
        if (layout == R.layout.comment_layout) {
            adapter = CommentAdapter(viewmodel)
            val recy =view.findViewById<RecyclerView>(R.id.comment_list)
            recy.layoutManager = LinearLayoutManager(this.requireContext(),
                LinearLayoutManager.VERTICAL, false)
            recy.adapter = adapter


            viewmodel._commentList.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    adapter.setDataList(it)
                }
            })
            val comment_input: TextInputEditText = view.findViewById(R.id.input_comment)
            val sendBtn : ShapeableImageView = view.findViewById(R.id.sent_icon)
            sendBtn.setOnClickListener {
                println(comment_input.text.toString())
                viewmodel.submitComment(comment = comment_input.text.toString())
                comment_input.setText("")
            }
        }


        return  view
    }

}