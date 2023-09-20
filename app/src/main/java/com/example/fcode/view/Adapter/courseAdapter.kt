package com.example.fcode.view.Adapter

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fcode.R
import com.example.fcode.databinding.ItemRvBinding
import com.example.fcode.view.model.Course
import com.google.firebase.storage.FirebaseStorage


class courseAdapter(var listCourse: List<Course>, private val listener: itemClick): RecyclerView.Adapter<courseAdapter.courseViewHolder>() {

    inner class courseViewHolder(val itemBinding: ItemRvBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {
            fun binding(course: Course){
                Glide.with(itemBinding.imageView.context)
                    .load(course.img)
                    .centerCrop()
                    .into(itemBinding.imageView)
                itemBinding.nameCourse.text = course.nameCourse
                itemBinding.nameAuthor.text = "Tác giả: "+ course.author
                itemBinding.courseNotes.text = course.noteCourse
                itemBinding.itemRvCourse.setOnClickListener {
                    listener.viewClick(course.document)
                }
            }
    }

    interface itemClick{
        fun viewClick(document: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): courseViewHolder {
        val itemBinding = ItemRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return courseViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        Log.e("kich t", "getItemCount: ${listCourse.size}", )
        return listCourse.size
    }

    override fun onBindViewHolder(holder: courseViewHolder, position: Int) {
        val course = listCourse[position]
        holder.binding(course)
    }
}