package ru.stolexiy.catman.data.datasource.local.model

sealed interface Tables {
    object Categories : Tables {
        const val NAME = "categories"

        object Fields {
            const val ID = "category_id"
            const val NAME = "category_name"
            const val COLOR = "category_color"
            const val DESCRIPTION = "category_description"
            const val PART_OF_SPENT_TIME = "category_part_of_spent_time"
        }
    }

    object Purposes : Tables {
        const val NAME = "purposes"

        object Fields {
            const val ID = "purpose_id"
            const val NAME = "purpose_name"
            const val CATEGORY_ID = "purpose_category_id"
            const val DEADLINE = "purpose_deadline"
            const val DESCRIPTION = "purpose_description"
            const val PRIORITY = "purpose_priority"
            const val IS_FINISHED = "purpose_is_finished"
            const val PROGRESS = "purpose_progress"
        }
    }

    object Tasks : Tables {
        const val NAME = "tasks"

        object Fields {
            const val ID = "task_id"
            const val NAME = "task_name"
            const val DEADLINE = "task_deadline"
            const val PRIORITY = "task_priority"
            const val START_TIME = "task_start_time"
            const val COMPLETION_TIME = "task_completion_time"
            const val ACTUAL_TIME_COSTS = "task_actual_time_costs"
            const val DESCRIPTION = "task_description"
            const val PURPOSE_ID = "task_purpose_id"
            const val IS_FINISHED = "tasks_is_finished"
            const val PLAN_TIME_COSTS = "tasks_plan_time_costs"
        }
    }
}
