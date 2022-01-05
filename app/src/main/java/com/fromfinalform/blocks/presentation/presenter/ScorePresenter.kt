package com.fromfinalform.blocks.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.MvpView

@InjectViewState
class ScorePresenter : MvpPresenter<ScorePresenter.ScoreView>() {

    interface ScoreView : MvpView {
    }
}