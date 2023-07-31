package io.blmswap.blmswap.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.blmswap.blmswap.R
import io.blmswap.blmswap.databinding.CardTokenBinding
import io.blmswap.blmswap.models.Token
import io.blmswap.blmswap.room.entities.TokenEntity

class TokenAdapter(private var tokenList:List<TokenEntity>,private var listener: CardClickListener) : RecyclerView.Adapter<TokenAdapter.TokenViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TokenAdapter.TokenViewHolder {
        var cardTokenBinding: CardTokenBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.card_token,parent,false)
        return TokenViewHolder(cardTokenBinding)
    }

    override fun onBindViewHolder(holder: TokenAdapter.TokenViewHolder, position: Int) {
        holder.bind(tokenList[position],listener,position)
    }

    override fun getItemCount(): Int {
        return tokenList.size
    }

    class TokenViewHolder(private var binding: CardTokenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item : TokenEntity,listener:CardClickListener ,position: Int){
            binding.nameToken.text = item.name
            binding.symbolToken.text = item.symbol
            Glide.with(binding.root.context)
                .load(item.logoBlockChain)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imageChainToken)
            Glide.with(binding.root.context)
                .load(item.logoURI)
                .thumbnail(Glide.with(binding.root.context).load(R.drawable.ic_circle_question))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imageToken)

            Log.e("token",item.toString())

            binding.mainConstraintCardToken.setOnClickListener {
                listener.clickOnCardTokenForSelect(item)
            }


            binding.whiteStarFavorite.setOnClickListener {
                listener.clickChangeStatusToken(item,position)
            }
            binding.goldenStarFavorite.setOnClickListener {
                listener.clickChangeStatusToken(item,position)
            }

            if (item.isFavorite){
                binding.whiteStarFavorite.visibility = View.GONE
                binding.goldenStarFavorite.visibility = View.VISIBLE
            }else{
                binding.whiteStarFavorite.visibility = View.VISIBLE
                binding.goldenStarFavorite.visibility = View.GONE
            }
        }
    }

}