package photoeditor.cutout.backgrounderaser.bg.remove.android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerStickersBinding

class StickersSubAdapter(private val stickers:ArrayList<Int>,val itemClick:clickHandler) : RecyclerView.Adapter<StickersSubAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerStickersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(stickers[position])
        holder.itemView.setOnClickListener {

            itemClick.onClickSticker(stickers[position])
        }

    }

    override fun getItemCount(): Int {

        return stickers.size
    }


    public class ViewHolder(private val binding: RecyclerStickersBinding) : RecyclerView.ViewHolder(binding.root)
    {

        fun bind (stickerType:Int)
        {
            binding.sticker.setImageResource(stickerType)
        }

    }

    interface clickHandler
    {
        fun onClickSticker(sticker:Int)
    }
}