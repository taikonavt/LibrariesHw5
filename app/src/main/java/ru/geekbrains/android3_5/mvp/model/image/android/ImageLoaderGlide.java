package ru.geekbrains.android3_5.mvp.model.image.android;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import io.paperdb.Paper;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import ru.geekbrains.android3_5.App;
import ru.geekbrains.android3_5.mvp.common.Utils;
import ru.geekbrains.android3_5.mvp.model.image.ImageLoader;
import ru.geekbrains.android3_5.ui.NetworkStatus;
import timber.log.Timber;

import java.io.ByteArrayOutputStream;

public class ImageLoaderGlide implements ImageLoader<ImageView> {
    @Override
    public void loadInto(@Nullable String url, ImageView container) {
        if (NetworkStatus.isOnline()) {
            GlideApp.with(container.getContext()).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model,
                                               Target<Bitmap> target, DataSource dataSource,
                                               boolean isFirstResource) {
                    App.getRepository().saveImage(resource, url);
                    return false;
                }
            }).into(container);
        } else {
            App.getRepository().getImage(url)
            .subscribe(new SingleObserver<Bitmap>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    GlideApp.with(container.getContext())
                            .load(bitmap)
                            .into(container);
                }

                @Override
                public void onError(Throwable throwable) {
                    Timber.e(throwable, "Failed read Avatar");
                    throwable.printStackTrace();
                }
            });
        }
    }
}
