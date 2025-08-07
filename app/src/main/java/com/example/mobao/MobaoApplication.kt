// src/main/java/com/example/mobao/MobaoApplication.kt
package com.example.mobao

import android.app.Application
import dagger.hilt.android.HiltAndroidApp // 이 import가 필요합니다.

@HiltAndroidApp // 이 애노테이션이 필수입니다.
class MobaoApplication : Application() {
    // 이 클래스 내부에 특별한 코드가 없어도 됩니다.
}