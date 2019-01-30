package ru.geekbrains.android3_5.mvp.model.image.android;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import io.paperdb.Paper;
import ru.geekbrains.android3_5.mvp.common.Utils;
import ru.geekbrains.android3_5.mvp.model.image.ImageLoader;
import ru.geekbrains.android3_5.ui.NetworkStatus;

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
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Paper.book("images").write(Utils.SHA1(url), stream.toByteArray());
                    return false;
                }
            }).into(container);
        } else {
            String sha1 = Utils.SHA1(url);
            if(Paper.book("images").contains(sha1)){
                byte[] bytes = Paper.book("images").read(sha1);
                GlideApp.with(container.getContext())
                        .load(bytes)
                        .into(container);
            }
        }
    }
}
