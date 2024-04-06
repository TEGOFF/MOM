package ModulesAndAdapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.tm.CategoryActivity
import com.example.tm.R
import DataClasses.Category

class CategoriesPopUpAdapter(view: View, cats: MutableList<Category>): RecyclerView.Adapter<CategoriesPopUpAdapter.ViewHolder>() {

    val cats = cats

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val catsname: TextView = itemView.findViewById(R.id.tvCatName)
        val lay: ConstraintLayout = itemView.findViewById(R.id.clCatsLay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = cats[position].name

        holder.catsname.setText("# ${name}")

        holder.lay.setOnClickListener {
            val intent = Intent(holder.itemView.context, CategoryActivity::class.java)

            intent.putExtra("catsName", cats[position].id)
            intent.putExtra("catsName", cats[position].name)

            startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return cats.size
    }
}