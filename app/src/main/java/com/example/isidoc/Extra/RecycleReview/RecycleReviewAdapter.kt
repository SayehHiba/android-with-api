package com.example.isidoc.Extra.RecycleReview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isidoc.R

class RecycleReviewAdapter(val List: Array<String>,var clickItem: OnRecycleReviewClickListner) :
        RecyclerView.Adapter<RecycleReviewAdapter.ListViewHolder>()
{
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ItemTextView: TextView = itemView.findViewById(R.id.Process_text)

        fun bind(valeur: String, action: OnRecycleReviewClickListner) {
            ItemTextView.text = valeur
            itemView.setOnClickListener{

                action.onItemClick(valeur,adapterPosition)
            }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row, parent, false)

        return ListViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return List.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        // holder.bind(processList[position])
        holder.bind(List[position],clickItem)
    }
}