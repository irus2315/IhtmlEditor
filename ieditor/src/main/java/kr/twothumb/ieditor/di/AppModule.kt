package kr.twothumb.ieditor.di

import kr.twothumb.ieditor.ui.HtmlEditorViewModel
import kr.twothumb.ieditor.ui.imagepicker.ImagePickerViewModel
import org.koin.dsl.module

val appModule = module {
    single{ HtmlEditorViewModel() }
    single{ ImagePickerViewModel() }
}

