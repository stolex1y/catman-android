package ru.stolexiy.catman.data.datasource.local.dao

import ru.stolexiy.catman.data.datasource.local.model.Tables.Categories
import ru.stolexiy.catman.data.datasource.local.model.Tables.Purposes
import ru.stolexiy.catman.data.datasource.local.model.Tables.Tasks

const val ALL_ACTUAL_TIME_COSTS =
    "SELECT sum(t.${Tasks.Fields.ACTUAL_TIME_COSTS}) " +
            "FROM ${Tasks.NAME} as t"

const val GET_ALL_CATEGORY_TASKS =
    "SELECT t.* " +
            "FROM ${Categories.NAME} c " +
            "JOIN ${Purposes.NAME} p ON p.${Purposes.Fields.CATEGORY_ID} = c.${Categories.Fields.ID} " +
            "JOIN ${Tasks.NAME} t ON p.${Purposes.Fields.ID} = t.${Tasks.Fields.PURPOSE_ID}"

const val GET_ALL_PURPOSE_TASKS =
    "SELECT t.* " +
            "FROM ${Purposes.NAME} p " +
            "JOIN ${Tasks.NAME} t ON p.${Purposes.Fields.ID} = t.${Tasks.Fields.PURPOSE_ID}"

const val CATEGORY_ACTUAL_TIME_COSTS =
    "SELECT sum(ct.${Tasks.Fields.ACTUAL_TIME_COSTS}) " +
            "FROM ($GET_ALL_CATEGORY_TASKS) ct"

const val TASK_PROGRESS =
    """CASE 
        WHEN (${Tasks.Fields.PLAN_TIME_COSTS} = NULL) 
            THEN 0
        ELSE 
            (${Tasks.Fields.ACTUAL_TIME_COSTS} / ${Tasks.Fields.PLAN_TIME_COSTS})
    END"""

const val PURPOSE_PROGRESS =
    "(SELECT avg($TASK_PROGRESS) " +
            "FROM ($GET_ALL_PURPOSE_TASKS) pt) AS ${Purposes.Fields.PROGRESS}"

const val CATEGORY_PART_OF_SPENT_TIME =
    "(CAST(($CATEGORY_ACTUAL_TIME_COSTS) AS REAL) / ($ALL_ACTUAL_TIME_COSTS)) " +
            "AS ${Categories.Fields.PART_OF_SPENT_TIME}"

const val DYNAMIC_CATEGORY_FIELDS =
    "$CATEGORY_PART_OF_SPENT_TIME"

const val DYNAMIC_PURPOSE_FIELDS =
    "$PURPOSE_PROGRESS"
