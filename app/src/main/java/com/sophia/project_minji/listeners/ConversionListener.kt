package com.sophia.project_minji.listeners

import com.sophia.project_minji.dataclass.User

interface ConversionListener {
    fun onConversionClicked(user: User)
}