package photoeditor.cutout.backgrounderaser.bg.remove.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerEditorBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.GenerealEditorModel

class EditorAdapter(val context: Context, private val options:ArrayList<GenerealEditorModel>, val itemClick:clickHandler) : RecyclerView.Adapter<EditorAdapter.ViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerEditorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(options[position])
        holder.itemView.setOnClickListener {
            handleClickListener(position)
        }

    }

    override fun getItemCount(): Int {
     return options.size
    }


    private fun handleClickListener (position: Int)
    {
        if(position == 0)
        {
             itemClick.onFilterClick()
        }
        if(position == 4)
        {
            itemClick.onBgClick()
        }
        if(position ==3)
        {
            itemClick.onCropClick()
        }
        if(position == 1)
        {
            itemClick.onStickerClick()

        }
        if(position == 2)
        {
            itemClick.onAdjustmentsClick()
        }

    }


    class ViewHolder(private val binding:RecyclerEditorBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(option:GenerealEditorModel)
       {
          binding.icon.setImageResource(option.icon)
          binding.option.text=option.option
       }
    }

    interface clickHandler
    {
        fun onFilterClick()
        fun onBgClick()
        fun onCropClick()
        fun onStickerClick()
        fun onAdjustmentsClick()
    }

}