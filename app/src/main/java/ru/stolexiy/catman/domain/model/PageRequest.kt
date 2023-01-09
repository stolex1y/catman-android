package ru.stolexiy.catman.domain.model

class PageRequest<T>(val page: Int, val size: Int, val sort: Sort<T>)