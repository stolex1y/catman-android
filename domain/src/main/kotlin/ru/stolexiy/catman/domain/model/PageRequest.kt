package ru.stolexiy.catman.domain.model

import java.io.Serializable

class PageRequest<T>(val page: Int, val size: Int, val sort: ru.stolexiy.catman.domain.model.Sort<T>) : Serializable
