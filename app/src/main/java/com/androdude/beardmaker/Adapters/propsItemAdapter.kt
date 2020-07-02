package com.androdude.beardmaker.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.androdude.beardmaker.ModelClass.propsItemClass
import com.androdude.beardmaker.R

class propsItemAdapter(val mList : ArrayList<propsItemClass>, val context : Context) : RecyclerView.Adapter<propsItemAdapter.mViewHolder>()
{
    lateinit var mListener : onItemClickListener

    interface onItemClickListener
    {
        fun getPos(pos : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener)
    {
        this.mListener=listener
    }





    class mViewHolder(itemview : View,mListener : onItemClickListener) : RecyclerView.ViewHolder(itemview)
    {
        val t1 = itemview.findViewById<ImageView>(R.id.beard_imageView)
        val t2 = itemview.findViewById<RelativeLayout>(R.id.beard_cardview)


        init {
            itemview.setOnClickListener{
                if(mListener != null && adapterPosition != RecyclerView.NO_POSITION)
                {
                    mListener.getPos(adapterPosition)
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
        return mViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout,null,false),mListener)
    }

    override fun getItemCount(): Int {
        return mList.size

    }

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        holder.t1.setImageResource(mList[position].id)


    }
}