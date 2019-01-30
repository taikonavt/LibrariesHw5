package ru.geekbrains.android3_5.mvp.model.image;

import android.support.annotation.Nullable;


public interface ImageLoader<T>
{
    void loadInto(@Nullable String url, T container);
}
