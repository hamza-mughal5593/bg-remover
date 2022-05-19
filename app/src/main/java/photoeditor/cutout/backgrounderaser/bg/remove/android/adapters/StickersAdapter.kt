package photoeditor.cutout.backgrounderaser.bg.remove.android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerEditorBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerStickerTypesBinding

class StickersAdapter(private val stickerTypes:ArrayList<Int>) : RecyclerView.Adapter<StickersAdapter.ViewHolder>() {



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

    }

    override fun getItemCount(): Int {

        return stickerTypes.size
    }


    public class ViewHolder(private val binding: RecyclerStickerTypesBinding) : RecyclerView.ViewHolder(binding.root)
    {

        fun bind (stickerType:Int)
        {
            binding.sticker.setImageResource(stickerType)
        }

    }

}