package com.fromfinalform.blocks.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.presentation.presenter.ScorePresenter
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class ScoreFragment : MvpAppCompatFragment(), ScorePresenter.ScoreView {

    lateinit var vRoot: View

    @InjectPresenter
    lateinit var presenter: ScorePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vRoot = inflater.inflate(R.layout.fragment_score, container, false)

        return vRoot
    }
}