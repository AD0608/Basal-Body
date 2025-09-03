package com.basalbody.app.ui.auth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.basalbody.app.databinding.ItemIntroBinding
import com.basalbody.app.model.dummy.DummyData

class IntroPagerAdapter(private val pages: List<DummyData.IntroData>) :
    RecyclerView.Adapter<IntroPagerAdapter.PageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ItemIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val page = pages[position]

        holder.binding.apply {
            ivIntro.setImageResource(page.imageRes)
            tvTitle.text = page.title
            tvDescription.text = page.description
        }

    }

    override fun getItemCount() = pages.size

    inner class PageViewHolder(val binding: ItemIntroBinding) :
        RecyclerView.ViewHolder(binding.root)
}