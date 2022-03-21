package com.example.android.codelabs.paging.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistryOwner;

import com.example.android.codelabs.paging.data.GithubRepository;

import org.jetbrains.annotations.NotNull;

public class ViewModelFactory extends AbstractSavedStateViewModelFactory {

    private GithubRepository repository;

    public ViewModelFactory(@NonNull @NotNull SavedStateRegistryOwner owner, GithubRepository repository) {
        super(owner, null);
        this.repository = repository;
    }

    @NonNull
    @NotNull
    @Override
    protected <T extends ViewModel> T create(@NonNull @NotNull String key,
                                             @NonNull @NotNull Class<T> modelClass,
                                             @NonNull @NotNull SavedStateHandle handle) {
        //modelClass是SearchRepositoriesViewModel类或其父类.
        if (modelClass.isAssignableFrom(SearchRepositoriesViewModel.class)) {
            return (T) new SearchRepositoriesViewModel(repository, handle);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
