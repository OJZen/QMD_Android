package com.qmd.jzen.ui.viewmodel

import androidx.lifecycle.*
import com.qmd.jzen.database.entity.Music
import com.qmd.jzen.database.repository.MusicRepository
import kotlinx.coroutines.launch

class FavoriteMusicViewModel(private val repository: MusicRepository) : ViewModel() {

    val musicList : LiveData<List<Music>> = repository.allMusic.asLiveData()

    fun delete(music: Music) = viewModelScope.launch {
        repository.delete(music)
    }

}

class FavoriteMusicViewModelFactory(private val repository: MusicRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteMusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteMusicViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}