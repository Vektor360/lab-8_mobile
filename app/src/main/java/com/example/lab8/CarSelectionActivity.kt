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

