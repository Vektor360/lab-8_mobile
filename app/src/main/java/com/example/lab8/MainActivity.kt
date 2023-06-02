package com.example.lab8
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.lang.Math.round
import android.widget.Button
import android.content.Intent

private var sortByPriceDescending = true


class Car(
    val manufacturer: String,
    val model: String,
    val price: Long,
    val photoResId: Int
)

class CarAdapter(private val carList: MutableList<Car>) :
    RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_car, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = carList[position]
        holder.bind(car)
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    fun updateCarList(newCarList: MutableList<Car>) {
        carList.clear()
        carList.addAll(newCarList)
        notifyDataSetChanged()
    }

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val manufacturerTextView: TextView = itemView.findViewById(R.id.manufacturerTextView)
        private val modelTextView: TextView = itemView.findViewById(R.id.modelTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        private val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)

        fun bind(car: Car) {
            manufacturerTextView.text = car.manufacturer
            modelTextView.text = car.model
            priceTextView.text = car.price.toString()
            Glide.with(itemView)
                .load(car.photoResId)
                .into(photoImageView)
        }
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var carAdapter: CarAdapter
    private lateinit var carList: MutableList<Car>
    private var sortByPriceDescending = true


    private fun toggleSortOrderDescending() {
        sortByPriceDescending = true
        updateCarList(sortByPriceDescending)
    }

    private fun toggleSortOrderAscending() {
        sortByPriceDescending = false
        updateCarList(sortByPriceDescending)
    }

    private fun updateCarList(isDescendingOrder: Boolean) {
        val sortedCarList = if (isDescendingOrder) {
            carList.sortedByDescending { it.price }.toMutableList()
        } else {
            carList.sortedBy { it.price }.toMutableList()
        }
        carAdapter.updateCarList(sortedCarList)
    }

    companion object {
        private const val ADD_CAR_REQUEST_CODE = 1
        private const val CAR_EXTRA_KEY = "car_extra"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        carList = createCarList().toMutableList()
        carAdapter = CarAdapter(carList)
        recyclerView.adapter = carAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val filterButton: Button = findViewById(R.id.filterButton)
        filterButton.setOnClickListener {
            if (sortByPriceDescending) {
                toggleSortOrderAscending()
            } else {
                toggleSortOrderDescending()
            }
        }
        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, CarSelectionActivity::class.java)
            startActivityForResult(intent, ADD_CAR_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_CAR_REQUEST_CODE && resultCode == RESULT_OK) {
            val carBundle = data?.getBundleExtra(CAR_EXTRA_KEY)
            if (carBundle != null) {
                val manufacturer: String = carBundle.getString("manufacturer")!!
                val model: String = carBundle.getString("model")!!
                val price = carBundle.getLong("price")
                val photoResId = carBundle.getInt("photoResId")
                // Создайте объект Car с полученными данными и добавьте его в список
                val car = Car(manufacturer, model, price, photoResId)
                carList.add(car)
                // Обновите список автомобилей в RecyclerView
                updateCarList(sortByPriceDescending)
            }
        }
    }

    private fun toggleSortOrder() {
        sortByPriceDescending = !sortByPriceDescending
        updateCarList(sortByPriceDescending)
    }

    private fun createCarList(): List<Car> {
        val manufacturers = listOf("Toyota", "BMW", "Ford")
        val models = listOf(
            listOf("Corolla", "Camry", "RAV4"),
            listOf("3 Series", "5 Series", "X5"),
            listOf("Mustang", "Focus", "Escape")
        )
        val prices = mutableListOf<Double>()
        for (i in 0 until manufacturers.size) {
            for (j in 0 until models[i].size) {
                prices.add(Random.nextDouble(10000.0, 50000.0))
            }
        }
        val carList = mutableListOf<Car>()
        for (i in 0 until manufacturers.size) {
            for (j in 0 until models[i].size) {
                val model = models[i][j]
                val price = round(prices[i * 3 + j] / 100) * 100
                val photoResId = getPhotoResId(manufacturers[i], models[i][j])
                val car = Car(manufacturers[i], model, price, photoResId)
                carList.add(car)
            }
        }
        if (sortByPriceDescending) {
            carList.sortByDescending { it.price }
        } else {
            carList.sortBy { it.price }
        }

        return carList
    }

    private fun getPhotoResId(manufacturer: String, model: String): Int {
        val photoName = "${manufacturer.toLowerCase()}_${model.toLowerCase()}".replace(" ", "_")
        return resources.getIdentifier(photoName, "drawable", packageName)
    }
}