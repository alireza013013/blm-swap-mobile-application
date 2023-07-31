package io.blmswap.blmswap.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.blmswap.blmswap.R
import io.blmswap.blmswap.databinding.CardTokenListBinding
import io.blmswap.blmswap.room.entities.TokenList


class TokenListAdapter(private var tokenList:List<TokenList>,private var listener: TokenListClickListener) : RecyclerView.Adapter<TokenListAdapter.TokenListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TokenListAdapter.TokenListViewHolder {
        var cardTokenListBinding : CardTokenListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.card_token_list,parent,false)
        return TokenListViewHolder(cardTokenListBinding)
    }

    override fun onBindViewHolder(holder: TokenListAdapter.TokenListViewHolder, position: Int) {
        holder.bind(tokenList[position],listener,position)
    }

    override fun getItemCount(): Int {
        return tokenList.size
    }

    class TokenListViewHolder(private var binding: CardTokenListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item : TokenList,listener:TokenListClickListener, position: Int){
           binding.nameTokenList.text = item.name
           binding.textCountToken.text = item.countToken.toString() + "Tokens"
            Glide.with(binding.root.context)
                .load(item.logoURI)
                .thumbnail(Glide.with(binding.root.context).load(R.drawable.ic_circle_question))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imageListToken)

            binding.textVersionList.text = "v"+item.versionMajor+"."+item.versionMinor+"."+item.versionPatch

            if (item.visibility){
                binding.switchButtonVisibility.isChecked = false
            }else{
                binding.switchButtonVisibility.isChecked = true
            }

            binding.switchButtonVisibility.setOnCheckedChangeListener { _, isChecked ->
                item.visibility = !isChecked
                listener.changeStatusVisibilityListToken(item,position)
            }

            binding.mainConstraintCardListToken.setOnClickListener {
                if (binding.mainConstraintRemoveList.visibility == View.VISIBLE){
                    binding.mainConstraintRemoveList.visibility = View.GONE
                }
            }


            binding.textRemoveList.setOnClickListener {
                listener.deleteListTokenFromDatabase(item)
            }

            binding.textViewOnBsc.setOnClickListener {
//                val url = "https://tokenlists.org/token-list?url=${}"
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(url)
//                startActivity(i)
            }

            binding.imageGear.setOnClickListener {
                if (binding.mainConstraintRemoveList.visibility == View.VISIBLE){
                    binding.mainConstraintRemoveList.visibility = View.GONE
                }else{
                    binding.mainConstraintRemoveList.visibility = View.VISIBLE
                }
            }



        }
    }

}