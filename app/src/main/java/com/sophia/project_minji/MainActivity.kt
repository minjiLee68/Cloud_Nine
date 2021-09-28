package com.sophia.project_minji

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sophia.project_minji.databinding.ActivityMainBinding
import com.sophia.project_minji.fragment.CalendarFragment
import com.sophia.project_minji.fragment.MyPageFragment
import com.sophia.project_minji.fragment.StudentListFragment

private const val TAG_STLIST_FRAGGMENT = "stListFragment"
private const val TAG_CALENDAR_FRAGMENT = "calendarFragment"
private const val TAG_MYPAGE_FRAGMENT = "mypageFragment"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val stListFragment = StudentListFragment()
    private val calendarFragment = CalendarFragment()
    private val mypageFragment = MyPageFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationBar()
    }

    private fun initNavigationBar() {
        binding.bottomNav.run {
           setOnItemSelectedListener { id ->
               when(id.itemId) {
                   R.id.student -> {
                       replaceFragment(TAG_STLIST_FRAGGMENT,stListFragment)
                   }
                   R.id.calendar -> {
                       replaceFragment(TAG_CALENDAR_FRAGMENT,calendarFragment)
                   }
                   R.id.mypage -> {
                       replaceFragment(TAG_MYPAGE_FRAGMENT,mypageFragment)
                   }
               }
               true
           }
            selectedItemId = R.id.student
        }
    }



    /** 없으면 add로 붙이고 이미 존재한다면 show와 hide를 이용해서 fragment를 관리 한다.
     * 이렇게 하지않고 fragment 이동시에 replace로만 교체해주면 호출이 될 때마다 fragment의 상태가
     * 유지 되지 않고 새로 만들어진다.**/

    //fragment state 유지 함수
    private fun replaceFragment(tag: String,fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (fragmentManager.findFragmentByTag(tag) == null) {
            fragmentTransaction.add(R.id.fragmenMain,fragment,tag)
        }

        val list = fragmentManager.findFragmentByTag(TAG_STLIST_FRAGGMENT)
        val calendar = fragmentManager.findFragmentByTag(TAG_CALENDAR_FRAGMENT)
        val mypage = fragmentManager.findFragmentByTag(TAG_MYPAGE_FRAGMENT)

        if (list != null) {
            fragmentTransaction.hide(list)
        }
        if (calendar != null) {
            fragmentTransaction.hide(calendar)
        }
        if (mypage != null) {
            fragmentTransaction.hide(mypage)
        }

        if (tag == TAG_STLIST_FRAGGMENT) {
            if (list != null) {
                fragmentTransaction.show(list)
            }
        }

        if (tag == TAG_CALENDAR_FRAGMENT) {
            if (calendar != null) {
                fragmentTransaction.show(calendar)
            }
        }

        if (tag == TAG_MYPAGE_FRAGMENT) {
            if (mypage != null) {
                fragmentTransaction.show(mypage)
            }
        }
        fragmentTransaction.commitAllowingStateLoss()
//        fragmentTransaction.add(binding.fragmenMain.id, fragment).commit()
//        fragmentTransaction.addToBackStack(null)
    }
}