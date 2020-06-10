package com.kodehauz.radiobasar.ui.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.kodehauz.radiobasar.R
import com.kodehauz.radiobasar.models.Comment
import com.kodehauz.radiobasar.ui.adapter.CommentAdapter
import com.kodehauz.radiobasar.viewmodel.AppViewModel
import com.mindorks.editdrawabletext.DrawablePosition
import com.mindorks.editdrawabletext.EditDrawableText
import com.mindorks.editdrawabletext.onDrawableClickListener

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

            val comment1 = Comment(name = "Ayooluwa", comment = "This is my comment and it's a demo data")
            val comment2 = Comment(name = "Mayowa", comment = "This is mayowa's comment coming in here")

            commentList.add(comment1)
            commentList.add(comment2)
            adapter.setDataList(commentList)
            val comment_input: EditDrawableText = view.findViewById(R.id.input_comment)
            comment_input.setDrawableClickListener(object : onDrawableClickListener {
                override fun onClick(target: DrawablePosition) {
                    when (target) {
                        DrawablePosition.RIGHT -> {
                            println(comment_input.text.toString())
                        }

                    }
                }
            })
        }


        return  view
    }
}