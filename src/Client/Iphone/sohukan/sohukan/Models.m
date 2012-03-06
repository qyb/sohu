//
//  Article.m
//  sohukan
//
//  Created by riven on 12-2-2.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "Models.h"

@implementation Article
    @synthesize key;
    @synthesize title;
    @synthesize url;
    @synthesize download_url;
    @synthesize category;
    @synthesize create_time;
    @synthesize is_star;
    @synthesize is_read;
    @synthesize is_download;

@end

@implementation Category
    @synthesize name;
    @synthesize articles;
@end