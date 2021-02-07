package com.bugu.things.storage.bean

/**
 * Author by xpl, Date on 2021/2/5.
 */

data class Person<T>(val name: String, val pet: T)
data class Pet(val name: String)