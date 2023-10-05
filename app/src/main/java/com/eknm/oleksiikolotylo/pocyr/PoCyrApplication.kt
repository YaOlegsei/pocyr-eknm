package com.eknm.oleksiikolotylo.pocyr

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PoCyrApplication @Inject constructor() : Application()