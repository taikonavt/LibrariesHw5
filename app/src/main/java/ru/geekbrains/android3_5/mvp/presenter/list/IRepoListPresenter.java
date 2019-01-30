package ru.geekbrains.android3_5.mvp.presenter.list;

import io.reactivex.subjects.PublishSubject;
import ru.geekbrains.android3_5.mvp.view.item.RepoItemView;

public interface IRepoListPresenter
{
    PublishSubject<RepoItemView> getClickSubject();
    void bindView(RepoItemView rowView);
    int getRepoCount();
}
