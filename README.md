<p align = "center">МИНИСТЕРСТВО НАУКИ И ВЫСШЕГО ОБРАЗОВАНИЯ
РОССИЙСКОЙ ФЕДЕРАЦИИ
ФЕДЕРАЛЬНОЕ ГОСУДАРСТВЕННОЕ БЮДЖЕТНОЕ
ОБРАЗОВАТЕЛЬНОЕ УЧРЕЖДЕНИЕ ВЫСШЕГО ОБРАЗОВАНИЯ
«САХАЛИНСКИЙ ГОСУДАРСТВЕННЫЙ УНИВЕРСИТЕТ»</p>
<br><br><br><br><br><br>
<p align = "center">Институт естественных наук и техносферной безопасности<br>Кафедра информатики<br>Ефанов Антон Максимович</p>
<br><br><br>

<p align = "center">Лабораторная работа 8<br>01.03.02 Прикладная математика и информатика</p>
<br><br><br><br><br><br><br><br><br><br><br><br>
<p align = "right">Научный руководитель<br>
Соболев Евгений Игоревич</p>
<br><br><br>
<p align = "center">г. Южно-Сахалинск<br>2023 г.</p>

***
# <p align = "center">Оглавление</p>
- [Цели и задачи](#цели-и-задачи)
- [Решение задач](#решение-задач)
- [Проблема при клонировании с Git](#проблема)
- [Вывод](#вывод)

***

# <p align = "center">Цели и задачи</p>

Задачи:
Упражнение. Типы View в RecyclerView 
Для этого сложного упражнения вам нужно будет создать два типа строк в вашем RecyclerView: для обычных и для более серьезных преступлений. Чтобы это реализовать, вы будете работать с функцией в RecyclerView.Adapter. Присвойте новое свойство requiresPolice объекту Crime и используйте его, чтобы определить, какой тип View загружен в CrimeAdapter, путем реализации функции getItemViewType(Int) (developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemViewType). В функции onCreateViewHolder(ViewGroup, Int) вам также необходимо добавить логику, которая возвращает различные ViewHolder в зависимости от ViewType, возвращаемого функцией getItemViewType(Int). Используйте оригинальный макет для преступлений, которые не требуют вмешательства полиции, и новый макет с усовершенствованным интерфейсом, содержащий кнопку с надписью «Связаться с полицией» для серьезных преступлений.

Приложение. Авто
Приложение, должно иметь следующие функции:
•	Отображение списка автомобилей с характеристиками (10-12 автомобилей, 3 производителя, 1-3 марки у каждого производителя)
•	Добавление нового автомобиля
•	Редактирование деталей автомобиля
Желательно:

•	Фильтрация по производителю и марке
•	Сортировка по цене

*** 

# <p align = "center">Решение</p>

`MainActivity.kt`
```
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
//help
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
```
`CarSelectionActivity.kt`
```
package com.example.lab8

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class CarSelectionActivity : AppCompatActivity() {
    private lateinit var manufacturerSpinner: Spinner
    private lateinit var modelSpinner: Spinner
    private lateinit var priceEditText: EditText

    private lateinit var selectedManufacturer: String
    private lateinit var selectedModel: String

    companion object {
        private const val ADD_CAR_REQUEST_CODE = 1
        private const val CAR_EXTRA_KEY = "car_extra"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_selection)

        manufacturerSpinner = findViewById(R.id.manufacturerSpinner)
        modelSpinner = findViewById(R.id.modelSpinner)
        priceEditText = findViewById(R.id.priceEditText)

        val manufacturers = listOf("Toyota", "BMW", "Ford")
        val manufacturerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, manufacturers)
        manufacturerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        manufacturerSpinner.adapter = manufacturerAdapter

        manufacturerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedManufacturer = parent.getItemAtPosition(position).toString()
                val models = getModelList(selectedManufacturer)
                val modelAdapter = ArrayAdapter(this@CarSelectionActivity, android.R.layout.simple_spinner_item, models)
                modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                modelSpinner.adapter = modelAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedModel = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val price = priceEditText.text.toString()
            if (price.isNotEmpty()) {
                val car = Car(selectedManufacturer, selectedModel, price.toLong(), getPhotoResId(selectedManufacturer, selectedModel))
                val intent = Intent()
                val carBundle = Bundle()
                carBundle.putString("manufacturer", car.manufacturer)
                carBundle.putString("model", car.model)
                carBundle.putLong("price", car.price)
                carBundle.putInt("photoResId", car.photoResId)
                intent.putExtra(CAR_EXTRA_KEY, carBundle)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun getModelList(manufacturer: String): List<String> {
        return when (manufacturer) {
            "Toyota" -> listOf("Corolla", "Camry", "RAV4")
            "BMW" -> listOf("3 Series", "5 Series", "X5")
            "Ford" -> listOf("Mustang", "Focus", "Escape")
            else -> emptyList()
        }
    }

    private fun getPhotoResId(manufacturer: String, model: String): Int {
        val photoName = "${manufacturer.toLowerCase()}_${model.toLowerCase()}".replace(" ", "_")
        return resources.getIdentifier(photoName, "drawable", packageName)
    }

}
```
***

# <p align = "center">Вывод</p>
Выполнив *лабораторную работу 8*, разобрался как нужно работать с элементами и RecicleView.
