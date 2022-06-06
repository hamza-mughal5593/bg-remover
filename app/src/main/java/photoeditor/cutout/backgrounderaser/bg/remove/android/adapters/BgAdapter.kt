package photoeditor.cutout.backgrounderaser.bg.remove.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerBgBinding

class BgAdapter(
    val context: Context,
    val bgList: ArrayList<Int>,
    val type: String,
    val itemClick: clikHandleBg
) : RecyclerView.Adapter<BgAdapter.ViewHolder>() {

    var rowIndex: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerBgBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(bgList[position], type)
        holder.itemView.setOnClickListener {

            rowIndex = position
            itemClick.onClickBg(bgList[position], position)
            notifyDataSetChanged()
        }
        if (rowIndex == position) {
            holder.binding.bgBg.setBackgroundResource(R.color.theme_color)

        } else {
            holder.binding.bgBg.setBackgroundResource(R.color.white)

        }

    }

    override fun getItemCount(): Int {
        return bgList.size
    }

    public class ViewHolder(val binding: RecyclerBgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bg: Int, type: String) {
            if (type == "Images") {
                Picasso
                    .get()
                    .load(bg)
                    .resize(300, 300)
                    .noFade()
                    .into(binding.bg);
            } else {
                binding.bg.setBackgroundResource(bg)

            }

        }
    }

    interface clikHandleBg {
        fun onClickBg(bg: Int, position: Int)
    }
}