package com.androdude.beardmaker.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androdude.beardmaker.ModelClass.propsClass
import com.androdude.beardmaker.R

class propsAdapter (val mList : ArrayList<propsClass>, val context : Context) : RecyclerView.Adapter<propsAdapter.mViewHolder>()
{

  lateinit var mListener : onClickListener

    interface onClickListener
    {
        fun getPos(pos : Int)
    }

    fun setOnClickListener(listener : onClickListener)
    {
        this.mListener=listener
    }





    class mViewHolder(itemview : View, mListener : onClickListener) : RecyclerView.ViewHolder(itemview)
    {
        val t1 = itemview.findViewById<ImageView>(R.id.beardImage_cardview)
        val t2 = itemview.findViewById<TextView>(R.id.name_cardview)



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
        return mViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.props_layout,null,false),mListener)
    }

    override fun getItemCount(): Int {
        return mList.size

    }

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        holder.t1.setBackgroundResource(mList[position].id)
        holder.t2.setText(mList[position].title)

    }
}