
    update scss_bucket set
        `id`=?,
        `name`=?,
        `owner_id`=?,
        `expriration_enabled`=?,
        `logging_enabled`=?,
        `meta`=?,
        `deleted`=?,
        `create_time`=?,
        `modify_time` =?
    where `id`=?