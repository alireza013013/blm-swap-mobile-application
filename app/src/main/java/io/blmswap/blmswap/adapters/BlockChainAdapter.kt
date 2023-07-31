package io.blmswap.blmswap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.blmswap.blmswap.R
import io.blmswap.blmswap.databinding.CardBlockChainBinding
import io.blmswap.blmswap.models.BlockChain
import io.blmswap.blmswap.tokenSelectModal.fragment.TokenSelectFragment

class BlockChainAdapter(private var blockChainList:List<BlockChain>, private var listener: CardClickListener) : RecyclerView.Adapter<BlockChainAdapter.BlockChainViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BlockChainAdapter.BlockChainViewHolder {
        var blockChainBinding: CardBlockChainBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.card_block_chain,parent,false)
        return BlockChainViewHolder(blockChainBinding)
    }

    override fun onBindViewHolder(holder: BlockChainAdapter.BlockChainViewHolder, position: Int) {
        holder.bind(blockChainList[position],listener,position)
    }

    override fun getItemCount(): Int {
        return blockChainList.size
    }

    class BlockChainViewHolder(private var binding: CardBlockChainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item : BlockChain,listener:CardClickListener,position: Int){
            if(item.isSelect){
                binding.mainConstraintCardChainDeselect.visibility = View.GONE
                binding.mainConstraintCardChainSelect.visibility = View.VISIBLE
                binding.nameChain.text = item.name
                Glide.with(binding.root.context)
                    .load(item.imageSrc)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageChainSelect)
            }
            if (!item.isSelect){
                binding.mainConstraintCardChainDeselect.visibility = View.VISIBLE
                binding.mainConstraintCardChainSelect.visibility = View.GONE
                Glide.with(binding.root.context)
                    .load(item.imageSrc)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageChain)

            }
            binding.mainConstraintCardChainDeselect.setOnClickListener {
                listener.clickOnCardBlockChain(item,position)
            }
            binding.mainConstraintCardChainSelect.setOnClickListener {
                listener.clickOnCardBlockChain(item,position)
            }
            binding.executePendingBindings()
        }
    }

}