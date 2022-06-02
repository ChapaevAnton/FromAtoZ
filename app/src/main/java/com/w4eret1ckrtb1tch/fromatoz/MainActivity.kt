package com.w4eret1ckrtb1tch.fromatoz

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.w4eret1ckrtb1tch.fromatoz.Item.Contact
import com.w4eret1ckrtb1tch.fromatoz.Item.Header
import com.w4eret1ckrtb1tch.fromatoz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val data = listOf(
        Contact(R.drawable.avatar, "Nick", "Kelvin"),
        Contact(R.drawable.avatar, "Иван", "Аетров"),
        Contact(R.drawable.avatar, "Иван", "Ветров"),
        Contact(R.drawable.avatar, "Иван", "гетров"),
        Contact(R.drawable.avatar, "Иван", "Детров"),
        Contact(R.drawable.avatar, "Иван", "Еетров"),
        Contact(R.drawable.avatar, "Иван", "Жетров"),
        Contact(R.drawable.avatar, "Иван", "Зетров"),
        Contact(R.drawable.avatar, "Иван", "Иетров"),
        Contact(R.drawable.avatar, "Иван", "Кетров"),
        Contact(R.drawable.avatar, "Иван", "Летров"),
        Contact(R.drawable.avatar, "Иван", "Метров"),
        Contact(R.drawable.avatar, "Иван", "Нетров"),
        Contact(R.drawable.avatar, "Иван", "Оетров"),
        Contact(R.drawable.avatar, "Иван", "Петров"),
        Contact(R.drawable.avatar, "Иван", "Ретров"),
        Contact(R.drawable.avatar, "Иван", "Сетров"),
        Contact(R.drawable.avatar, "Иван", "Тетров"),
        Contact(R.drawable.avatar, "Иван", "Уетров"),
        Contact(R.drawable.avatar, "Иван", "Фетров"),
        Contact(R.drawable.avatar, "Alli", "Baba"),
        Contact(R.drawable.avatar, "Maikl", "Kent"),
        Contact(R.drawable.avatar, "Keven", "Zalupov"),
        Contact(R.drawable.avatar, "Алеша", "Николаев"),
        Contact(R.drawable.avatar, "Макс", "Вернер"),
        Contact(R.drawable.avatar, "Maks", "Peterson"),
        Contact(R.drawable.avatar, "Alfa", "Kent"),
        Contact(R.drawable.avatar, "Petr", "1"),
        Contact(R.drawable.avatar, "Alex", "1"),
        Contact(R.drawable.avatar, "Cat", "felix")
    )

    private val items: MutableList<Item> = mutableListOf()

    private val sortedItems = data.asSequence()
        .sortedWith(compareBy({ it.lastName }, { it.firstName }))

    private val alphabet = sortedItems.asSequence()
        .map { Header(it.lastName.first()) }
        .distinct()
        .toList()

    private lateinit var binding: ActivityMainBinding

    private val contactAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ItemsAdapter()
    }
    private val alphabetAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ItemsAdapter(
            onAlphabetClickListener = { header, selectPosition ->
                Log.d("TAG", "header :$header ")
                Log.d("TAG", "selectPosition :$selectPosition ")
                scrollToPosition(header)
                selectMarker(selectPosition)
            }
        )
    }
    private val contactDecorator by lazy {
        ItemsHeaderDecorator(
            context = this@MainActivity,
            colorResDivider = R.color.temp,
            getItems = { contactAdapter.items as List<Contact> }
        )
    }

    private val centerSmoothScroller by lazy {
        CenterSmoothScroller(this)
    }

    private val itemTouchHelper by lazy {
        ItemTouchHelper(
            ItemsSwipeToDelete(
                onContactSwipeListener = { position ->
                    deleteContact(position)
                }
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // OPTIMIZE: старый вариант - через изменение общего списка

//        alphabet.forEach { header ->
//            val contactGroup = sortedItems
//                .filter { it.lastName.first() == header.label }
//                items.plusAssign(sequenceOf(header).plus(contactGroup))
//        }
//        Log.d("TAG", "alphabet: $alphabet")
//        Log.d("TAG", "all: $items")

        items.plusAssign(sortedItems)

        binding.contacts.apply {
            layoutManager = ItemsLinearLayoutManager(this@MainActivity)
            adapter = contactAdapter
            addItemDecoration(contactDecorator)
            itemTouchHelper.attachToRecyclerView(this)
        }
        binding.alphabet.adapter = alphabetAdapter

        contactAdapter.items = items
        alphabetAdapter.items = alphabet
    }

    private fun selectMarker(position: Int) {
        alphabetAdapter.selectMarker(position)
        centerSmoothScroller.targetPosition = position
        binding.alphabet
            .layoutManager
            ?.startSmoothScroll(centerSmoothScroller)
    }

    private fun deleteContact(position: Int) {
        items.removeAt(position)
        contactAdapter.deleteContact(position)
    }

    private fun scrollToPosition(header: Header) {
        val positionMarker = items.indexOfFirst { (it as Contact).lastName.first() == header.label }
        if (positionMarker != -1) {
            binding.contacts.smoothScrollToPosition(positionMarker)
        }
    }
    // OPTIMIZE: старый вариант - через изменение общего списка

//    private fun scrollToPosition(header: Header) {
//        val positionMarker = items.indexOfFirst { it == header }
//        if (positionMarker != -1) {
//            binding.contacts.smoothScrollToPosition(positionMarker)
//        }
//    }
}