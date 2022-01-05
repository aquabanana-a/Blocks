package com.fromfinalform.blocks.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.MvpView

@InjectViewState
class MainPresenter : MvpPresenter<MainPresenter.MainView>() {

    interface MainView : MvpView {
    }
}