package com.lovisgod.sorosoke.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lovisgod.sorosoke.R
import com.lovisgod.sorosoke.databinding.CommentItemBinding
import com.lovisgod.sorosoke.models.Comment
import com.lovisgod.sorosoke.viewmodel.AppViewModel

class CommentAdapter(var viewmodel: AppViewModel): RecyclerView.Adapter<CommentAdapter.Viewholder>()  {
    private  var commentList : ArrayList<Comment> = ArrayList()

    class Viewholder(itemView: CommentItemBinding): RecyclerView.ViewHolder(itemView.root) {
        val label = itemView.userLabel
        val comment  = itemView.userComment
        val layout = itemView.commentLa
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val itemView : CommentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.comment_item, parent, false)
        return  Viewholder(itemView)
    }

    override fun getItemCount(): Int {
        if (this.commentList.isNotEmpty()) {
            return commentList.size
        } else {
            return  0
        }

    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        var comment = commentList.get(position)
        holder.label.text = comment.name.substring(0, 3)
        holder.comment.text = comment.comment
    }

    fun setDataList (list: ArrayList<Comment>) {
        this.commentList = list
        notifyDataSetChanged()
    }
}