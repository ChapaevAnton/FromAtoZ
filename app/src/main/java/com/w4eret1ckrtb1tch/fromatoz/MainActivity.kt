package com.w4eret1ckrtb1tch.fromatoz

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.w4eret1ckrtb1tch.fromatoz.Item.Contact
import com.w4eret1ckrtb1tch.fromatoz.Item.Marker
import com.w4eret1ckrtb1tch.fromatoz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val data = listOf(
        Contact(1, R.drawable.avatar, "Nick", "Kelvin"),
        Contact(2, R.drawable.avatar, "Иван", "Аетров"),
        Contact(2, R.drawable.avatar, "Иван", "Бетров"),
        Contact(2, R.drawable.avatar, "Иван", "Ветров"),
        Contact(2, R.drawable.avatar, "Иван", "Гетров"),
        Contact(2, R.drawable.avatar, "Иван", "Детров"),
        Contact(2, R.drawable.avatar, "Иван", "Еетров"),
        Contact(2, R.drawable.avatar, "Иван", "Жетров"),
        Contact(2, R.drawable.avatar, "Иван", "Зетров"),
        Contact(2, R.drawable.avatar, "Иван", "Иетров"),
        Contact(2, R.drawable.avatar, "Иван", "Кетров"),
        Contact(2, R.drawable.avatar, "Иван", "Летров"),
        Contact(2, R.drawable.avatar, "Иван", "Метров"),
        Contact(2, R.drawable.avatar, "Иван", "Нетров"),
        Contact(2, R.drawable.avatar, "Иван", "Оетров"),
        Contact(2, R.drawable.avatar, "Иван", "Петров"),
        Contact(2, R.drawable.avatar, "Иван", "Ретров"),
        Contact(2, R.drawable.avatar, "Иван", "Сетров"),
        Contact(2, R.drawable.avatar, "Иван", "Тетров"),
        Contact(2, R.drawable.avatar, "Иван", "Уетров"),
        Contact(2, R.drawable.avatar, "Иван", "Фетров"),
        Contact(3, R.drawable.avatar, "Alli", "Baba"),
        Contact(4, R.drawable.avatar, "Maikl", "Kent"),
        Contact(5, R.drawable.avatar, "Keven", "Zalupov"),
        Contact(6, R.drawable.avatar, "Алеша", "Николаев"),
        Contact(7, R.drawable.avatar, "Макс", "Вернер"),
        Contact(8, R.drawable.avatar, "Maks", "Peterson"),
        Contact(9, R.drawable.avatar, "Alfa", "Kent"),
        Contact(10, R.drawable.avatar, "Petr", "1"),
        Contact(11, R.drawable.avatar, "Alex", "1"),
        Contact(11, R.drawable.avatar, "Cat", "felix")
    )

    private val items: MutableList<Item> = mutableListOf()

    private val sortedItems = data.asSequence()
        .sortedWith(compareBy({ it.lastName }, { it.firstName }))

    private val alphabet = sortedItems.asSequence()
        .map { Marker(it.lastName.first()) }
        .distinct()
        .toList()

    private lateinit var binding: ActivityMainBinding
    private val contactAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ItemsAdapter()
    }
    private val alphabetAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ItemsAdapter(
            onAlphabetClickListener = { marker, selectPosition ->
                Log.d("TAG", "marker :$marker ")
                Log.d("TAG", "selectPosition :$selectPosition ")
                scrollToPosition(marker)
                selectMarker(selectPosition)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alphabet.forEach { label ->
            val contactGroup = sortedItems
                .filter { it.lastName.first() == label.label }
                .toList()
            if (contactGroup.isNotEmpty()) {
                items.plusAssign(listOf(label).plus(contactGroup))
            }
        }
        Log.d("TAG", "alphabet: $alphabet")
        Log.d("TAG", "all: $items")

        binding.contacts.apply {
            layoutManager = ItemsLinearLayoutManager(this@MainActivity)
            adapter = contactAdapter
            addItemDecoration(HeaderDecoration(this@MainActivity, this, R.layout.item_header))
        }

        binding.alphabet.adapter = alphabetAdapter
        contactAdapter.items = items
        alphabetAdapter.items = alphabet
    }

    private fun selectMarker(position: Int) {
        alphabetAdapter.selectMarker(position)
    }

    private fun scrollToPosition(marker: Marker) {
        val positionMarker = items.indexOfFirst { it == marker }
        if (positionMarker != -1) {
            binding.contacts.smoothScrollToPosition(positionMarker)
        }
    }
}