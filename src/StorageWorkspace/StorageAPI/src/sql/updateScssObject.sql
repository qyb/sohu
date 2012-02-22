
    update scss_object set 
        `id`=?,
        `key`=?,
        `bfs_file`=?,
        `owner_id`=?,
        `bucket_id`=?,
        `meta`=?,
        `size`=?,
        `media_type`=?,
        `version_enabled`=?,
        `version`=?,
        `deleted`=?,
        `expiration_time`=?,
        `create_time`=?,
        `modify_time` =? 
    where  `id`=?