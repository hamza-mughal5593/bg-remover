package photoeditor.cutout.backgrounderaser.bg.remove.android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerStickerTypesBinding

class StickersAdapter(private val stickerTypes:ArrayList<Int>,val itemClick:clickHandler) : RecyclerView.Adapter<StickersAdapter.ViewHolder>() {


    var row_index=-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerStickerTypesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(stickerTypes[position])

        holder.itemView.setOnClickListener {

            row_index=position
            itemClick.onClickStickerType(position)
            notifyDataSetChanged()
        }

        if(row_index == position)
        {
            holder.binding.bg.setBackgroundResource(R.color.theme_color)
        }else
        {
            holder.binding.bg.setBackgroundResource(R.color.white)

        }

    }

    override fun getItemCount(): Int {

        return stickerTypes.size
    }


    public class ViewHolder( val binding: RecyclerStickerTypesBinding) : RecyclerView.ViewHolder(binding.root)
    {

        fun bind (stickerType:Int)
        {
            binding.sticker.setImageResource(stickerType)
        }

    }

    interface clickHandler
    {
      fun onClickStickerType(position:Int)
    }

}